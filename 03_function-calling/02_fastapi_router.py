"""
/chat 엔드포인트를 FastAPI 서버에서 만들어둔다.
Spring 백엔드 서버에서는 사용자 요청 시 /chat 엔드포인트를 호출한다.
또한 FastAPI 서버는 필요하다면 Spring 백엔드의 엔드포인트를 역호출 한다.

"""

import os
from contextlib import asynccontextmanager

import chromadb
import httpx
from chromadb.utils import embedding_functions
from dotenv import load_dotenv
from fastapi import FastAPI
from google import genai
from google.genai import types
from pydantic import BaseModel, Field

load_dotenv()

MODEL_NAME = "jhgan/ko-sroberta-multitask"
CHROMA_PATH = "./chroma_db"
GEMINI_MODEL = "gemini-3.5-flash"
SPRING_BASE_URL = os.getenv("SPRING_BASE_URL")
TIMEOUT = httpx.Timeout(5.0)
SIMILARITY_THRESHOLD = 0.35

resources: dict = {}

# 서버 실행 전과 후 설정 및 리소스를 할당하는 작업을 하게 된다.
@asynccontextmanager
async def lifespan(app: FastAPI):
    print("초기화 중...")
    chroma = chromadb.PersistentClient(path=CHROMA_PATH)
    ef = embedding_functions.SentenceTransformerEmbeddingFunction(model_name=MODEL_NAME)
    resources["docs"] = chroma.get_or_create_collection(
        "lms_regulation", embedding_function=ef, metadata={"hnsw:space": "cosine"})
    resources["courses"] = chroma.get_or_create_collection(
        "lms_courses", embedding_function=ef, metadata={"hnsw:space": "cosine"})
    resources["genai"] = genai.Client(api_key=os.getenv("GOOGLE_API_KEY"))
    print("초기화 완료")
    # 이 부분은 서버가 켜짐과 동시에 설정해야 하는 것들
    # Spring 으로 치자면 yml 파일이 동작하는 것이라고 보면 된다.
    yield
    # 이 부분은 서버가 꺼질때 동작하는 것들
    # ex) 리소스 정리
    resources.clear()

app = FastAPI(
    title="LMS AI Server!",
    description="RAG + Funtion Calling 통합 챗봇",
    lifespan=lifespan
)

# =========================================================================
# 요청/응답 스키마
# ... 은 not null
class ChatMessage(BaseModel):
    role: str = Field(..., pattern="^(user|assistant)$")
    content: str


class ChatRequest(BaseModel):
    question: str = Field(..., min_length=1)
    student_id: int = Field(1, description="로그인한 학생 ID (Spring이 전달)")
    history: list[ChatMessage] = Field(default_factory=list)


class ChatResponse(BaseModel):
    answer: str
    used_tools: list[str]   # 모델이 어떤 도구를 썼는지 (디버깅/신뢰성용)
# =========================================================================


# =========================================================================
def search_regulation(query: str) -> dict:
    """LMS 학사규정 문서에서 관련 조항을 검색한다.
    출석, 과제 제출, 성적 평가, 수료 조건, 환불, 표절, 시험 등
    '규정/제도/기준'에 대한 질문에 사용한다.
    개별 학생의 데이터(내 진도율 등)는 이 함수로 알 수 없다.
    """
    results = resources["docs"].query(query_texts=[query], n_results=3)
    chunks = []
    for doc, meta, dist in zip(
        results["documents"][0], results["metadatas"][0], results["distances"][0]
    ):
        if (1 - dist) >= SIMILARITY_THRESHOLD:
            chunks.append({"page": meta["page"], "content": doc})
    if not chunks:
        return {"result": "규정에서 관련 내용을 찾지 못했습니다."}
    return {"regulations": chunks,
            "instruction": "이 조항만 근거로 답하고 (출처: 학사규정 p.N)을 표기할 것"}


# ======================================================================
# 도구 2~4: Spring DB 조회 (01에서 만든 함수 — 실무라면 공용 모듈로 분리)
# ======================================================================
def _spring_get(path: str, params: dict | None = None) -> dict:
    """Spring API GET 공통 처리. 에러도 '데이터'로 반환한다 (02 참고)."""
    try:
        resp = httpx.get(f"{SPRING_BASE_URL}{path}", params=params, timeout=TIMEOUT)
        resp.raise_for_status()
        return resp.json()
    except httpx.ConnectError:
        return {"error": "학사 정보 서버(Spring)에 연결할 수 없습니다."}
    except httpx.TimeoutException:
        return {"error": "학사 정보 서버 응답이 지연되고 있습니다(5초 초과)."}
    except httpx.HTTPStatusError as e:
        return {"error": f"학사 정보 조회 실패 (HTTP {e.response.status_code})"}


def get_my_courses(student_id: int) -> dict:
    """학생이 수강 중인 강좌 목록과 진도율(%)을 조회한다.
    '내 강좌', '수강 과목', '진도율' 질문에 사용한다."""
    return _spring_get(f"/api/students/{student_id}/courses")


def get_assignments_due(student_id: int, days: int = 7) -> dict:
    """학생의 마감 임박 과제 목록을 조회한다. '과제', '마감' 질문에 사용한다."""
    return _spring_get(f"/api/students/{student_id}/assignments/upcoming",
                       params={"days": days})


def get_my_summary(student_id: int) -> dict:
    """학생의 학습 요약(평균 진도율/성적)을 조회한다.
    '내 성적', '학습 현황' 질문에 사용한다."""
    return _spring_get(f"/api/students/{student_id}/summary")


# ======================================================================
# 도구 5: 강좌 추천
# ======================================================================
def recommend_next_courses(course_code: str) -> dict:
    """특정 강좌(course_code, 예: C003)를 수강한 학생에게 이어서 들으면
    좋은 유사 강좌를 추천한다. '다음에 뭐 듣지', '추천' 질문에 사용한다.
    보통 get_my_courses로 수강 강좌를 먼저 확인한 뒤 사용한다.
    """
    courses = resources["courses"]
    seed = courses.get(ids=[course_code])
    if not seed["ids"]:
        return {"error": f"강좌 코드를 찾을 수 없음: {course_code}"}
    results = courses.query(query_texts=[seed["documents"][0]], n_results=4)
    recs = [
        {"code": cid, "title": meta["title"],
         "category": meta["category"], "level": meta["level"]}
        for cid, meta in zip(results["ids"][0], results["metadatas"][0])
        if cid != course_code
    ]
    return {"based_on": seed["metadatas"][0]["title"], "recommendations": recs[:3]}
# =========================================================================


# =========================================================================
# api (router)
@app.post("/chat", response_model=ChatResponse)
def chat(req : ChatRequest):
    config = types.GenerateContentConfig(
        tools=[search_regulation, get_my_courses, get_assignments_due,
               get_my_summary, recommend_next_courses],
        system_instruction=(
            "너는 LMS 학습 도우미다. "
            f"현재 로그인한 학생의 ID는 {req.student_id}이다. "
            "규정/제도 질문은 search_regulation으로 근거를 찾아 출처와 함께 답하고, "
            "학생 개인 데이터는 반드시 함수로 조회해라. "
            "함수 결과에 없는 내용은 지어내지 말고, "
            "함수가 error를 반환하면 상황을 정중히 설명해라."
        ),
        temperature=0.2,
    )

    # 대화 이력 → Gemini contents 형식 변환 (역할: user / model)
    contents: list = []
    for m in req.history:
        contents.append(types.Content(
            role="user" if m.role == "user" else "model",
            parts=[types.Part.from_text(text=m.content)],
        ))
    contents.append(types.Content(
        role="user", parts=[types.Part.from_text(text=req.question)]
    ))

    # 도구 선택 → 실행 → 결과 반납
    response = resources["genai"].models.generate_content(
        model=GEMINI_MODEL, contents=contents, config=config
    )

    # 어떤 도구가 실제로 쓰였는지 추출 → 응답에 포함 (사용자/개발자 신뢰성)
    used = []
    for content in (response.automatic_function_calling_history or []):
        for part in (content.parts or []):
            if part.function_call:
                used.append(part.function_call.name)

    return ChatResponse(answer=response.text or "(응답 없음)", used_tools=used)

# =========================================================================