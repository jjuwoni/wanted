# =====================================================================
# clients/llm.py — LLM 생성의 '단일 지점' (Spring의 외부 SDK 어댑터 빈)
# ---------------------------------------------------------------------
# ChatGoogleGenerativeAI 생성을 이 파일 하나에 가둬두면:
#   - 모델/키/온도 변경이 한 곳에서 끝난다
#   - 서비스 계층은 "어느 벤더의 LLM인지" 몰라도 된다
#     (Claude/GPT로 갈아타도 이 파일 + 의존성만 바꾸면 된다)
#
# ★ google_api_key를 '명시적으로' 넘기는 이유 (중요!):
#   ChatGoogleGenerativeAI는 기본적으로 os.environ에서 키를 찾지만,
#   pydantic-settings는 .env를 os.environ에 넣지 않는다 (config.py 주석).
#   자동 탐색에 기대면 "내 PC에선 되는데?" 류의 환경 의존 버그가 생긴다.
#   → 설정은 언제나 settings에서 명시적으로. (이 프로젝트의 원칙)
# =====================================================================
from langchain_google_genai import ChatGoogleGenerativeAI

from app.core.config import settings


def build_llm(temperature: float = 0.2) -> ChatGoogleGenerativeAI:
    """용도별 LLM 인스턴스 생성기.
    - 챗봇 라우팅: 0.2 (일관성이 생명 — 같은 질문엔 같은 도구)
    - 추천 이유 생성: 0.4 (약간의 표현 다양성 허용)
    """
    return ChatGoogleGenerativeAI(
        model=settings.gemini_model,
        google_api_key=settings.google_api_key,   # 명시 전달! (위 주석)
        temperature=temperature,
    )
