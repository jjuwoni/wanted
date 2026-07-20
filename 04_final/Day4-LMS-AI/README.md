# Day 4 배포 패키지 — LMS AI 학습 도우미

이 폴더에는 서로 통신하는 두 서버가 들어 있습니다.

- `lms-spring-mysql/`: MySQL과 사용자·수강·과제 데이터를 담당하는 Spring 서버
- `module04-integration/`: RAG, LangChain, ChromaDB, Gemini, Function Calling을 담당하는 FastAPI 서버

## 시작 순서

1. `lms-spring-mysql/README.md`에 따라 MySQL과 Spring을 실행합니다.
2. `module04-integration/README.md`의 STEP 2부터 따라 FastAPI 환경과 색인을 준비합니다.
3. Postman 컬렉션을 Import하고 A→E 순서로 테스트합니다.

FastAPI 코드를 공부할 때는 먼저 `module04-integration/docs/architecture-guide.md`를 읽으세요.
