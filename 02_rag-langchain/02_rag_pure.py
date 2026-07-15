import os
import chromadb
from chromadb.utils import embedding_functions
from dotenv import load_dotenv
from google import genai
from google.genai import types

load_dotenv()

# 임베딩 모델, Chroma 컬렉션, AI Model
MODEL_NAME = "jhgan/ko-sroberta-multitask"
CHROMA_PATH = "./chroma_db"
DOC_COLLECTION = "lms_regulation"
GEMINI_MODEL = "gemini-3.5-flash"
# 검색 결과가 0.35 미만의 유사도이면 규정에 없는 질문으로 판단하기
# 위한 기준점 설정
SIMILARITY_THRESHOLD = 0.35

client = genai.Client(api_key=os.getenv("GOOGLE_API_KEY"))

# ===============================================
# RAG 란 : 검색, 증강, 생성 의미한다.
# 검색 함수, 증강 함수, 생성 함수
def rag_answer(question : str) -> str :

    # 1. 검색
    # VectorDB 에 저장해둔 pdf 관련 정보를 검색한다.
    chunks = retrieve(question) # 해당 함수 호출 시 list[ {본문, 메타, 거리}, {본문, 메타, 거리}, {본문, 메타, 거리} ]

    if not chunks :
        return(
            "규정에서 관련 내용을 찾지 못했습니다."
            "운영 지원팀에 문의해주세요 : sjw@hello.com"
        )
    
    # 2. 증강
    # 검색 된 내용을 바탕으로 Prompt 를 입혀서 답변을 증강시킨다.
    prompt = build_prompt(question, chunks)

    # 3. 생성
    # 검색 및 증강 된 내용을 바탕으로 AI 에게 답변을 요구한다.
    return generate(prompt)

# ===============================================

# ===============================================
# RAG 의 3단계 중 1단계인 검색!
# 사용자의 질문과 관련 된 규정 청크를 찾는다.
# VectorDB 에서 질문과 가장 관련 있는 청크 top_k 개를 가져온다.
def retrieve(question : str, top_k : int = 3) -> list[dict]:

    # 로컬 chromadb 사용 준비
    chroma = chromadb.PersistentClient(path=CHROMA_PATH)

    # 사용할 테이블을 가져오고 없으면 만든다.
    collection = chroma.get_or_create_collection(
        # 사내 규정 PDF 를 Vector화 시킨 테이블
        name=DOC_COLLECTION,
        # 사용자의 질문을 임베딩하는 Model
        embedding_function=embedding_functions.SentenceTransformerEmbeddingFunction(
            model_name=MODEL_NAME
        ),
        metadata={"hnsw:space" : "cosine"}
    )

    # 벡터DB 테이블에서 사용자 질문을 바탕으로 검색을 할 것이며
    # 결과는 코사인 유사도가 가장 높은 3개를 검색하겠다.
    results = collection.query(query_texts=[question], n_results=top_k)

    chunks = []

    # 벡터DB 가 각각 반환한 본문, 메타데이터, 거리를 zip 으로 묶어서 넣을 준비
    for doc, meta, dist in zip(
        results["documents"][0], results["metadatas"][0], results["distances"][0]
    ):
        # for 변수 세 개는 zip이 만든 3개짜리 튜플을 즉시 풀어 받는다(언패킹).
        similarity = 1 - dist
        if similarity >= SIMILARITY_THRESHOLD:   # 관련 없는 청크는 버린다
            # 중괄호의 `키: 값` 쌍은 딕셔너리다. 서로 다른 정보를 이름으로 묶는다.
            chunks.append({"content": doc, "page": meta["page"], "similarity": similarity})
    return chunks

# ===============================================

# ===============================================
# RAG 의 3단계 중 2번째 단계인 증강!
# 1단계에서 전달 받은 VectorDB 조회 값인 Chunks 와
# 사용자의 질문인 Question 을 프롬프트에 끼워넣기

def build_prompt(question : str , chunks : list[dict]) -> str :

    # RAG 의 완성도는 프롬프트 70% 를 차지한다.
    # 나머지 20% 은? VectorDB에 어떤 식으로 값을 저장해서
    # 조회해오고 있는 것이 중요하다.

    # 청크들을 [자료 1](p.3) 답변 형태의 블록으로 이어 붙일 준비
    context = "\n\n".join(
        # 대괄호 없이 `표현식 for ...`만 전달하면 제너레이터 표현식이다.
        # 필요한 문자열을 하나씩 만들어 join에 넘기므로 중간 리스트가 필요 없다.
        f"[자료 {i+1}] (학사규정 {c['page']}페이지)\n{c['content']}"
        for i, c in enumerate(chunks)
    )

    return f"""당신은 학원의 LMS 학습 도우미입니다.
        아래 '참고 자료'에 있는 내용만을 근거로 질문에 답하세요.

        규칙:
        - 참고 자료에 없는 내용은 절대 지어내지 말 것
        - 참고 자료로 답할 수 없으면 "규정에서 관련 내용을 찾지 못했습니다"라고 답할 것
        - 답변 끝에 근거로 사용한 자료의 페이지를 (출처: 학사규정 p.N) 형식으로 표기할 것

        [참고 자료]
        {context}

        [질문]
        {question}"""
# ===============================================

# ===============================================
# RAG 의 3단계 중 마지막 단계
# G : 생성
def generate(prompt : str) -> str:
    response = client.models.generate_content(
        model=GEMINI_MODEL,
        contents=prompt,
        # 규정 관련 안내는 정확성이 중요하기 때문에
        # 0.2 로 낮게 설정한다.
        config=types.GenerateContentConfig(temperature=0.2)
    )

    # 응답 중 생성한 TEXT 만 리턴
    return response.text

# ===============================================

# ===============================================
# Main 흐름
if __name__ == "__main__":
    question = "동영상 강의 출석 인정 기준이 뭐야? 구체적인 진도율 퍼센트로 답해줘."
    print(f"질문: {question}")
    print("-" * 60)
    print(rag_answer(question))
    print()
    print("01번 파일의 답변과 02파일의 답변을 비교해보자!!")
    print("01번은 자기가 알아서 규정을 만들어내고 답변을 했다.")
    print("RAG 가 적용된 02번은 사내규정을 바탕으로 답변을 해주게 된다.")

    for q in [
        # 리스트에 질문 문자열을 모아두면 같은 처리 코드를 반복 작성하지 않아도 된다.
        "과제를 마감 이후에 제출하면 점수가 어떻게 되나요?",
        "수료 조건을 알려주세요",
        "과제할 때 ChatGPT 같은 AI를 써도 되나요?",
    ]:
        print("\n" + "=" * 60)
        print(f"질문: {q}")
        print("-" * 60)
        print(rag_answer(q))


    print("\n" + "=" * 60)
    q = "오늘 점심 메뉴 추천해줘"
    print(f"질문: {q}")
    print("-" * 60)
    print(rag_answer(q))

# ===============================================