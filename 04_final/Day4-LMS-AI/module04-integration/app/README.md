# app 폴더 읽는 법

- `main.py`: 애플리케이션 조립과 공통 미들웨어 등록
- `routers/`: HTTP 입출력
- `schemas/`: JSON 계약과 검증
- `services/`: 챗봇·추천 유스케이스
- `repositories/`: Vector DB 접근 추상화
- `clients/`: Spring·Gemini 같은 외부 시스템 호출
- `core/`: 설정, 로깅, 공통 예외

권장 읽기 순서는 `main.py → routers → schemas → services → repositories/clients → core`입니다.
