# =====================================================================
# core/config.py — 설정의 단일 출처 (Spring의 application.yml 역할)
# ---------------------------------------------------------------------
# pydantic-settings의 BaseSettings는 환경변수/.env를 읽어
# '타입이 검증된' 설정 객체를 만든다.
#   - GOOGLE_API_KEY 환경변수 → google_api_key 필드 (자동 매핑)
#   - 필수값이 없으면 서버가 '시작 시점에' 즉시 죽는다 (fail-fast)
#     → 운영 중에 터지는 것보다 백배 낫다.
# 설정을 코드 곳곳에 os.getenv로 흩뿌리지 말고 여기 한 곳에 모은다.
# ★ 주의: pydantic-settings는 .env를 '이 객체'에만 담고 os.environ에는
#   넣지 않는다. 그래서 모든 곳에서 키를 settings.google_api_key로
#   '명시적으로' 전달한다 (clients/llm.py 참고). 자동 탐색 의존 금지!
# =====================================================================
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")

    # --- 외부 연동 ---
    google_api_key: str                                  # 필수 (없으면 기동 실패)
    spring_base_url: str = "http://localhost:8080"
    spring_timeout_seconds: float = 5.0                  # API 계약서 2장

    # --- 색인 배치 전용: MySQL 읽기 전용 계정 (db/init.sql의 lms_reader) ---
    # ★ 온라인 요청은 절대 이 경로를 쓰지 않는다! 실시간 조회는 Spring API로.
    #   (두 경로의 구분은 README 4장 — 이번 수업의 핵심 설계 결정)
    mysql_url: str = "mysql+pymysql://lms_reader:reader1234!@localhost:3306/lmsdb"

    # --- AI 모델 ---
    gemini_model: str = "gemini-3.5-flash"
    embedding_model: str = "jhgan/ko-sroberta-multitask" # 색인과 검색은 반드시 동일!

    # --- Vector DB ---
    chroma_path: str = "./chroma_db"
    doc_collection: str = "lms_regulation"
    course_collection: str = "lms_courses"
    similarity_threshold: float = 0.35   # RAG 환각 방지 1차 방어선 (Day 2)

    # --- 추천 파이프라인 튜닝 상수 (README 5장의 점수식과 1:1 대응) ---
    # 팀별 튜닝 실습 대상! 값을 바꾸면 추천 순위가 어떻게 변하는지 관찰할 것.
    per_seed_candidates: int = 5      # 이력 강좌 1개당 뽑을 후보 수
    level_up_bonus: float = 0.15      # 같은 카테고리 + 한 단계 위 레벨 보너스
    completed_weight: float = 1.0     # 수료 강좌의 이력 가중치

    # 레벨 서열 — 레벨업 판정("입문 수료 → 중급 추천")에 사용
    level_ranks: dict[str, int] = {"입문": 1, "중급": 2, "고급": 3}


settings = Settings()   # 앱 전체가 공유하는 싱글톤
