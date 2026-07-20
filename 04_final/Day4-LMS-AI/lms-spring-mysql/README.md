# LMS Spring 서버 — MySQL 최종본 (Day 4)

> Day 4 통합 프로젝트에서 **FastAPI(AI 서버)의 짝**이 되는 Spring 프로젝트의 완성본입니다.
> Day 3 스켈레톤에 로깅(RequestIdFilter/logback), 예외 연동(AiServerException), 추천 프록시(RecommendController), 그리고 **MySQL 전환**이 모두 병합되어 있습니다 — 덮어쓸 파일 없이 이 프로젝트 하나로 시작하세요.

---

## 진행 순서 (STEP 0 → 3)

### STEP 0. 준비물 확인
로컬에 MySQL(8.x)이 설치·실행 중이어야 합니다. `mysql --version`으로 확인.

### STEP 1. DB와 계정 만들기 (최초 1번만)
```bash
mysql -u root -p < db/init.sql
```
이 스크립트는 `lmsdb` 데이터베이스와 계정 2개를 만듭니다: Spring용 `lms_user`(전체 권한), 그리고 FastAPI 색인 배치용 `lms_reader`(**SELECT만**). 계정을 왜 둘로 나누는지가 오늘 수업 내용의 일부입니다 — `db/init.sql` 상단 주석(최소 권한 원칙)을 꼭 읽어보세요.

확인:
```bash
mysql -u lms_user -p'lms1234!' -e "USE lmsdb; SELECT 1;"
mysql -u lms_reader -p'reader1234!' -e "USE lmsdb; SELECT 1;"
```

### STEP 2. 서버 실행
```bash
./gradlew bootRun        # Windows: gradlew.bat bootRun
```
비밀번호를 init.sql과 다르게 만들었다면 `application.yml`의 `password`만 수정하세요.

기동하면서 `schema.sql`(테이블 전체 재생성) → `data.sql`(더미데이터)이 자동 실행됩니다. **재시작 = 데이터 초기화**라서 실습 중 마음껏 망가뜨려도 됩니다. 단, `application.yml`의 `sql.init.mode: always` 주석에 있는 실무 노트를 반드시 읽으세요 — 운영에서 이 설정은 금물이며, 실무는 Flyway 같은 마이그레이션 도구로 스키마 변경 이력을 관리합니다.

### STEP 3. 동작 확인 체크리스트
1. 콘솔 로그가 `[rid=-]` 형식으로 나오는가 (logback 적용 확인)
2. `http://localhost:8080/api/students/1/courses` → 김민준의 수강 3건, 필드가 `progress_rate`처럼 snake_case인가 (계약서 2장)
3. `http://localhost:8080/api/students/1/assignments/upcoming?days=7` → 미제출 과제 2건 (마감일이 항상 "며칠 뒤"인 이유는 `data.sql`의 상대 날짜 주석 참고)
4. `http://localhost:8080/api/students/99/summary` → `404` + `{"detail": ..., "request_id": ...}` (에러 계약 v1.1)

---

## 이 프로젝트에서 꼭 읽어야 할 주석 4곳

| 파일 | 내용 |
|---|---|
| `config/CorsConfig.java` | CORS는 브라우저 전용 정책 — "RestClient가 CORS에 막혔다"가 오진인 이유 |
| `config/RestClientConfig.java` | 타임아웃 미설정 → 톰캣 스레드 고갈 시나리오, LLM이라 read 60초인 이유 |
| `config/RequestIdFilter.java` + `resources/logback-spring.xml` | X-Request-ID가 MDC를 거쳐 모든 로그에 자동으로 붙는 원리 (FastAPI와 같은 rid로 이어짐) |
| `ai/AiClientService.java` | 챗봇(정중한 실패)과 추천(예외 승격)의 에러 정책이 왜 다른가 |

## Day 4에서 추가된 스키마 컬럼 2개 (AI 기능의 재료)

`course.updated_at` — INSERT/UPDATE 때 MySQL이 자동 기록. FastAPI의 **증분 색인**이 "마지막 색인 이후 바뀐 강좌"를 이 컬럼으로 골라냅니다. 수업 중 `UPDATE course SET description='...' WHERE code='C003';`을 해보고 증분 색인이 그 강좌만 다시 임베딩하는 것을 관찰하세요.

`enrollment.enrolled_at` — 수강 시작일. 기본 추천 로직은 사용하지 않지만, "최근에 들은 강좌일수록 취향을 더 반영"하는 recency 가중치 심화 과제의 재료입니다.

## 데이터 시나리오 (data.sql)

| 학생 | 이력 | 용도 |
|---|---|---|
| 1 김민준 | Java 기초 수료(100/95) · Spring Boot 입문 85% · MySQL 기초 40% | 추천 데모 주인공 — 가중치 손계산 예시(AI 서버 README 5장)와 숫자가 일치 |
| 2 이서연 | Python 기초 수료 · LLM 입문 60% | 학생 1과 전혀 다른 추천이 나오는 비교 시연 |
| 3 박지훈 | 이력 없음 | 추천 404 에러 시나리오 |

강좌 `code`(C001~C043)는 ChromaDB의 강좌 ID와 일치합니다 — MySQL 이력과 벡터 검색을 잇는 다리입니다.
