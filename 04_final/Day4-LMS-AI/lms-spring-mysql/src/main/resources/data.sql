-- =====================================================================
-- data.sql — 실습용 더미데이터 (MySQL 버전)
-- ---------------------------------------------------------------------
-- 시나리오 설계 의도:
--   학생 1(김민준) = 추천 데모의 주인공 (백엔드 트랙)
--     Java 기초 '수료' + Spring Boot 입문 85% + MySQL 기초 40%
--     → 추천 기대: C004(실전 REST API, 입문→중급 레벨업 보너스)가 상위,
--       C005(JPA), C002(객체지향 심화) 등이 뒤따르는 그림
--   학생 2(이서연) = 데이터·AI 트랙 → 학생 1과 '완전히 다른' 추천이
--     나오는 것을 비교 시연 ("이력이 다르면 추천이 다르다")
--   학생 3(박지훈) = 수강 이력 없음 → 404 에러 시나리오 담당
--
-- ★ 날짜는 전부 '실행일 기준 상대 날짜'(MySQL 함수)로 넣는다.
--   고정 날짜로 넣으면 며칠만 지나도 "이번 주 마감" 데모가 깨진다.
--   H2 시절 DATEADD(...) → MySQL은 DATE_ADD(CURDATE(), INTERVAL n DAY)
-- =====================================================================

-- 학생 -----------------------------------------------------------------
INSERT INTO student (id, name, email) VALUES
  (1, '김민준', 'minjun.kim@example.com'),
  (2, '이서연', 'seoyeon.lee@example.com'),
  (3, '박지훈', 'jihun.park@example.com');

-- 강좌 (code는 Day 1 courses.csv / ChromaDB의 강좌 ID와 반드시 일치!) ---
-- updated_at은 명시하지 않는다 → MySQL이 INSERT 시각을 자동 기록.
-- 수업 중 UPDATE course SET description... 을 해보면 updated_at이 바뀌고,
-- 증분 색인이 그 강좌만 다시 임베딩하는 것을 관찰할 수 있다!
INSERT INTO course (id, code, title, description, category, level) VALUES
  ( 1, 'C001', 'Java 프로그래밍 기초', '변수와 자료형부터 클래스와 객체지향의 기본 개념까지 Java 언어의 기초 문법을 처음부터 차근차근 배웁니다.', '백엔드', '입문'),
  ( 2, 'C002', '객체지향 프로그래밍 심화', '상속과 다형성 인터페이스 추상클래스 등 객체지향 설계의 핵심 원리와 SOLID 원칙을 Java 예제로 학습합니다.', '백엔드', '중급'),
  ( 3, 'C003', 'Spring Boot 입문', 'Spring Boot로 첫 웹 애플리케이션을 만들며 의존성 주입과 MVC 패턴 컨트롤러 작성법을 익힙니다.', '백엔드', '입문'),
  ( 4, 'C004', 'Spring Boot 실전 REST API 개발', '실무 수준의 REST API를 설계하고 예외 처리 검증 문서화까지 Spring Boot 기반 백엔드 개발 전 과정을 다룹니다.', '백엔드', '중급'),
  ( 5, 'C005', 'JPA와 데이터베이스 연동', 'JPA와 Hibernate로 엔티티를 설계하고 연관관계 매핑과 영속성 컨텍스트를 이해하며 실무 쿼리 최적화를 배웁니다.', '백엔드', '중급'),
  ( 6, 'C007', 'MySQL 데이터베이스 기초', '관계형 데이터베이스 개념과 SQL 기본 문법 SELECT JOIN GROUP BY를 실습 위주로 배웁니다.', '백엔드', '입문'),
  ( 7, 'C008', 'SQL 성능 튜닝과 인덱스', '실행 계획을 읽고 인덱스 설계와 쿼리 튜닝으로 느린 쿼리를 개선하는 데이터베이스 성능 최적화 기법을 다룹니다.', '백엔드', '고급'),
  ( 8, 'C019', 'Python 프로그래밍 기초', '파이썬 설치부터 자료형 제어문 함수까지 프로그래밍이 처음인 사람을 위한 파이썬 입문 과정입니다.', '데이터·AI', '입문'),
  ( 9, 'C022', '머신러닝 기초', '지도학습과 비지도학습 개념 회귀 분류 모델을 scikit-learn으로 직접 만들며 머신러닝의 기본기를 다집니다.', '데이터·AI', '중급'),
  (10, 'C025', 'LLM 애플리케이션 개발 입문', '대규모 언어모델 API를 활용해 챗봇과 요약 서비스를 만들며 프롬프트 엔지니어링 기초를 배웁니다.', '데이터·AI', '중급'),
  (11, 'C026', 'RAG 시스템 구축 실전', '임베딩과 벡터 데이터베이스 검색 증강 생성 파이프라인을 구축해 문서 기반 질의응답 시스템을 완성합니다.', '데이터·AI', '고급'),
  (12, 'C036', 'Git과 GitHub 협업 완전정복', '버전 관리 개념부터 브랜치 전략 풀리퀘스트 코드리뷰까지 팀 협업에 필요한 Git 사용법을 익힙니다.', '개발일반', '입문'),
  (13, 'C043', '자료구조와 알고리즘 기초', '배열 스택 큐 해시 트리 등 핵심 자료구조와 정렬 탐색 알고리즘을 코딩테스트 문제로 연습합니다.', '컴퓨터과학', '입문');

-- 수강 -----------------------------------------------------------------
-- 학생 1: 백엔드 트랙. 가중치 계산 연습(README 5장)의 예시가 바로 이 데이터다.
--   C001 수료(가중치 1.0) / C003 진행 85%(0.85) / C007 진행 40%(0.4)
INSERT INTO enrollment (student_id, course_id, progress_rate, score, status, enrolled_at) VALUES
  (1, 1, 100, 95, 'COMPLETED',   DATE_SUB(CURDATE(), INTERVAL 60 DAY)),
  (1, 3,  85, 92, 'IN_PROGRESS', DATE_SUB(CURDATE(), INTERVAL 30 DAY)),
  (1, 6,  40, 78, 'IN_PROGRESS', DATE_SUB(CURDATE(), INTERVAL 20 DAY));

-- 학생 2: 데이터·AI 트랙
INSERT INTO enrollment (student_id, course_id, progress_rate, score, status, enrolled_at) VALUES
  (2, 8, 100, 88, 'COMPLETED',   DATE_SUB(CURDATE(), INTERVAL 45 DAY)),
  (2, 10, 60, 85, 'IN_PROGRESS', DATE_SUB(CURDATE(), INTERVAL 15 DAY));

-- (학생 3은 의도적으로 수강 이력 없음 → 추천 404 시나리오)

-- 과제 -----------------------------------------------------------------
INSERT INTO assignment (id, course_id, title, due_date) VALUES
  (1, 3, '3주차 과제: 회원 REST API 만들기',   DATE_ADD(CURDATE(), INTERVAL 3 DAY)),
  (2, 3, '4주차 과제: 예외 처리 적용하기',      DATE_ADD(CURDATE(), INTERVAL 10 DAY)),
  (3, 6, '2주차 과제: JOIN 연습 문제',         DATE_ADD(CURDATE(), INTERVAL 5 DAY)),
  (4, 6, '1주차 과제: SELECT 기본기',          DATE_SUB(CURDATE(), INTERVAL 4 DAY)),
  (5, 10, 'LLM 과제: 프롬프트 개선 리포트',     DATE_ADD(CURDATE(), INTERVAL 6 DAY));

-- 제출 -----------------------------------------------------------------
-- 학생 1은 과제 4(지난 과제)만 제출 → 과제 1, 3이 '미제출+마감 임박'
INSERT INTO submission (assignment_id, student_id, submitted_at, score) VALUES
  (4, 1, DATE_SUB(NOW(), INTERVAL 5 DAY), 90);
