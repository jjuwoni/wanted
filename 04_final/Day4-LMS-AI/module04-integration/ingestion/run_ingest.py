# =====================================================================
# ingestion/run_ingest.py — 색인 배치의 진입점
# ---------------------------------------------------------------------
# [실행 방법 — 모듈 루트에서]
#   python -m ingestion.run_ingest                # 최초 전체 색인 (bulk)
#   python -m ingestion.run_ingest --incremental  # 변경분만 (증분)
#
# [Embedding 전략의 실행부 — README 5장과 1:1 대응]
#   강좌: 1강좌 = 1문서, page_content = "제목 - 설명"
#         id = course.code  ← MySQL과 ChromaDB를 잇는 유일 키!
#         metadata = code/title/category/level (검색 결과에서 바로 사용)
#   규정: 페이지 → 500/50 청킹, id = chunk_{i}, metadata = page
#
# [증분 색인의 원리]
#   1. 마지막 실행 시각을 .ingest_state.json에 기록해 둔다
#   2. 다음 실행 때 MySQL에서 updated_at > 마지막시각 인 강좌만 읽는다
#   3. 같은 id(code)로 add하면 Chroma가 '덮어쓴다'(upsert) → 중복 없음
#   실습: UPDATE course SET description='...' WHERE code='C003'; 후
#         --incremental 실행 → "강좌 1건"만 다시 임베딩되는 것을 관찰!
# =====================================================================
import argparse
import json
import logging
import sys
from datetime import datetime
from pathlib import Path

# 스크립트를 어디서 실행해도 app 패키지를 찾도록 루트를 경로에 추가
ROOT = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(ROOT))

from langchain_chroma import Chroma
from langchain_core.documents import Document
from langchain_huggingface import HuggingFaceEmbeddings

from app.core.config import settings
from app.core.logging import setup_logging
from ingestion.mysql_reader import fetch_courses
from ingestion.pdf_loader import load_regulation_chunks

logger = logging.getLogger(__name__)

STATE_FILE = ROOT / ".ingest_state.json"   # 마지막 색인 시각 저장소


def _load_last_run() -> datetime | None:
    if STATE_FILE.exists():
        return datetime.fromisoformat(json.loads(STATE_FILE.read_text())["last_run"])
    return None


def _save_last_run(when: datetime) -> None:
    STATE_FILE.write_text(json.dumps({"last_run": when.isoformat()}))


def _course_to_document(c: dict) -> Document:
    """강좌 dict → 임베딩용 Document.
    제목만은 정보가 부족하고 설명만은 키워드를 놓친다 → "제목 - 설명" 결합.
    (Day 1에서 실험으로 확인한 전략)"""
    return Document(
        page_content=f"{c['title']} - {c['description']}",
        metadata={"code": c["code"], "title": c["title"],
                  "category": c["category"], "level": c["level"]},
    )


def main() -> None:
    setup_logging()   # 배치도 앱과 같은 로그 형식 — 운영에서 한 눈에 읽힌다
    parser = argparse.ArgumentParser(description="LMS Vector DB 색인 배치")
    parser.add_argument("--incremental", action="store_true",
                        help="변경분만 색인 (updated_at 기반)")
    args = parser.parse_args()

    started = datetime.now()
    embeddings = HuggingFaceEmbeddings(model_name=settings.embedding_model)

    # --- 강좌 카탈로그 (MySQL → Chroma) ---
    since = _load_last_run() if args.incremental else None
    if args.incremental and since is None:
        logger.warning("증분 기록이 없어 전체 색인으로 전환합니다 (최초 실행?)")

    courses = fetch_courses(since=since)
    courses_vs = Chroma(
        collection_name=settings.course_collection,
        persist_directory=settings.chroma_path,
        embedding_function=embeddings,
        collection_metadata={"hnsw:space": "cosine"},
    )
    if courses:
        docs = [_course_to_document(c) for c in courses]
        # ids를 code로 지정 → 같은 강좌는 덮어쓰기(upsert). 재실행 안전!
        courses_vs.add_documents(docs, ids=[c["code"] for c in courses])
        logger.info("강좌 색인: %d건 %s", len(courses),
                    "(증분)" if since else "(전체)")
    else:
        logger.info("변경된 강좌 없음 — 색인 생략")

    # --- 학사규정 PDF (증분 대상 아님 — 규정은 개정 시 전체 재색인) ---
    if not args.incremental:
        chunks = load_regulation_chunks(ROOT / "data" / "lms_regulation.pdf")
        docs_vs = Chroma(
            collection_name=settings.doc_collection,
            persist_directory=settings.chroma_path,
            embedding_function=embeddings,
            collection_metadata={"hnsw:space": "cosine"},
        )
        docs_vs.add_documents(chunks, ids=[f"chunk_{i}" for i in range(len(chunks))])
        logger.info("규정 색인: %d청크", len(chunks))

    _save_last_run(started)
    logger.info("색인 완료. 다음 증분 기준 시각: %s", started.isoformat())

    # ------------------------------------------------------------------
    # TODO(팀 과제 - 심화): '삭제 동기화' 구현하기
    #   MySQL에서 지워진 강좌가 Chroma에는 남아 유령 추천이 될 수 있다.
    #   힌트: MySQL의 전체 code 집합과 courses_vs.get()["ids"]를 비교해
    #         차집합을 courses_vs.delete(ids=[...])로 제거.
    # ------------------------------------------------------------------


if __name__ == "__main__":
    main()
