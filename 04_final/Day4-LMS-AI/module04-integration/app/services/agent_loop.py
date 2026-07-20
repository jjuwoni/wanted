
# =====================================================================
# services/agent_loop.py — Function Calling 루프 (공용 부품)
# ---------------------------------------------------------------------
# ★ 출신 성분: Day 3의 01_fc_basic.py에서 '손으로' 만든 바로 그 루프다.
#   학습용 20줄이 그대로 실무 부품으로 승격됐다 — 원리를 아는 코드는
#   버려지지 않는다. (수업 전체를 관통하는 메시지!)
#
# [루프가 곧 통제 장치인 이유 — Day 3 복습]
#   모델은 함수를 실행하지 못한다. tool_calls(JSON)로 '요청'할 뿐이고,
#   실행은 이 루프 안, 즉 우리 서버에서만 일어난다.
#   tools_by_name에 없는 도구는 모델에게 존재하지 않는다(화이트리스트).
# =====================================================================

import json
import logging
from typing import Any

from langchain_core.messages import BaseMessage, ToolMessage


logger = logging.getLogger(__name__)

MAX_TURNS = 5  # 무한루프 방지 (호출 1번 = 돈!)


def extract_text_content(content: Any) -> str:
    """
    Gemini/LangChain의 AIMessage.content를 문자열로 변환한다.

    모델과 LangChain 버전에 따라 content는 다음 형태로 반환될 수 있다.

    1. 일반 문자열
       "출석 기준은 다음과 같습니다."

    2. 콘텐츠 블록 리스트
       [
           {
               "type": "text",
               "text": "출석 기준은 다음과 같습니다."
           }
       ]

    FastAPI 응답 스키마의 answer는 str 타입이므로,
    여기에서 항상 일반 문자열로 정규화한다.
    """

    # 일반적인 문자열 응답
    if isinstance(content, str):
        return content.strip()

    # Gemini가 콘텐츠 블록 리스트로 반환한 경우
    if isinstance(content, list):
        text_parts: list[str] = []

        for item in content:
            # 예:
            # {
            #     "type": "text",
            #     "text": "출석 기준은 ..."
            # }
            if isinstance(item, dict):
                text = item.get("text")

                if isinstance(text, str) and text.strip():
                    text_parts.append(text.strip())

            # 리스트 안에 문자열이 직접 포함된 경우
            elif isinstance(item, str) and item.strip():
                text_parts.append(item.strip())

        return "\n".join(text_parts).strip()

    # None 처리
    if content is None:
        return ""

    # 예상하지 못한 형태가 들어온 경우의 방어 처리
    return str(content).strip()


def run_tool_loop(
    llm_with_tools,
    tools_by_name: dict,
    messages: list[BaseMessage],
) -> tuple[str, list[str]]:
    """
    [요청 → 실행 → 반납 → 답변] 루프를 돌리고
    (최종 답변, 사용된 도구 이름 목록)을 반환한다.

    used_tools를 여기서 공짜로 얻는다.
    도구를 '우리가' 실행하므로 실행하면서 이름만 적으면 된다.
    이는 Function Calling 라우팅을 확인하는 관측 정보가 된다.
    """

    used_tools: list[str] = []

    for turn in range(MAX_TURNS):
        ai_msg = llm_with_tools.invoke(messages)

        # tool_calls가 없다
        # = 모델이 더 이상 도구를 요청하지 않고 최종 답변을 생성했다.
        if not ai_msg.tool_calls:
            answer = extract_text_content(ai_msg.content)

            # 콘텐츠 블록에서 텍스트를 추출하지 못한 경우
            if not answer:
                logger.warning(
                    "AI 최종 응답에서 텍스트를 추출하지 못했습니다. "
                    "content_type=%s, content=%r",
                    type(ai_msg.content).__name__,
                    ai_msg.content,
                )

                answer = "(응답 없음)"

            logger.debug(
                "AI 최종 응답 변환 완료: 원본 타입=%s, 답변 길이=%d",
                type(ai_msg.content).__name__,
                len(answer),
            )

            return answer, used_tools

        # 모델의 도구 호출 요청을 메시지 이력에 먼저 추가한다.
        #
        # 이 메시지가 누락되면 이후 ToolMessage만 전달되기 때문에
        # 모델은 자신이 어떤 도구를 요청했는지 알 수 없게 된다.
        messages.append(ai_msg)

        for tc in ai_msg.tool_calls:
            name = tc["name"]
            args = tc["args"]

            logger.info(
                "도구 실행 [%d회차]: %s(%s)",
                turn + 1,
                name,
                args,
            )

            used_tools.append(name)

            # 화이트리스트에서 도구를 조회한다.
            tool = tools_by_name.get(name)

            # 도구가 에러 dict를 반환하거나 예외를 발생시켜도
            # 전체 Function Calling 루프는 중단하지 않는다.
            #
            # 에러를 ToolMessage 데이터로 모델에게 전달해
            # 최종 자연어 답변으로 수습하도록 한다.
            try:
                if tool:
                    result = tool.invoke(args)
                else:
                    result = {
                        "error": f"알 수 없는 도구: {name}"
                    }

            except Exception as e:
                logger.error(
                    "도구 실행 실패: tool=%s, error=%s",
                    name,
                    e,
                    exc_info=True,
                )

                result = {
                    "error": f"도구 실행 중 오류가 발생했습니다: {name}"
                }

            # 도구 실행 결과를 Gemini가 다시 읽을 수 있도록
            # JSON 문자열 형태의 ToolMessage로 전달한다.
            messages.append(
                ToolMessage(
                    content=json.dumps(
                        result,
                        ensure_ascii=False,
                        default=str,
                    ),
                    tool_call_id=tc["id"],
                )
            )

    logger.warning(
        "도구 호출 %d회 초과로 루프 중단",
        MAX_TURNS,
    )

    return (
        "(도구 호출이 반복 상한을 초과해 중단했습니다)",
        used_tools,
    )
