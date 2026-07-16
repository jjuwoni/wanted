"""
[Function Calling]

어제 RAG 와는 조금 다른 점
* RAG : "출석 인정 기준이 뭐야?" 라고 물어보면 답은 VectorDB 에 넣어둔 규정.pdf 파일에 데이터가 존재한다.
* FC : "내가 듣고 있는 1번 강의의 진도율이 몇 % 야?" 라고 물어보면 답은 MySQL 수강 이력 DB 에 데이터가 존재한다.

- 문서 등은 미리 임베딩 해둘 수 있지만, 현재 학생의 진도율 데이터는 실시간 데이터 조회가 필요하다.
- 따라서 AI 모델 (Google Gemini) 에게 도구를 쥐어 줄 것이다. (Function)
"""

import os

from dotenv import load_dotenv
from google import genai
from google.genai import types

# FastAPI 에서 역으로 Http 요청을 보낼 준비
import httpx

load_dotenv()

client = genai.Client(api_key=os.getenv("GOOGLE_API_KEY"))

# 사용할 Model
MODEL = "gemini-3.5-flash"

# API Key, 인스턴스 주소는 env 로 작성 후 git 에는 올라가지 않게
# 설정하는 것이 중요하다.
SPRING_BASE_URL = os.getenv("SPRING_BASE_URL")

# 타임아웃 관련 설정
# FastAPI -> Spring 요청 시 Spring 이 응답을 안 주면?!!?!?!?
# 해당 함수는 영문도 모른채 영원히 기다리게 된다.
# 따라서 5초 안에 응답을 하지 않는다면 실패로 처리를 할 것이다.
TIMEOUT = httpx.Timeout(5.0)

# ==============================================================
# FastAPI 쪽에서 LLM 없이 직접 Spring API 호출
def get_my_courses(student_id : int) -> dict:
    return _spring_get(f"/api/students/{student_id}/summary")

def get_assignments_due(student_id: int, days: int = 7) -> dict:
    """학생의 마감 임박 과제 목록을 조회한다. '과제', '마감' 질문에 사용한다."""
    return _spring_get(f"/api/students/{student_id}/assignments/upcoming",
                       params={"days": days})

def get_my_summary(student_id: int) -> dict:
    """학생의 학습 요약(평균 진도율/성적)을 조회한다.
    '내 성적', '학습 현황' 질문에 사용한다."""
    return _spring_get(f"/api/students/{student_id}/summary")
# ==============================================================


# ==============================================================
# Spring Server API 호출 통합 핸들러 함수
# 통합 해들러 이기 때문에 URL 만 전달할 수도 있고, 필요하면 파라미터가 올 수도 있다.
# 고로 파라미터는 있으면 dict 타입으로 전달 받고 없으면 null 로 처리하겠다.
def _spring_get(path : str, params : dict | None = None) -> dict:

    try:
        resp = httpx.get(f"{SPRING_BASE_URL}{path}", params=params, timeout=TIMEOUT)
        resp.raise_for_status() # 4XX / 5XX 응답이 오면 예외를 raise
        return resp.json() # spring api 는 json 문자열을 return 해준다.
        # json 문자열을 json 객체로 바꾸어 주는 것이 json() 이다.
    except httpx.ConnectError:
        return {"error" : "LMS 백엔드 서버에 연결할 수 없습니다!"}
    except httpx.TimeoutException:
        return {"error" : "LMS 백엔드 서버에 요청이 많아 응답이 지연되고 있습니다..."}
    except httpx.HTTPStatusError as e:
        return {"error" : f"LMS 학사 정보 조회 실패!! (HTTP {e.response.status_code})",
                "detail" : e.response.text[:200]}
# ==============================================================


# ==============================================================
# Main 흐름

if __name__ == "__main__":
    print("=== [0] Spring 호출 테스트 ===")
    print(get_my_courses(1))
    print("=============================")
    config = types.GenerateContentConfig(
        tools=[get_my_courses , get_assignments_due, get_my_summary],
        system_instruction=(
            "너는 LMS 학습 도우미다."
            "현재 로그인한 학생의 ID 는 1이다."
            "학생의 개인 데이터가 필요하면 반드기 함수로 조회하고,"
            "조회 결과에 없는 내용은 지어니지 말아라."
            "함수가 error 를 반환하면 그 상황을 정중히 설명해라."
        ),
        temperature=0.2
    )

    questions = [
        "내가 지금 뭐 듣고 있지? 진도율도 같이 보여줘",
        "이번 주 마감 과제 중에 아직 제출 안 한 거 있어?",
        "나 요즘 공부 잘하고 있는 거야?",
    ]
    for q in questions:
        print("\n" + "=" * 60)
        print(f"Q: {q}")
        response = client.models.generate_content(model=MODEL, contents=q, config=config)
        print(f"A: {response.text}")
# ==============================================================