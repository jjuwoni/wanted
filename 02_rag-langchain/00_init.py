import csv
# pathlib.Path는 운영체제별 경로 구분자(Windows의 \\, macOS/Linux의 /)를
# 직접 신경 쓰지 않고 `/` 연산자로 경로를 조립하게 해주는 클래스다.
from pathlib import Path

import chromadb
from chromadb.utils import embedding_functions
from pypdf import PdfReader

# Day 1과 동일한 설정 (모델이 다르면 좌표계가 달라져 검색이 망가진다)
MODEL_NAME = "jhgan/ko-sroberta-multitask"
CHROMA_PATH = "./chroma_db"
COURSE_COLLECTION = "lms_courses"
DOC_COLLECTION = "lms_regulation"
# 쉼표로 여러 변수에 값을 한 번에 넣는 '다중 할당' 문법이다.
CHUNK_SIZE, CHUNK_OVERLAP = 500, 50

DATA_DIR = Path(__file__).parent / "data"
# __file__은 현재 소스 파일 위치다. 이를 기준으로 data 경로를 만들면
# 터미널을 어느 폴더에서 실행하더라도 같은 데이터 파일을 찾을 수 있다.


def chunk_text(text: str) -> list[str]:
    # text: str은 매개변수의 예상 타입, -> list[str]은 반환 타입 힌트다.
    # 실행을 강제하는 문법은 아니지만 IDE 자동완성과 정적 검사에 도움을 준다.
    if len(text) <= CHUNK_SIZE:
        return [text]
    # 빈 리스트와 시작값을 각각 chunks, start에 넣는 다중 할당이다.
    chunks, start = [], 0
    # 시작 위치를 CHUNK_SIZE가 아닌 (크기 - 겹침)만큼 이동하므로
    # 앞 청크의 마지막 50자가 다음 청크의 앞부분에 다시 포함된다.
    while start < len(text):
        # text[a:b]는 a부터 b 직전까지 잘라내는 슬라이싱이다.
        chunks.append(text[start : start + CHUNK_SIZE])
        # += 는 `start = start + ...`를 줄여 쓴 복합 대입 연산자다.
        start += CHUNK_SIZE - CHUNK_OVERLAP
    return chunks


def main() -> None:
    # -> None은 이 함수가 의미 있는 값을 반환하지 않음을 나타낸다.
    # PersistentClient는 메모리가 아니라 CHROMA_PATH 폴더에 데이터를 저장한다.
    client = chromadb.PersistentClient(path=CHROMA_PATH)
    ef = embedding_functions.SentenceTransformerEmbeddingFunction(model_name=MODEL_NAME)

    # --- 1) 강좌 카탈로그 색인 (Day 1의 02) ---
    courses_col = client.get_or_create_collection(
        name=COURSE_COLLECTION, embedding_function=ef, metadata={"hnsw:space": "cosine"}
    )
    # with 블록을 벗어나면 파일이 자동으로 닫힌다. 예외가 나도 닫히므로
    # open()/close()를 직접 짝지어 쓰는 것보다 안전한 컨텍스트 매니저 문법이다.
    with open(DATA_DIR / "courses.csv", encoding="utf-8") as f:
        courses = list(csv.DictReader(f))
    # documents, ids, metadatas의 같은 순번은 한 강좌를 나타낸다.
    # upsert는 같은 ID가 있으면 갱신하므로 스크립트를 다시 실행해도 중복되지 않는다.
    courses_col.upsert(
        # `[표현식 for 항목 in 반복가능객체]`는 반복 결과로 새 리스트를 만드는
        # 리스트 컴프리헨션이다. 아래 세 리스트의 길이와 순서는 같아야 한다.
        documents=[f"{c['title']} - {c['description']}" for c in courses],
        ids=[c["course_id"] for c in courses],
        metadatas=[
            {"title": c["title"], "category": c["category"], "level": c["level"]}
            for c in courses
        ],
    )
    print(f"강좌 색인 완료: {courses_col.count()}건")

    # --- 2) 학사규정 PDF 색인 (Day 1의 03) ---
    docs_col = client.get_or_create_collection(
        name=DOC_COLLECTION, embedding_function=ef, metadata={"hnsw:space": "cosine"}
    )
    reader = PdfReader(str(DATA_DIR / "lms_regulation.pdf"))
    documents, metadatas, ids = [], [], []
    # enumerate(..., start=1)는 요소와 함께 1부터 시작하는 순번을 돌려준다.
    for page_num, page in enumerate(reader.pages, start=1):
        # `A or ""`는 A가 None 또는 빈 값이면 빈 문자열을 선택한다.
        # strip()은 문자열 양끝의 공백과 줄바꿈을 제거한다.
        text = (page.extract_text() or "").strip()
        if not text:
            # continue는 현재 반복만 건너뛰고 다음 페이지로 이동한다.
            continue
        for i, chunk in enumerate(chunk_text(text)):
            documents.append(chunk)
            metadatas.append({"source": "LMS 학사규정", "page": page_num})
            ids.append(f"p{page_num}_c{i}")
    docs_col.upsert(documents=documents, metadatas=metadatas, ids=ids)
    print(f"규정 색인 완료: {docs_col.count()}개 청크")
    print("준비 끝! 이제 01_gemini_basic.py 부터 시작하세요.")


if __name__ == "__main__":
    # 이 파일을 직접 실행할 때만 main()을 호출한다.
    # 다른 파일에서 import할 때 색인이 자동 실행되는 것을 막는 표준 패턴이다.
    main()