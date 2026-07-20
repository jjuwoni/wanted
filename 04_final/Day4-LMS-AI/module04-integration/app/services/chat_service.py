# =====================================================================
# services/chat_service.py — RAG 챗봇 (도구 5개를 든 라우팅 챗봇)
# ---------------------------------------------------------------------
# Day 3의 03_router_chat_api.py가 '계층 구조'로 이사온 것.
# 이 파일은 오케스트레이션(도구 구성 + 루프 실행)만 담당하고, 일은
# 아래 계층이 한다 — 협업 시 이 경계가 곧 담당 경계다:
#   규정 검색   → repositories.vector_store     (RAG의 R)
#   학사 조회   → clients.spring_client          (Function Calling)
#   추천 후보   → services.recommend_service     (서비스 간 협력, ①~③만)
#   FC 루프    → services.agent_loop             (Day 3 수제 루프의 승격판)
# =====================================================================
import logging

from langchain_core.messages import AIMessage, HumanMessage, SystemMessage
from langchain_core.tools import tool

from app.clients import spring_client
from app.clients.llm import build_llm
from app.core.exceptions import AppException, UpstreamException
from app.repositories.vector_store import vector_store
from app.schemas.chat import ChatMessage
from app.services import recommend_service
from app.services.agent_loop import run_tool_loop

logger = logging.getLogger(__name__)


# ----------------------------------------------------------------------
# 도구 1: 학사규정 검색 — RAG의 '검색부'가 도구가 되었다 (에이전틱 RAG)
# ----------------------------------------------------------------------
# Day 2와의 차이: Day 2는 모든 질문에 무조건 검색부터 했다(고정 체인).
# 지금은 모델이 "규정 질문이네" 판단할 때만 이 도구를 부른다.
# '증강(A)'은? 결과가 ToolMessage로 대화에 반납되는 것 자체가 증강이다.
@tool
def search_regulation(query: str) -> dict:
    """LMS 학사규정 문서에서 관련 조항을 검색한다.
    출석, 과제 제출, 성적 평가, 수료 조건, 환불, 표절, 시험 등
    '규정/제도/기준'에 대한 질문에 사용한다.
    개별 학생의 데이터(내 진도율 등)는 이 함수로 알 수 없다."""
    # ↑ 마지막 줄 = 부정 문구. 지우면 라우팅이 흔들린다 (Day 3 실습 2)
    chunks = vector_store.search_regulation(query)
    if not chunks:
        return {"result": "규정에서 관련 내용을 찾지 못했습니다."}
    return {"regulations": chunks,
            "instruction": "이 조항만 근거로 답하고 (출처: 학사규정 p.N)을 표기할 것"}


# ----------------------------------------------------------------------
# 도구 5: 강좌 추천 — recommend_service의 ①~③만 빌려온다
#   (④ 설명 생성은 챗봇 모델이 대화 맥락에서 직접 — 중첩 LLM 호출 방지)
# ----------------------------------------------------------------------
@tool
def recommend_next_courses(student_id: int) -> dict:
    """학생의 수강 이력 전체를 바탕으로 다음에 들으면 좋은 강좌를 추천한다.
    '다음에 뭐 듣지', '추천해줘', '뭘 배우면 좋을까' 질문에 사용한다."""
    try:
        return recommend_service.recommend_candidates(student_id)
    except AppException as e:
        # 도구 세계의 규칙: 예외 대신 '에러 데이터' (루프를 지키기 위해 — Day 3)
        return {"error": e.detail}


# 도구 2~4: Spring 조회 함수를 도구로 변환.
# tool()은 함수의 타입 힌트 → 파라미터 명세, docstring → 도구 설명서로
# 자동 변환한다. docstring 원문은 clients/spring_client.py에 있다 —
# 도구 설명(=라우팅 품질)의 소유권도 그 파일 담당자에게 있는 셈.
TOOLS = [
    search_regulation,
    tool(spring_client.get_my_courses),
    tool(spring_client.get_assignments_due),
    tool(spring_client.get_my_summary),
    recommend_next_courses,
]
TOOLS_BY_NAME = {t.name: t for t in TOOLS}

# 도구를 묶은 모델 — 모듈 로드 시 1번만 조립 (요청마다 bind하면 낭비)
_llm_with_tools = build_llm(temperature=0.2).bind_tools(TOOLS)


def answer(question: str, student_id: int,
           history: list[ChatMessage]) -> tuple[str, list[str]]:
    """통합 챗봇의 본체. (답변, 사용된 도구 목록)을 반환한다.

    "출석 기준?"      → search_regulation          (RAG 경로)
    "내 진도율?"      → get_my_courses              (Spring→MySQL 경로)
    "다음에 뭐 듣지?" → recommend_next_courses      (추천 파이프라인)
    "공부 팁"         → 도구 없이 바로 답변
    """
    system = SystemMessage(
        "너는 스마트러닝 아카데미의 LMS 학습 도우미다. "
        # 인자 통제는 프롬프트로 하는 '소프트'한 통제다. 하드한 경계는
        # TOOLS 목록뿐 — Day 3 심화 주석("모델이 넣은 인자를 믿지 말라") 참고
        f"현재 로그인한 학생의 ID는 {student_id}이다. "
        "규정/제도 질문은 search_regulation으로 근거를 찾아 출처와 함께 답하고, "
        "학생 개인 데이터는 반드시 도구로 조회해라. "
        "도구 결과에 없는 내용은 지어내지 말고, "
        "도구가 error를 반환하면 상황을 정중히 설명해라."
    )

    # 대화 이력 → LangChain 메시지 (서버는 무상태 — 이력은 Spring이 보관)
    messages: list = [system]
    for m in history:
        messages.append(HumanMessage(m.content) if m.role == "user"
                        else AIMessage(m.content))
    messages.append(HumanMessage(question))

    logger.info("챗봇 질문 (student_id=%d): %s", student_id, question[:50])
    try:
        answer_text, used_tools = run_tool_loop(_llm_with_tools, TOOLS_BY_NAME, messages)
    except Exception as e:
        # Gemini 자체 장애(키/쿼터/네트워크)를 502 계약 응답으로 승격 →
        # Spring AiClientService가 같은 rid로 로그를 남기고 정중히 수습한다
        logger.error("LLM 호출 실패: %s", e, exc_info=True)
        raise UpstreamException("AI 모델 호출에 실패했습니다.")

    logger.info("챗봇 응답 완료 used_tools=%s", used_tools)
    return answer_text, used_tools

# ----------------------------------------------------------------------
# TODO(팀 과제): ChatResponse에 sources(근거 페이지) 추가하기
#   힌트: run_tool_loop가 도구 결과를 직접 다루므로, search_regulation의
#   결과(chunks)를 모아 반환하도록 agent_loop를 확장하면 된다.
#   계약서 v1.3으로 문서 갱신 → schemas → Spring DTO 순서로 전파할 것!
# ----------------------------------------------------------------------
