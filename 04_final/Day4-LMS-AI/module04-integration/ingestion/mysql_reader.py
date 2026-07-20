# =====================================================================
# ingestion/mysql_reader.py — MySQL에서 색인 재료를 '읽기만' 하는 계층
# ---------------------------------------------------------------------
# ★ 두 가지 데이터 경로의 구분 (README 4장 — 이번 설계의 핵심 결정) ★
#   온라인(요청 시): "학생 1의 수강 이력" → 반드시 Spring API 경유
#     이유: 신원·비즈니스 규칙의 주인은 Spring. AI 서버가 실시간
#     조회까지 DB를 직접 만지면 규칙이 두 곳으로 흩어진다.
#   오프라인(배치): 강좌 카탈로그 전체 → MySQL 직접 SELECT (이 파일)
#     이유: 대량 조회를 API로 N번 때리는 것보다 효율적이고,
#     Spring이 꺼져 있어도 색인할 수 있다. 단, '읽기 전용' 계정으로!
#     (db/init.sql의 lms_reader — SELECT 권한만. 최소 권한 원칙)
#
# SQLAlchemy를 쓰는 이유: 커넥션 풀 + 파라미터 바인딩(SQL 인젝션 방지)을
# 표준적으로 처리해 준다. JPA처럼 ORM까지 갈 필요는 없어서(단순 SELECT
# 두 개) text 쿼리로 가볍게 쓴다 — 도구는 문제 크기에 맞게!
# =====================================================================
import logging
from datetime import datetime

from sqlalchemy import create_engine, text

from app.core.config import settings

logger = logging.getLogger(__name__)

# 엔진은 모듈에 1개 — 내부에 커넥션 풀을 들고 있다 (매번 새 연결 금지)
# pool_pre_ping: 끊어진 커넥션을 자동 감지·교체 (MySQL 유휴 타임아웃 대비)
_engine = create_engine(settings.mysql_url, pool_pre_ping=True)


def fetch_courses(since: datetime | None = None) -> list[dict]:
    """강좌 카탈로그를 읽는다.

    since=None      → 전체 조회 (최초 bulk 색인)
    since=지난 시각  → updated_at이 그 이후인 것만 (증분 색인!)
                      "강좌가 매일 바뀌면 매번 전체 재색인할 것인가?"의 답.

    파라미터는 반드시 :since 바인딩으로 — f-string으로 SQL을 조립하는
    순간 SQL 인젝션의 문이 열린다 (Spring의 PreparedStatement와 같은 원리).
    """
    sql = "SELECT code, title, description, category, level, updated_at FROM course"
    params: dict = {}
    if since is not None:
        sql += " WHERE updated_at > :since"
        params["since"] = since

    with _engine.connect() as conn:                    # with = 풀에 자동 반납
        rows = conn.execute(text(sql), params).mappings().all()

    courses = [dict(r) for r in rows]
    logger.info("MySQL 조회: 강좌 %d건 (since=%s)", len(courses), since)
    return courses
