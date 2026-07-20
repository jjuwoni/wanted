# Spring ↔ FastAPI API 계약서 (v1.2)

> Day 3 3교시 산출물. **Day 4 통합의 유일한 기준 문서**입니다.
> 양쪽 개발자(Spring 담당 / FastAPI 담당)는 상대의 코드가 아니라 **이 문서**를 보고 개발합니다. 계약을 바꾸려면 문서를 먼저 고치고 양쪽에 공지합니다.

---

## 1. 전체 구조와 책임 분리

```
[브라우저] ──(A)──> [Spring :8080] ──(B)──> [FastAPI :8000] ──> Gemini / ChromaDB
                        ▲                        │
                        └────────(C)─────────────┘  Function Calling 역호출
```

| 구간 | 호출자 → 수신자 | 프로토콜 | CORS 필요? |
|---|---|---|---|
| (A) | 브라우저 → Spring | HTTP (fetch) | **필요** (다른 origin이면) — Spring `CorsConfig` |
| (B) | Spring → FastAPI | HTTP (RestClient) | **불필요** — 서버 간 통신은 CORS 대상 아님 |
| (C) | FastAPI → Spring | HTTP (httpx) | **불필요** — 위와 동일 |

**책임 분리 원칙:** 상태(로그인, 세션, RDB)는 Spring이, AI 연산(LLM, 벡터 검색)은 FastAPI가 담당한다. FastAPI는 무상태(stateless)로 유지한다.

**인증에 대한 현재 결정(v1.0):** 인증/인가는 범위 제외. 로그인 학생은 **ID 1로 하드코딩**하며 Spring `ChatController`의 `DEMO_STUDENT_ID` 상수가 유일한 출처다. (추후 Spring Security 도입 시 이 상수만 `@AuthenticationPrincipal`로 교체하면 되도록 설계)

---

## 2. 공통 규칙

| 항목 | 규칙 |
|---|---|
| JSON 필드 네이밍 | **snake_case로 통일** (예: `student_id`, `used_tools`). FastAPI(Pydantic)의 기본값이므로, Spring이 `@JsonNaming(SnakeCaseStrategy)`로 맞춘다 |
| 문자 인코딩 | UTF-8 |
| 날짜 형식 | `yyyy-MM-dd` (ISO-8601) |
| 타임아웃 | Spring→FastAPI: **connect 3s / read 60s** (LLM 응답은 느리다!) · FastAPI→Spring: **5s** |
| 에러 응답 | FastAPI: `{"detail": "메시지"}` (프레임워크 기본) · Spring: `{"detail": "메시지"}` 로 **FastAPI 형식에 맞춘다** (한 가지 형식만 처리하도록) |

---

## 3. (B) Spring → FastAPI

### POST /chat — 통합 챗봇

요청:
```json
{
  "question": "다음 학기에 뭐 들으면 좋을까?",
  "student_id": 1,
  "history": [
    {"role": "user", "content": "내 진도율 알려줘"},
    {"role": "assistant", "content": "Spring Boot 입문 85%..."}
  ]
}
```

응답 (200):
```json
{
  "answer": "Spring Boot 입문을 거의 마치셨으니 JPA와 데이터베이스 연동을 추천드려요...",
  "used_tools": ["get_my_courses", "recommend_next_courses"]
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| question | string (필수) | 사용자 질문 원문 |
| student_id | int | 로그인 학생 ID. **Spring이 채운다** (v1.0: 하드코딩 상수) |
| history | array | 과거 대화. **Spring이 보관·전달** (FastAPI는 저장하지 않음). 최근 6개 이내 권장 |
| answer | string | 챗봇 답변 (규정 답변이면 "(출처: 학사규정 p.N)" 포함) |
| used_tools | array | 사용된 도구 이름 목록 (디버깅/UI 배지용) |

에러: `422` 검증 실패 / `500` 내부 오류 → 모두 `{"detail": ...}`

### GET /health — 상태 확인
응답: `{"status": "ok", "doc_chunks": 13, "courses": 13}`

### POST /courses/recommend — 강의 추천 (v1.2)

요청: `{"student_id": 1, "top_k": 3}`

응답 (200):
```json
{
  "based_on": "Java 프로그래밍 기초(수료) 외 2건",
  "recommendations": [
    {"code": "C004", "title": "Spring Boot 실전 REST API 개발",
     "category": "백엔드", "level": "중급",
     "score": 1.515, "matched_from": ["Java 프로그래밍 기초", "Spring Boot 입문"]}
  ],
  "explanation": "Java와 Spring 기초를 다지셨으니..."
}
```

| 필드 | 설명 |
|---|---|
| based_on | 추천 근거가 된 수강 이력 요약 |
| score | 2단계 파이프라인 점수: Σ(유사도×이력가중치) + 레벨업 보너스 |
| matched_from | 이 후보를 만든 수강 이력 강좌들 (설명 가능성) |

에러: 이력 없음 `404` / Spring 장애 `502` — `{"detail", "request_id"}`

---

## 4. (C) FastAPI → Spring

### GET /api/students/{studentId}/courses — 수강 목록
```json
{
  "student_id": 1,
  "courses": [
    {"code": "C003", "title": "Spring Boot 입문", "category": "백엔드",
     "progress_rate": 85, "score": 92, "status": "IN_PROGRESS"}
  ]
}
```

### GET /api/students/{studentId}/assignments/upcoming?days=7 — 마감 임박 과제
```json
{
  "student_id": 1,
  "assignments": [
    {"course_title": "Spring Boot 입문", "title": "3주차 과제: REST API 만들기",
     "due_date": "2026-07-17", "submitted": false}
  ]
}
```

### GET /api/students/{studentId}/summary — 학습 요약
```json
{
  "student_id": 1, "student_name": "김민준",
  "course_count": 3, "avg_progress_rate": 68.3, "avg_score": 88.5
}
```

에러: 존재하지 않는 학생 → `404` + `{"detail": "학생을 찾을 수 없습니다: 99"}`

---

## 5. 변경 이력

| 버전 | 날짜 | 내용 |
|---|---|---|
| v1.0 | Day 3 | 최초 작성. 인증 제외(학생 ID 하드코딩), snake_case 통일 결정 |
| v1.1 | Day 4 | 에러 본문에 request_id 추가 (로깅/예외 관통) |
| v1.2 | Day 4 | POST /courses/recommend 추가 — score·matched_from·based_on 정의 |
