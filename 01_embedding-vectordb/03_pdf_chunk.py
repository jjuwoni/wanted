"""
[목표]
1. LMS 규정이 담긴 사내 문서 PDF 에서 Text 를 추출한다.
2. 긴 문서를 'Chunk'로 자르는 이유와 방법에 대해 이해한다.
3. Chunk 잘린 Text 를 임베딩 후 VectorDB 에 저장하고 검색한다.

- 많은 양의 Text 를 통채로 임베딩을 하게 되면
- 1개의 벡터에 여러 주제가 섞여 유사도를 판단하기 좋지 않은 벡터값이 된다.
- 따라서 Text 를 주제 / 핵신 키워드 등을 기준으로 잘라(청킹) 각각 임베딩을
- 해야 퀄리티 있는 답변을 만들어 낼 수 있게 된다.
"""

from pathlib import Path

import chromadb
# 문서가 들어오면 지정한 모델로 임배딩 해라 라는 것을 명령한다.
from chromadb.utils import embedding_functions

# pdf 읽는 라이브러리
from pypdf import PdfReader

# STEP 1. 사용 모델, DB 경로, 테이블명, 청크 사이즈, 청크 오버랩
MODEL_NAME = "jhgan/ko-sroberta-multitask"  # 한글 문장 임베딩 모델
CHROMA_PATH = "./chroma_db"                 # 벡터가 저장될 로컬 폴더
DOC_COLLECTION = "lms_regulation"           # RDB의 '테이블'에 해당하는 개념

CHUNK_SIZE = 500            # 1개의 청크에 포함되는 최대 글자 수
CHUNK_OVERLAP = 50          # 이웃 청크끼리 겹치는 글자 수

# STEP 2. PDF -> (1~9 페이지 별) 텍스트 추출
def extract_pages(pdf_path: str) -> list[dict]:
    """PDF의 각 페이지 텍스트를 추출한다.

    페이지 번호를 함께 보관하는 이유:
      나중에 챗봇이 답변할 때 "학사규정 4페이지 참고"처럼
      '출처'를 보여주기 위해서다. (RAG 신뢰성의 핵심!)
    """
    # PdfReader는 PDF 파일을 열고 .pages를 통해 페이지 단위로 접근하게 해준다.
    # 이미지로 스캔한 PDF에는 글자 데이터가 없어 extract_text()만으로 읽을 수 없다.
    reader = PdfReader(pdf_path)
    pages = []
    # enumerate(..., start=1)는 항목과 순번을 함께 주며, 실제 페이지처럼 1부터 센다.
    for page_num, page in enumerate(reader.pages, start=1):
        text = page.extract_text() or ""    # 빈 페이지면 None 대신 빈 문자열
        text = text.strip()
        if text:                             # 표지처럼 내용 없는 페이지는 제외
            pages.append({"page": page_num, "text": text})
    print(f"PDF 추출 완료: 총 {len(reader.pages)}페이지 중 {len(pages)}페이지에 텍스트 존재")
    return pages

# STEP 3. 청킹(Chunking) - 긴 텍스트를 검색하기 좋은 크기로 자른다.
def chunk_text(text: str, size: int = CHUNK_SIZE, overlap: int = CHUNK_OVERLAP) -> list[str]:
    """텍스트를 size 글자 단위로 자르되, 이웃 청크와 overlap만큼 겹치게 한다.

    overlap(겹침)을 두는 이유:
      "출석은 진도율 90% 이상 | 인 경우 인정한다"      ← 경계에서 잘리면
      핵심 문장이 반토막 나서 어느 청크로도 검색이 안 된다.
      앞뒤 청크가 50자씩 겹치면 잘린 문장이 한쪽 청크에는 온전히 남는다.

    size를 500자로 잡은 이유 (정답이 아니라 트레이드오프다):
      - 너무 작으면(예: 100자): 문맥이 부족해 청크만 봐서는 의미를 모른다.
      - 너무 크면(예: 3000자): 여러 주제가 섞여 검색 정확도가 떨어지고,
        나중에 LLM 프롬프트에 넣을 때 토큰(=비용)도 많이 먹는다.
      - 규정 문서는 '조' 단위가 대략 200~600자라서 500자가 적당하다.

    ※ 지금은 원리를 이해하기 위해 직접 구현한다.
      내일(Day 2) LangChain의 RecursiveCharacterTextSplitter를 쓰면
      문단/문장 경계를 존중하며 더 똑똑하게 잘라준다.
    """
    if len(text) <= size:
        return [text]

    chunks = []
    start = 0
    while start < len(text):
        end = start + size
        chunks.append(text[start:end])
        # 다음 시작점 = 이번 끝 - 겹침 → 겹치는 구간이 생긴다
        start = end - overlap
    return chunks

def build_chunks(pages: list[dict]) -> tuple[list[str], list[dict], list[str]]:
    """페이지별 텍스트를 청크로 변환하고 Chroma 저장용 3종 세트를 만든다.

    반환: (documents, metadatas, ids)
      - documents: 청크 본문 (임베딩 대상)
      - metadatas: 출처 정보 {source, page}
      - ids: "p{페이지}_c{순번}" 형식의 고유 키
    """
    # 세 리스트의 같은 인덱스는 반드시 같은 청크를 가리켜야 한다.
    # 예: documents[0]의 출처는 metadatas[0], 고유 ID는 ids[0]이다.
    documents, metadatas, ids = [], [], []
    for page in pages:
        # enumerate를 사용해 한 페이지 안에서 청크 순번 i도 함께 만든다.
        for i, chunk in enumerate(chunk_text(page["text"])):
            documents.append(chunk)
            metadatas.append({"source": "LMS 학사규정", "page": page["page"]})
            ids.append(f"p{page['page']}_c{i}")
    print(f"청킹 완료: {len(pages)}페이지 → {len(documents)}개 청크")
    return documents, metadatas, ids

# STEP 4. ChromaDB 저장 및 테스트
def get_collection():
    """규정 문서용 컬렉션을 가져온다 (02와 같은 DB, 다른 컬렉션)."""
    client = chromadb.PersistentClient(path=CHROMA_PATH)
    embedding_fn = embedding_functions.SentenceTransformerEmbeddingFunction(
        model_name=MODEL_NAME
    )
    return client.get_or_create_collection(
        name=DOC_COLLECTION,
        embedding_function=embedding_fn,
        metadata={"hnsw:space": "cosine"},
    )


def ask(collection, question: str, top_k: int = 2):
    """질문과 가장 관련 있는 규정 청크를 찾아 출력한다.

      내일 RAG 챗봇은 여기서 찾은 청크를
      "이 내용을 참고해서 답변해줘"라며 Gemini에게 넘기는 것뿐이다.
      즉, 오늘 우리는 RAG의 'R(Retrieval, 검색)'을 완성한 것이다.
    """
    results = collection.query(query_texts=[question], n_results=top_k)

    print(f"\n질문: \"{question}\"")
    for doc, meta, dist in zip(
        results["documents"][0], results["metadatas"][0], results["distances"][0]
    ):
        preview = doc[:120].replace("\n", " ")
        print(f"  [유사도 {1 - dist:.4f}] ({meta['source']} p.{meta['page']})")
        print(f"    → {preview}...")

# ---실행 Main()---
if __name__ == "__main__":
    # __file__은 현재 파이썬 파일의 경로다. 실행 위치가 달라져도
    # 이 파일을 기준으로 data 폴더를 찾기 위해 Path(__file__).parent를 쓴다.
    pdf_path = Path(__file__).parent / "data" / "lms_regulation.pdf"

    # 1단계: PDF → 페이지 텍스트
    pages = extract_pages(str(pdf_path))

    # 2단계: 페이지 → 청크
    documents, metadatas, ids = build_chunks(pages)

    # 3단계: 청크 → Vector DB (upsert라 재실행해도 안전)
    collection = get_collection()
    collection.upsert(documents=documents, metadatas=metadatas, ids=ids)
    print(f"저장 완료: 컬렉션 '{DOC_COLLECTION}'에 {collection.count()}개 청크")

    # 4단계: 검색 테스트 — 규정에 '정답이 있는' 질문들
    ask(collection, "강낭콩을 기르는 방법이 뭐야?")
    ask(collection, "점심 메뉴 추천해줘")
    ask(collection, "토마토는 무슨 색일까?")
    ask(collection, "넌 이름이 뭐야?")

# 규정에 없는 질문 ex) "점심 메뉴 추천" 을 넣으면 어떻게 될까?
# 기준선 (threshold) 을 설정해서 코사인유사도 점수가 예를 들어 0.4 미만이면 답변할 수 없는 구조로 설계해야 한다.