# =====================================================================
# main.py — 앱 '조립'만 하는 곳 (Spring의 @SpringBootApplication + 설정)
# ---------------------------------------------------------------------
# 이 파일에 로직이 없어야 정상이다. 하는 일은 네 가지 조립뿐:
#   1. 로깅 초기화             (core/logging.py)
#   2. request_id 미들웨어     ← Spring과 로그가 이어지는 입구!
#   3. lifespan (무거운 자원 1회 초기화)
#   4. 라우터 + 전역 예외 핸들러 등록
#
# 실행: uvicorn app.main:app --reload --port 8000
# =====================================================================
import logging
import time
from contextlib import asynccontextmanager

from fastapi import FastAPI, Request

from app.core.exceptions import register_exception_handlers
from app.core.logging import REQUEST_ID_HEADER, set_request_id, setup_logging
from app.repositories.vector_store import vector_store
from app.routers import chat, health, recommend

# 1) 로깅은 '무엇보다 먼저' — 이후의 모든 초기화 로그도 형식을 갖추도록
setup_logging()
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    # 임베딩 모델/컬렉션은 시작 시 1번만 (Day 1부터의 원칙)
    vector_store.init()
    logger.info("LMS AI Server 준비 완료")
    yield
    logger.info("LMS AI Server 종료")


app = FastAPI(
    title="LMS AI Server - Day 4 (통합)",
    description="RAG 챗봇 + 강의 추천 | 계층형 구조 | Spring과 로깅/예외 연동",
    lifespan=lifespan,
)


# ----------------------------------------------------------------------
# 2) request_id 미들웨어 — 모든 요청이 지나는 관문
# ----------------------------------------------------------------------
# Spring의 RequestIdFilter(OncePerRequestFilter)와 완전히 대칭이다:
#   [수신] 헤더에 X-Request-ID가 있으면 이어받고(Spring이 보낸 것),
#          없으면 새로 발급 (Postman이 FastAPI를 직접 때린 경우)
#   [보관] ContextVar에 저장 → 이후 모든 로그에 자동으로 [rid=...] 부착
#   [응답] 응답 헤더에도 실어준다 → Postman에서 rid를 눈으로 확인 가능
# ----------------------------------------------------------------------
@app.middleware("http")
async def request_id_middleware(request: Request, call_next):
    rid = set_request_id(request.headers.get(REQUEST_ID_HEADER))

    start = time.perf_counter()
    response = await call_next(request)          # 여기서 라우터/핸들러 실행
    elapsed_ms = (time.perf_counter() - start) * 1000

    response.headers[REQUEST_ID_HEADER] = rid

    # 접근 로그: 한 줄로 '무엇이 얼마나 걸려 어떻게 끝났는지'
    # LLM 요청은 수 초~수십 초가 정상이므로 처리시간 기록이 특히 중요하다
    logger.info("%s %s → %d (%.0fms)",
                request.method, request.url.path, response.status_code, elapsed_ms)
    return response


# 3) 전역 예외 핸들러 (core/exceptions.py — Spring @RestControllerAdvice 대응)
register_exception_handlers(app)

# 4) 라우터 등록 — 기능 추가 시 여기 한 줄 + routers/ 파일 하나가 전부
app.include_router(chat.router)
app.include_router(recommend.router)
app.include_router(health.router)
