# =====================================================================
# ingestion/pdf_loader.py — 학사규정 PDF → 청크 Document 리스트
# ---------------------------------------------------------------------
# Day 3의 00_ingest.py에서 만든 로직 그대로:
#   pypdf로 페이지 텍스트 추출 → LangChain Document 규격으로 조립
#   → RecursiveCharacterTextSplitter로 청킹 (size 500 / overlap 50)
# 청킹 상수의 의미와 트레이드오프는 Day 1 README 4장 참고.
# =====================================================================
import logging
from pathlib import Path

from langchain_core.documents import Document
from langchain_text_splitters import RecursiveCharacterTextSplitter
from pypdf import PdfReader

logger = logging.getLogger(__name__)

CHUNK_SIZE, CHUNK_OVERLAP = 500, 50


def load_regulation_chunks(pdf_path: Path) -> list[Document]:
    """PDF → 페이지별 Document → 청크 Document 리스트.
    metadata의 page는 챗봇의 '(출처: p.N)' 표기에 쓰인다 (Day 1부터의 원칙)."""
    reader = PdfReader(str(pdf_path))
    pages = [
        Document(page_content=t,
                 metadata={"source": "LMS 학사규정", "page": num})
        for num, page in enumerate(reader.pages, start=1)
        if (t := (page.extract_text() or "").strip())
    ]
    splitter = RecursiveCharacterTextSplitter(
        chunk_size=CHUNK_SIZE, chunk_overlap=CHUNK_OVERLAP)
    chunks = splitter.split_documents(pages)
    logger.info("PDF 청킹: %d페이지 → %d청크", len(pages), len(chunks))
    return chunks
