-- =====================================================================
-- schema.sql — LMS 데이터베이스 스키마 (MySQL 버전)
-- ---------------------------------------------------------------------
-- 테이블 관계 (ERD):
--   student 1 ──── N enrollment N ──── 1 course     (수강: 다대다 연결)
--                                        │
--   student 1 ──── N submission N ─── 1 assignment N ┘ (과제는 강좌에 속함)
--
-- 서버 시작 시 자동 실행된다 (application.yml: sql.init.mode=always)
-- → DROP부터 하므로 재시작 = 초기화. 운영에서는 금물! (yml 주석 참고)
--
-- [Day 4 추가 컬럼 2개 — AI 기능을 위한 준비]
--   course.updated_at    : '증분 색인'의 기준. FastAPI 배치가
--                          "마지막 색인 이후 바뀐 강좌"만 골라 재임베딩한다.
--   enrollment.enrolled_at: 수강 시작일. "최근 들은 강좌일수록 취향을
--                          더 반영"하는 recency 가중치의 재료 (심화 과제용).
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
) ENGINE=InnoDB;

-- 강좌
-- code: Vector DB(ChromaDB)의 강좌 ID(C001~C050)와 연결되는 열쇠!
--       추천 파이프라인이 "MySQL의 수강 이력 → ChromaDB의 유사 강좌"로
--       넘어갈 때 이 코드를 사용한다. (Day 1 courses.csv의 course_id)
CREATE TABLE course (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    code        VARCHAR(10)  NOT NULL UNIQUE,
    title       VARCHAR(100) NOT NULL,
    -- description: ★ 임베딩의 재료! FastAPI 색인 배치가 '제목 - 설명'을
    --   하나의 문서로 만들어 벡터화한다. 설명이 좋을수록 추천이 좋아진다.
    description VARCHAR(300) NOT NULL,
    category    VARCHAR(30)  NOT NULL,
    level       VARCHAR(10)  NOT NULL,
    -- 행이 INSERT/UPDATE 될 때마다 MySQL이 자동으로 현재 시각을 기록.
    -- 증분 색인: WHERE updated_at > {마지막 색인 시각} 로 변경분만 조회!
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                          ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 수강 (학생-강좌 연결 + 학습 현황)
CREATE TABLE enrollment (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id     BIGINT NOT NULL,
    course_id      BIGINT NOT NULL,
    progress_rate  INT    NOT NULL DEFAULT 0,          -- 진도율 0~100 (%)
    score          INT,                                 -- 현재 성적 (없으면 NULL)
    status         VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',  -- IN_PROGRESS/COMPLETED
    enrolled_at    DATE   NOT NULL,                     -- 수강 시작일 (recency용)
    CONSTRAINT fk_enroll_student FOREIGN KEY (student_id) REFERENCES student(id),
    CONSTRAINT fk_enroll_course  FOREIGN KEY (course_id)  REFERENCES course(id),
    CONSTRAINT uq_enrollment UNIQUE (student_id, course_id)     -- 중복 수강 방지
) ENGINE=InnoDB;

-- 과제 (강좌에 속함)
CREATE TABLE assignment (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id  BIGINT       NOT NULL,
    title      VARCHAR(100) NOT NULL,
    due_date   DATE         NOT NULL,
    CONSTRAINT fk_assign_course FOREIGN KEY (course_id) REFERENCES course(id)
) ENGINE=InnoDB;

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
) ENGINE=InnoDB;
