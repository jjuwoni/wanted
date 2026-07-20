# FastAPI 아키텍처 학습 가이드

이 문서는 파일 목록을 외우기 위한 문서가 아니라, **변경 이유에 따라 코드를 어디에 배치해야 하는지 판단하는 기준**을 제공합니다.

## 1. 핵심 책임

Spring은 로그인 사용자, 수강 이력, 과제, MySQL 트랜잭션과 같은 서비스 상태를 관리합니다. FastAPI는 LLM 호출, 벡터 검색, Function Calling, 추천 점수 계산처럼 AI 연산을 담당합니다.

FastAPI가 실시간 요청마다 MySQL을 직접 조회하지 않는 이유는 비즈니스 규칙과 권한 검사를 Spring 한 곳에 유지하기 위해서입니다. 반면 전체 강좌 색인은 대량 배치 작업이므로 읽기 전용 계정으로 MySQL을 직접 조회합니다.

## 2. 의존 방향

```text
HTTP 요청
   ↓
routers       요청을 받고 응답 모델을 선언
   ↓
services      유스케이스와 판단 규칙 실행
   ↓
repositories / clients
   ↓
ChromaDB / Spring / Gemini
```

`core`와 `schemas`는 여러 계층이 공유하지만, 서비스 로직을 포함하지 않습니다.

### 계층별 판단 질문

| 질문 | 배치할 위치 |
|---|---|
| 새 URL이나 HTTP 상태 코드가 필요한가? | `routers/` |
| 요청·응답 JSON 구조가 바뀌는가? | `schemas/`와 `docs/api-contract.md` |
| 추천 점수나 도구 선택 규칙이 바뀌는가? | `services/` |
| ChromaDB 조회 방식이 바뀌는가? | `repositories/` |
| Spring 또는 Gemini 호출 방식이 바뀌는가? | `clients/` |
| 환경변수·로깅·공통 예외에 영향을 주는가? | `core/` |
| 요청 서버와 별개로 데이터를 색인하는가? | `ingestion/` |

## 3. 요청 흐름 읽기

### 챗봇 `/chat`

1. `routers/chat.py`가 `ChatRequest`를 검증합니다.
2. `chat_service.py`가 사용할 도구를 정의하고 LLM에 바인딩합니다.
3. `agent_loop.py`가 LLM의 tool call을 실행하고 결과를 다시 LLM에 전달합니다.
4. 규정 질문은 `vector_store.py`, 개인 질문은 `spring_client.py`를 사용합니다.
5. 최종 답변과 실제 사용 도구 목록을 `ChatResponse`로 반환합니다.

### 추천 `/courses/recommend`

1. `spring_client.py`가 학생의 실제 수강 이력을 Spring에서 가져옵니다.
2. 각 이력 강좌를 시드로 ChromaDB에서 유사 강좌 후보를 생성합니다.
3. `recommend_service.py`가 이미 수강한 강좌를 제외하고 가중치와 레벨업 보너스로 재정렬합니다.
4. 상위 결과를 LLM에 전달해 자연어 추천 이유를 생성합니다.

이 구조에서 벡터 검색은 후보를 찾고, 서비스 계층은 최종 판단을 합니다. Vector DB에 비즈니스 규칙을 넣지 않는 것이 중요합니다.

## 4. 오프라인 색인 흐름

`python -m ingestion.run_ingest`는 FastAPI 서버 요청과 별도로 실행되는 배치 프로그램입니다.

```text
MySQL course / 학사규정 PDF
        ↓
mysql_reader.py / pdf_loader.py
        ↓
Document 변환과 청킹
        ↓
동일한 embedding model
        ↓
ChromaDB upsert
```

색인과 검색에서 서로 다른 임베딩 모델을 사용하면 벡터 좌표계가 달라져 유사도 결과를 신뢰할 수 없습니다.

## 5. 코드 리뷰 기준

- 라우터에 추천 점수 계산이나 외부 API 호출 코드가 들어가 있지 않은가?
- 서비스가 HTTP 요청·응답 객체에 직접 의존하지 않는가?
- ChromaDB 접근이 `vector_store.py` 밖으로 퍼지지 않았는가?
- Spring/Gemini 호출 실패가 의미 있는 예외로 변환되는가?
- API 필드 변경 시 계약 문서와 양쪽 DTO가 함께 수정됐는가?
- 요청 로그에 동일한 `X-Request-ID`가 유지되는가?
