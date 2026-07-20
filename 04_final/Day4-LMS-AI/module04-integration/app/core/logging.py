# =====================================================================
# core/logging.py — Spring과 '이어지는' 로깅의 심장
# ---------------------------------------------------------------------
# [목표] Postman 요청 1건이 Spring → FastAPI → (Spring 역호출)을
#   오가는 동안, 세 곳의 로그가 '같은 request_id'로 묶이게 한다.
#
#   [Spring 로그]  ... [rid=a1b2c3d4] /chat 수신, AI 서버 호출
#   [FastAPI 로그] ... [rid=a1b2c3d4] /chat 수신 (Spring이 보낸 ID 이어받음)
#   [FastAPI 로그] ... [rid=a1b2c3d4] Spring 역호출: /api/students/1/courses
#   [Spring 로그]  ... [rid=a1b2c3d4] /api/students/1/courses 수신
#
#   → 장애가 나면 rid 하나로 grep 해서 전 구간을 재구성할 수 있다.
#     (분산 추적(distributed tracing)의 가장 단순한 형태다)
#
# [Spring MDC와의 대응]
#   Spring: MDC.put("requestId", ...) + logback 패턴의 %X{requestId}
#   FastAPI: ContextVar + logging.Filter        ← 이 파일
#   MDC는 스레드로컬 기반이라 async 파이썬에서 못 쓴다.
#   ContextVar가 async 환경에서 '요청별 저장소' 역할을 대신한다.
#   (요청 A와 B가 동시에 처리돼도 각자의 request_id가 섞이지 않는다!)
# =====================================================================
import logging
import sys
import uuid
from contextvars import ContextVar

# 헤더 이름 — Spring RequestIdFilter와 '철자까지' 같아야 한다 (계약!)
REQUEST_ID_HEADER = "X-Request-ID"

# 요청별 저장소. 기본값 "-"는 '요청 밖'(서버 기동 로그 등)을 뜻한다.
_request_id_var: ContextVar[str] = ContextVar("request_id", default="-")


def set_request_id(value: str | None) -> str:
    """미들웨어가 요청 시작 시 호출한다.
    Spring이 헤더로 보낸 ID가 있으면 '이어받고', 없으면(FastAPI를 직접
    호출한 경우 = Postman 직접 테스트) 새로 만든다."""
    rid = value or uuid.uuid4().hex[:8]
    _request_id_var.set(rid)
    return rid


def get_request_id() -> str:
    """현재 요청의 ID. spring_client가 역호출 헤더에 실을 때,
    예외 핸들러가 에러 응답에 넣을 때 사용한다."""
    return _request_id_var.get()


class RequestIdLogFilter(logging.Filter):
    """모든 로그 레코드에 request_id 속성을 주입하는 필터.
    덕분에 로그를 남기는 쪽은 request_id를 신경 쓸 필요가 없다 —
    logger.info("...")만 해도 포맷터가 알아서 [rid=...]를 붙인다."""

    def filter(self, record: logging.LogRecord) -> bool:
        record.request_id = get_request_id()
        return True   # True = 이 로그를 버리지 않고 통과시킨다


def setup_logging(level: int = logging.INFO) -> None:
    """앱 시작 시 1번 호출. 로그 형식을 통일한다.

    포맷을 Spring(logback-spring.xml)과 '눈으로 비교하기 좋게' 맞췄다:
      2026-07-14 10:00:00 INFO  [rid=a1b2c3d4] app.routers.chat - 질문 수신
    """
    handler = logging.StreamHandler(sys.stdout)
    handler.addFilter(RequestIdLogFilter())
    handler.setFormatter(logging.Formatter(
        fmt="%(asctime)s %(levelname)-5s [rid=%(request_id)s] %(name)s - %(message)s",
        datefmt="%Y-%m-%d %H:%M:%S",
    ))

    root = logging.getLogger()
    root.setLevel(level)
    root.handlers = [handler]          # 기본 핸들러를 우리 것으로 교체

    # uvicorn의 접근 로그(access log)에도 같은 형식/필터를 적용해서
    # "서버 로그 따로, 앱 로그 따로"가 되는 것을 막는다
    for name in ("uvicorn", "uvicorn.access", "uvicorn.error"):
        lg = logging.getLogger(name)
        lg.handlers = [handler]
        lg.propagate = False
