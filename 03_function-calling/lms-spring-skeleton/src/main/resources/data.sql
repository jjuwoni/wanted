-- =====================================================================
-- data.sql — 실습용 더미데이터
-- ---------------------------------------------------------------------
-- 시나리오 설계 의도:
--   학생 1(김민준) = 챗봇 데모의 주인공 (ChatController의 DEMO_STUDENT_ID)
--     - Spring Boot 입문(C003)을 85%까지 들음 → "다음 강좌 추천" 데모용
--     - 미제출 과제 1건이 며칠 뒤 마감 → "이번 주 마감 과제" 데모용
--   학생 2(이서연) = 실습 2에서 학생 ID를 바꿔볼 때 사용
--   학생 3(박지훈) = 수강 이력이 없는 학생 → 빈 결과 처리 확인용
--
-- ★ 과제 마감일은 DATEADD로 '실행일 기준 상대 날짜'로 넣는다.
--   고정 날짜(2026-07-17)로 넣으면 며칠만 지나도 "이번 주 마감" 데모가
--   깨지기 때문이다. 수업이 언제 열려도 데모가 항상 성립한다.
--   ※ H2 문법. MySQL로 전환 시: DATEADD('DAY', 3, CURRENT_DATE)
--                            → DATE_ADD(CURDATE(), INTERVAL 3 DAY)
-- =====================================================================

-- 학생 -----------------------------------------------------------------
INSERT INTO student (id, name, email) VALUES
  (1, '김민준', 'minjun.kim@example.com'),
  (2, '이서연', 'seoyeon.lee@example.com'),
  (3, '박지훈', 'jihun.park@example.com');

-- 강좌 (code는 Day 1 courses.csv / ChromaDB의 강좌 ID와 반드시 일치!) ---
INSERT INTO course (id, code, title, category, level) VALUES
  (1,  'C001', 'Java 프로그래밍 기초',            '백엔드',   '입문'),
  (2,  'C003', 'Spring Boot 입문',                '백엔드',   '입문'),
  (3,  'C004', 'Spring Boot 실전 REST API 개발',  '백엔드',   '중급'),
  (4,  'C005', 'JPA와 데이터베이스 연동',          '백엔드',   '중급'),
  (5,  'C007', 'MySQL 데이터베이스 기초',          '백엔드',   '입문'),
  (6,  'C019', 'Python 프로그래밍 기초',           '데이터·AI','입문'),
  (7,  'C025', 'LLM 애플리케이션 개발 입문',       '데이터·AI','중급'),
  (8,  'C026', 'RAG 시스템 구축 실전',             '데이터·AI','고급'),
  (9,  'C036', 'Git과 GitHub 협업 완전정복',       '개발일반', '입문'),
  (10, 'C043', '자료구조와 알고리즘 기초',          '컴퓨터과학','입문');

-- 수강 -----------------------------------------------------------------
-- 학생 1: 백엔드 트랙 진행 중 (추천 데모: C003 기반 → C004/C005가 추천되어야 자연스러움)
INSERT INTO enrollment (student_id, course_id, progress_rate, score, status) VALUES
  (1, 1, 100, 95, 'COMPLETED'),      -- Java 기초 수료
  (1, 2,  85, 92, 'IN_PROGRESS'),    -- Spring Boot 입문 진행 중
  (1, 5,  40, 78, 'IN_PROGRESS');    -- MySQL 기초 진행 중

-- 학생 2: 데이터·AI 트랙
INSERT INTO enrollment (student_id, course_id, progress_rate, score, status) VALUES
  (2, 6, 100, 88, 'COMPLETED'),
  (2, 7,  60, 85, 'IN_PROGRESS');

-- (학생 3은 의도적으로 수강 이력 없음)

-- 과제 -----------------------------------------------------------------
-- 마감일: 실행일 기준 상대 날짜 (위 상단 주석 참고)
INSERT INTO assignment (id, course_id, title, due_date) VALUES
  (1, 2, '3주차 과제: 회원 REST API 만들기',        DATEADD('DAY', 3,  CURRENT_DATE)),  -- 3일 뒤 마감
  (2, 2, '4주차 과제: 예외 처리 적용하기',           DATEADD('DAY', 10, CURRENT_DATE)),  -- 10일 뒤
  (3, 5, '2주차 과제: JOIN 연습 문제',              DATEADD('DAY', 5,  CURRENT_DATE)),  -- 5일 뒤
  (4, 5, '1주차 과제: SELECT 기본기',               DATEADD('DAY', -4, CURRENT_DATE)),  -- 이미 지남
  (5, 7, 'LLM 과제: 프롬프트 개선 리포트',           DATEADD('DAY', 6,  CURRENT_DATE));

-- 제출 -----------------------------------------------------------------
-- 학생 1은 과제 4(지난 과제)만 제출함
--   → 과제 1(3일 뒤), 과제 3(5일 뒤)이 '미제출 + 마감 임박'으로 조회되어야 정상
INSERT INTO submission (assignment_id, student_id, submitted_at, score) VALUES
  (4, 1, DATEADD('DAY', -5, CURRENT_TIMESTAMP), 90);
