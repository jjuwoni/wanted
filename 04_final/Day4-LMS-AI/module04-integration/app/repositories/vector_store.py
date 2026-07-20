# =====================================================================
# repositories/vector_store.py — ChromaDB 접근 계층 (langchain-chroma)
# ---------------------------------------------------------------------
# [Spring의 Repository에 해당 — 데이터 저장소의 유일한 창구]
#   서비스가 벡터 검색의 세부 문법을 몰라도 되도록, 도메인 언어로 된
#   메서드만 노출한다. Vector DB 제품을 바꿔도 이 파일만 고치면 된다.
#
# [저장소 계층의 헌법] 검색만 한다. 판단하지 않는다.
#   "이미 수강한 강좌 제외", "레벨업 보너스" 같은 비즈니스 규칙은
#   전부 services/의 몫이다. (관심사 분리 — 협업 시 담당 경계이기도 하다)
#
# 임베딩 모델 로딩은 수 초가 걸리므로 init()은 앱 시작 시(lifespan)
# 1번만 호출한다 — Day 1부터 지켜온 원칙.
# =====================================================================
import logging

from langchain_chroma import Chroma
from langchain_huggingface import HuggingFaceEmbeddings

from app.core.config import settings

logger = logging.getLogger(__name__)


class VectorStore:
    def __init__(self):
        self._docs: Chroma | None = None      # 학사규정 청크
        self._courses: Chroma | None = None   # 강좌 카탈로그

    def init(self) -> None:
        """임베딩 모델 + 컬렉션 연결 (앱 시작 시 1번, main.py lifespan).
        데이터를 넣는 게 아니라 ingestion 배치가 만들어둔 컬렉션에 '접속'한다."""
        logger.info("VectorStore 초기화: 모델=%s", settings.embedding_model)
        embeddings = HuggingFaceEmbeddings(model_name=settings.embedding_model)
        self._docs = Chroma(
            collection_name=settings.doc_collection,
            persist_directory=settings.chroma_path,
            embedding_function=embeddings)
        self._courses = Chroma(
            collection_name=settings.course_collection,
            persist_directory=settings.chroma_path,
            embedding_function=embeddings)
        logger.info("VectorStore 준비 완료 (규정 %d청크 / 강좌 %d건)",
                    *self.counts().values())

    # ------------------------------------------------------------------
    # 학사규정 (RAG용)
    # ------------------------------------------------------------------
    def search_regulation(self, query: str, top_k: int = 3) -> list[dict]:
        """질문과 관련된 규정 청크를 threshold 필터 후 반환한다.
        ※ langchain-chroma의 score는 '거리'다 (Day 2). 유사도 = 1 - 거리."""
        hits = self._docs.similarity_search_with_score(query, k=top_k)
        chunks = [
            {"page": doc.metadata["page"], "content": doc.page_content,
             "similarity": round(1 - dist, 4)}
            for doc, dist in hits
            if (1 - dist) >= settings.similarity_threshold
        ]
        logger.info("규정 검색: '%s' → %d개 청크", query[:30], len(chunks))
        return chunks

    # ------------------------------------------------------------------
    # 강좌 카탈로그 (추천용)
    # ------------------------------------------------------------------
    def get_course(self, code: str) -> dict | None:
        """강좌 코드(= Chroma id = MySQL course.code)로 문서 1건 조회."""
        res = self._courses.get(ids=[code])
        if not res["ids"]:
            return None
        return {"code": code, "document": res["documents"][0], **res["metadatas"][0]}

    def similar_courses(self, seed_document: str, n: int) -> list[dict]:
        """기준 문서와 유사한 강좌 후보 n개 (순수 벡터 유사도만!).
        ingestion이 metadata에 code를 심어둔 덕분에 결과에서 코드를 바로 안다."""
        hits = self._courses.similarity_search_with_score(seed_document, k=n)
        return [
            {"code": d.metadata["code"], "title": d.metadata["title"],
             "category": d.metadata["category"], "level": d.metadata["level"],
             "similarity": round(1 - dist, 4)}
            for d, dist in hits
        ]

    def counts(self) -> dict:
        return {"doc_chunks": self._docs._collection.count(),
                "courses": self._courses._collection.count()}


# 앱 전체가 공유하는 인스턴스 (Spring의 싱글톤 빈에 해당)
vector_store = VectorStore()
