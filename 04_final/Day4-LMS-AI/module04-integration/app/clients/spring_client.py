# =====================================================================
# clients/spring_client.py — Spring 학사 API 역호출 (구간 C)
# ---------------------------------------------------------------------
# [Spring의 AiClientService에 '대칭'되는 계층]
#   Spring: RestClient로 FastAPI 호출 + 장애 격리   (구간 B)
#   FastAPI: httpx로 Spring 호출 + 장애 격리        (구간 C, 이 파일)
#
# ★ 로깅 연결의 핵심: X-Request-ID 전파 ★
#   이 파일의 단 한 줄 — headers={REQUEST_ID_HEADER: get_request_id()} —
#   이 Spring 역호출 로그를 FastAPI 로그와 같은 rid로 묶어준다.
#   (Spring의 RequestIdFilter가 이 헤더를 받아 MDC에 넣는다)
#
# [에러 정책 — Day 3에서 확립한 '에러의 데이터화' 유지]
#   이 함수들은 Gemini의 '도구'로 쓰인다. 예외를 던지면 FC 루프가
#   깨지므로, 실패를 {"error": ...} 데이터로 반환해 모델이 사용자에게
#   상황을 설명하게 한다. 단, 이제는 '로그도 함께' 남긴다 —
#   모델이 사용자에게 얼버무린 장애를 운영자는 로그로 정확히 봐야 한다.
# =====================================================================
import logging

import httpx

from app.core.config import settings
from app.core.logging import REQUEST_ID_HEADER, get_request_id

logger = logging.getLogger(__name__)

_TIMEOUT = httpx.Timeout(settings.spring_timeout_seconds)


def _spring_get(path: str, params: dict | None = None) -> dict:
    """Spring GET 공통 처리: request_id 전파 + 로깅 + 에러의 데이터화."""
    url = f"{settings.spring_base_url}{path}"
    logger.info("Spring 역호출 → GET %s params=%s", path, params)

    try:
        resp = httpx.get(
            url,
            params=params,
            timeout=_TIMEOUT,
            # ★ 이 한 줄이 두 서버의 로그를 잇는다 (파일 상단 주석 참고)
            headers={REQUEST_ID_HEADER: get_request_id()},
        )
        resp.raise_for_status()
        logger.info("Spring 응답 ← %s %s", resp.status_code, path)
        return resp.json()

    except httpx.ConnectError:
        logger.error("Spring 연결 실패: %s (서버 다운?)", url)
        return {"error": "학사 정보 서버(Spring)에 연결할 수 없습니다."}
    except httpx.TimeoutException:
        logger.error("Spring 응답 지연: %s (>%ss)", url, settings.spring_timeout_seconds)
        return {"error": "학사 정보 서버의 응답이 지연되고 있습니다."}
    except httpx.HTTPStatusError as e:
        # Spring 에러 본문 {"detail":..., "request_id":...} — 계약 v1.1.
        # 같은 rid이므로 이 로그 한 줄로 Spring 로그의 원인까지 찾아간다.
        logger.warning("Spring 오류 응답: %s %s body=%s",
                       e.response.status_code, path, e.response.text[:200])
        return {"error": f"학사 정보 조회 실패 (HTTP {e.response.status_code})"}


# ----------------------------------------------------------------------
# Gemini의 '도구'가 되는 함수들 — docstring이 곧 도구 설명서 (Day 3)
# ----------------------------------------------------------------------
def get_my_courses(student_id: int) -> dict:
    """학생이 수강 중인 강좌 목록과 진도율(%), 성적, 상태를 조회한다.
    '내 강좌', '수강 과목', '진도율', '성적' 질문에 사용한다."""
    return _spring_get(f"/api/students/{student_id}/courses")


def get_assignments_due(student_id: int, days: int = 7) -> dict:
    """학생의 마감 임박 과제 목록(과제명, 마감일, 제출 여부)을 조회한다.
    '과제', '마감', '숙제' 질문에 사용한다."""
    return _spring_get(f"/api/students/{student_id}/assignments/upcoming",
                       params={"days": days})


def get_my_summary(student_id: int) -> dict:
    """학생의 학습 요약(수강 수, 평균 진도율/성적)을 조회한다.
    '학습 현황', '나 잘하고 있어?' 질문에 사용한다."""
    return _spring_get(f"/api/students/{student_id}/summary")
