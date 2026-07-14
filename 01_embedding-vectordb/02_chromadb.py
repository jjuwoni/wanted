"""
[목표]
1. VectorDB 가 왜 필요한가를 이해한다.

[필요성]
- LMS 강좌 50개를 매번 요청 시마다 Embedding 을 하게 되면
- 임베딩 시에 발생하는 딜레이가 발생하게 된다.
- 따라서 최초 요청 시 임베딩 후 VectorDB 에 저장을 해두게 된다면
- 이미 있는 데이터의 경우 임베딩 할 필요가 없어진다.
"""

# csv 파일을 읽기 위한 준비
import csv
# Path 는 운영체제에 맞는 파일 경로를 만들어주는 도구이다.
from pathlib import Path

import chromadb
# 문서가 들어오면 지정한 모델로 임베딩해라 라는 것을 명령한다.
from chromadb.utils import embedding_functions

# STEP 1. 사용 모델, DB 경로, Table 명 설정
MODEL_NAME = "jhgan/ko-sroberta-multitask"  # 한글 문장 임베딩 모델
CHROMA_PATH = "./chroma_db"                 # 벡터가 저장될 로컬 폴더
COURSE_COLLECTION = "lms_courses"           # RDB의 '테이블'에 해당하는 개념


# STEP 2. Chroma DB Client 생성
# PersistentClient: 디스크에 저장 → 프로그램을 껐다 켜도 데이터 유지
# (메모리에만 두는 chromadb.Client()도 있지만 실습엔 영속화가 편하다)
client = chromadb.PersistentClient(path=CHROMA_PATH)

# ChromaDB에게 "문서가 들어오면 이 모델로 임베딩해라"라고 알려준다.
# 이렇게 등록해두면 add/query 할 때 인코딩을 Chroma가 알아서 해준다.
embedding_fn = embedding_functions.SentenceTransformerEmbeddingFunction(
    model_name=MODEL_NAME
)

# get_or_create: 없으면 만들고, 있으면 기존 것을 가져온다 (재실행 안전)
# hnsw:space="cosine": 유사도 계산 방식을 코사인으로 지정
# MySQL 에서의 DDL 구문(테이블을 생성하는 구문이라고 생각한다.)
collection = client.get_or_create_collection(
    name=COURSE_COLLECTION,
    embedding_function=embedding_fn,
    metadata={"hnsw:space": "cosine"},
)

# STEP 3. 강좌가 기록 된 CSV 읽어들이고 ChromaDB 에 저장
def load_courses(csv_path: str) -> list[dict]:
    """courses.csv를 읽어 딕셔너리 리스트로 반환한다."""
    with open(csv_path, encoding="utf-8") as f:
        # DictReader는 CSV 첫 줄의 열 이름을 키로 사용한다.
        # 따라서 각 행을 c["title"], c["category"]처럼 읽을 수 있다.
        return list(csv.DictReader(f))

def index_courses(courses: list[dict]) -> None:
    """강좌 목록을 임베딩해서 컬렉션에 저장한다."""
    # documents: 임베딩 대상 텍스트.
    #   제목만 넣으면 정보가 부족하고, 설명만 넣으면 제목 키워드를 놓친다.
    #   → "제목 - 설명"을 합쳐서 하나의 문서로 만든다. (실무 단골 패턴)
    # [표현식 for 항목 in 목록]은 리스트 컴프리헨션이다.
    # courses를 반복하면서 각 강좌에 대응하는 새 문자열 목록을 만든다.
    documents = [f"{c['title']} - {c['description']}" for c in courses]

    # ids: RDB의 PK와 같다. 같은 id로 다시 add하면 중복 저장되므로
    #      upsert를 사용한다 (있으면 갱신, 없으면 삽입 → 재실행해도 안전).
    ids = [c["course_id"] for c in courses]

    # metadatas: 벡터와 함께 저장하는 부가 정보 (RDB의 일반 컬럼 느낌).
    #   나중에 "백엔드 카테고리에서만 검색" 같은 필터링에 쓴다.
    metadatas = [
        {"title": c["title"], "category": c["category"], "level": c["level"]}
        for c in courses
    ]

    collection.upsert(documents=documents, ids=ids, metadatas=metadatas)
    print(f"색인 완료: 강좌 {collection.count()}개가 Vector DB에 저장됨")

# STEP 4. 의미 기반 검색 - 자연어로 강좌 찾기
def search_courses(query: str, top_k: int = 3, category: str | None = None):
    """자연어 질의로 강좌를 검색한다.

    내부 동작 순서 (query() 한 줄이 해주는 일):
      1. query 문장을 임베딩 모델로 벡터화     ← 질문 '1개만' 인코딩!
      2. 저장된 50개 벡터와 코사인 유사도 비교
      3. 가장 가까운 top_k개 반환
    """
    # where 필터: 메타데이터 조건 검색. SQL의 WHERE category = '백엔드' 격.
    # → "의미 검색 + 정형 필터"를 섞어 쓸 수 있는 게 Vector DB의 강점.
    where = {"category": category} if category else None

    # query_texts는 여러 질문을 한꺼번에 받을 수 있어서 리스트 형태다.
    # 지금은 질문이 하나이므로 결과도 results[...][0]에서 첫 묶음을 꺼낸다.
    results = collection.query(query_texts=[query], n_results=top_k, where=where)

    print(f"\n질의: \"{query}\"" + (f"  (카테고리={category})" if category else ""))
    # Chroma는 '거리(distance)'를 반환한다. cosine 공간에서는
    # 거리 = 1 - 코사인유사도. 즉 거리가 작을수록 비슷하다.
    # zip은 메타데이터와 거리를 같은 순번끼리 한 쌍으로 묶어 반복한다.
    for meta, dist in zip(results["metadatas"][0], results["distances"][0]):
        similarity = 1 - dist
        print(f"  [{similarity:.4f}] ({meta['category']}/{meta['level']}) {meta['title']}")
    return results

# STEP 5. 강좌 추천
def recommend_similar(course_id: str, top_k: int = 3):
    """수강한 강좌와 의미적으로 가장 가까운 강좌를 추천한다.

    아이디어: 추천 = "그 강좌 자체를 검색어로 쓰는 것"
      1. 수강한 강좌의 문서(제목-설명)를 꺼낸다
      2. 그 문서로 유사도 검색을 한다
      3. 자기 자신을 빼고 상위 top_k개를 추천
    """
    # get(): id로 문서를 조회한다 (SQL의 SELECT * WHERE id = ...)
    seed = collection.get(ids=[course_id])
    if not seed["ids"]:
        print(f"강좌 {course_id}를 찾을 수 없습니다.")
        return []

    seed_doc = seed["documents"][0]
    seed_title = seed["metadatas"][0]["title"]

    # 자기 자신이 항상 1등(유사도 1.0)으로 나오므로 +1개를 조회한 뒤 제외한다.
    results = collection.query(query_texts=[seed_doc], n_results=top_k + 1)

    print(f"\n\"{seed_title}\" 수강생에게 추천:")
    recommendations = []
    for cid, meta, dist in zip(
        results["ids"][0], results["metadatas"][0], results["distances"][0]
    ):
        if cid == course_id:        # 자기 자신은 건너뛴다
            continue
        recommendations.append({"course_id": cid, **meta, "similarity": 1 - dist})
        print(f"  [{1 - dist:.4f}] ({meta['level']}) {meta['title']}")
    return recommendations[:top_k]


#-------실행 Main()------
if __name__ == "__main__":
    csv_path = Path(__file__).parent / "data" / "courses.csv"
    courses = load_courses(str(csv_path))
    index_courses(courses)

    # --- 의미 검색 데모: LIKE로는 절대 못 찾는 질의들 ---
    search_courses("서버 개발자가 되고 싶은데 뭐부터 배워야 해?")
    search_courses("웹사이트가 너무 느려서 빠르게 만들고 싶어")
    search_courses("인공지능으로 챗봇 만들기", category="데이터·AI")
    search_courses("백엔드 배포에 대해 알고 싶어")
    search_courses("프론트 배포에 대해 알고 싶어")
    search_courses("연동에 대해 알고 싶어")

    # --- 추천 v1 데모 ---
    recommend_similar("C003")   # Spring Boot 입문 수강생에게 추천
    recommend_similar("C022")   # 머신러닝 기초 수강생에게 추천

    # search_courses("하고 싶은 질문을 남겨본다.")
    # where 필터에 조건들을 더 추가해서 중급 이상만 추천 될 수 있도록 변경해보기