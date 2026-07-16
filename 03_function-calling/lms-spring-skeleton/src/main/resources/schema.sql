-- =====================================================================
-- schema.sql — LMS 데이터베이스 스키마
-- ---------------------------------------------------------------------
-- 테이블 관계 (ERD):
--   student 1 ──── N enrollment N ──── 1 course     (수강: 다대다 연결)
--                                        │
--   student 1 ──── N submission N ─── 1 assignment N ┘ (과제는 강좌에 속함)
--
-- H2(MODE=MySQL)와 MySQL 양쪽에서 동작하는 문법만 사용한다.
-- 서버 시작 시 자동 실행된다 (application.yml: spring.sql.init.mode=always)
-- =====================================================================

DROP TABLE IF EXISTS submission;
DROP TABLE IF EXISTS assignment;
DROP TABLE IF EXISTS enrollment;
DROP TABLE IF EXISTS course;
DROP TABLE IF EXISTS student;

-- 학생 (인증/인가는 범위 제외 — 비밀번호 컬럼 없음. API 계약서 1장 참고)
CREATE TABLE student (
    id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    name    VARCHAR(50)  NOT NULL,
    email   VARCHAR(100) NOT NULL UNIQUE
);

-- 강좌
-- code: Vector DB(ChromaDB)의 강좌 ID(C001~C050)와 연결되는 열쇠!
--       추천 챗봇이 "RDB의 수강 이력 → Vector DB의 유사 강좌"로
--       넘어갈 때 이 코드를 사용한다. (Day 1 courses.csv의 course_id)
CREATE TABLE course (
    id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    code      VARCHAR(10)  NOT NULL UNIQUE,
    title     VARCHAR(100) NOT NULL,
    category  VARCHAR(30)  NOT NULL,
    level     VARCHAR(10)  NOT NULL
);

-- 수강 (학생-강좌 연결 + 학습 현황)
CREATE TABLE enrollment (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id     BIGINT NOT NULL,
    course_id      BIGINT NOT NULL,
    progress_rate  INT    NOT NULL DEFAULT 0,          -- 진도율 0~100 (%)
    score          INT,                                 -- 현재 성적 (없으면 NULL)
    status         VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',  -- IN_PROGRESS/COMPLETED
    CONSTRAINT fk_enroll_student FOREIGN KEY (student_id) REFERENCES student(id),
    CONSTRAINT fk_enroll_course  FOREIGN KEY (course_id)  REFERENCES course(id),
    CONSTRAINT uq_enrollment UNIQUE (student_id, course_id)     -- 같은 강좌 중복 수강 방지
);

-- 과제 (강좌에 속함)
CREATE TABLE assignment (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id  BIGINT       NOT NULL,
    title      VARCHAR(100) NOT NULL,
    due_date   DATE         NOT NULL,
    CONSTRAINT fk_assign_course FOREIGN KEY (course_id) REFERENCES course(id)
);

-- 과제 제출 (제출 기록이 없으면 = 미제출)
CREATE TABLE submission (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    assignment_id  BIGINT NOT NULL,
    student_id     BIGINT NOT NULL,
    submitted_at   TIMESTAMP NOT NULL,
    score          INT,
    CONSTRAINT fk_sub_assignment FOREIGN KEY (assignment_id) REFERENCES assignment(id),
    CONSTRAINT fk_sub_student    FOREIGN KEY (student_id)    REFERENCES student(id),
    CONSTRAINT uq_submission UNIQUE (assignment_id, student_id)
);
