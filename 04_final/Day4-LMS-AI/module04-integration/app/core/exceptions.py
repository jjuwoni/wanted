# =====================================================================
# core/exceptions.py — 예외를 '계약된 형식'으로 변환하는 곳
# ---------------------------------------------------------------------
# [Spring의 @RestControllerAdvice에 해당하는 계층]
#
# 에러 응답 계약 (v1.1 — Day 4에서 request_id 추가):
#   {"detail": "사람이 읽을 메시지", "request_id": "a1b2c3d4"}
#   - Spring GlobalExceptionHandler도 같은 형식으로 응답한다.
#   - request_id가 에러 본문에 있으므로, Postman에서 에러를 받으면
#     그 ID로 양쪽 서버 로그를 바로 찾아갈 수 있다. (운영의 기본기!)
#
# 예외 처리 원칙:
#   1. 서비스/클라이언트 계층은 '의미 있는 예외'를 던진다 (AppException)
#   2. 라우터는 try/except를 쓰지 않는다 — 전역 핸들러가 다 받는다
#      (라우터마다 try/except 도배 = Spring에서 컨트롤러마다
#       try/catch 도배하던 그 나쁜 냄새와 같다)
#   3. 예상 못한 예외(버그)는 500 + '스택트레이스는 로그에만'.
#      클라이언트에게 내부 구조를 노출하지 않는다.
# =====================================================================
import logging

from fastapi import FastAPI, Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse

from app.core.logging import get_request_id

logger = logging.getLogger(__name__)


class AppException(Exception):
    """우리 서비스의 '예상된' 실패를 표현하는 기본 예외.

    Spring에서 EntityNotFoundException 등을 던지고 Advice가 받듯,
    서비스 계층은 이 예외(또는 자식)를 던지고 아래 핸들러가 받는다.
    """

    def __init__(self, status_code: int, detail: str):
        self.status_code = status_code
        self.detail = detail
        super().__init__(detail)


class NotFoundException(AppException):
    def __init__(self, detail: str):
        super().__init__(status_code=404, detail=detail)


class UpstreamException(AppException):
    """상류 서버(Spring/Gemini) 장애를 뜻하는 예외.
    502 Bad Gateway: '내 잘못이 아니라 내가 의존하는 서버가 문제'라는
    의미의 상태 코드다. 500(내 버그)과 구분하면 장애 파악이 빨라진다."""

    def __init__(self, detail: str):
        super().__init__(status_code=502, detail=detail)


def _error_body(detail) -> dict:
    """에러 응답 본문 조립 — 계약 형식은 이 함수 한 곳에서만 만든다."""
    return {"detail": detail, "request_id": get_request_id()}


def register_exception_handlers(app: FastAPI) -> None:
    """main.py에서 앱 조립 시 1번 호출한다."""

    @app.exception_handler(AppException)
    async def handle_app_exception(request: Request, exc: AppException):
        # 예상된 실패: WARNING 레벨 (스택트레이스 불필요 — 시끄럽기만 하다)
        logger.warning("처리된 예외 %s: %s", exc.status_code, exc.detail)
        return JSONResponse(status_code=exc.status_code,
                            content=_error_body(exc.detail))

    @app.exception_handler(RequestValidationError)
    async def handle_validation(request: Request, exc: RequestValidationError):
        # 422: 요청 형식이 계약과 다름 (보통 호출하는 쪽 = Spring DTO 불일치!)
        logger.warning("요청 검증 실패: %s", exc.errors())
        return JSONResponse(status_code=422, content=_error_body(exc.errors()))

    @app.exception_handler(Exception)
    async def handle_unexpected(request: Request, exc: Exception):
        # 예상 못한 예외 = 버그. ERROR 레벨 + 스택트레이스는 '로그에만'.
        # exc_info=True가 스택트레이스를 로그로 보내는 스위치다.
        logger.error("처리되지 않은 예외: %s", exc, exc_info=True)
        return JSONResponse(status_code=500,
                            content=_error_body("서버 내부 오류가 발생했습니다."))
