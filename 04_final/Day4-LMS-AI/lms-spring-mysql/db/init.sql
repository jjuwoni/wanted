-- =====================================================================
-- db/init.sql — MySQL 최초 준비 (수업 시작 전, 각자 PC에서 '1번만' 실행)
-- ---------------------------------------------------------------------
-- 실행 방법:
--   mysql -u root -p < db/init.sql
--   (또는 Workbench에서 root로 접속해 전체 실행)
--
-- 이 스크립트가 만드는 것 3가지:
--   1. lmsdb      : 수업용 데이터베이스
--   2. lms_user   : Spring이 쓰는 계정 (읽기/쓰기 전체 권한)
--   3. lms_reader : FastAPI '색인 배치'가 쓰는 계정 (SELECT만!)
--
-- ★ 왜 계정을 둘로 나누는가? — 이것 자체가 오늘의 수업 내용이다.
--   AI 서버의 배치는 데이터를 '읽기만' 하면 된다. 실수로라도
--   UPDATE/DELETE를 못 하도록 권한을 최소로 준다(최소 권한 원칙).
--   실무에서는 여기에 더해 읽기 전용 복제본(read replica)을 바라본다.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS lmsdb
  CHARACTER SET utf8mb4          -- 한글 + 이모지까지 안전한 문자셋
  COLLATE utf8mb4_unicode_ci;

-- Spring용 계정 (비밀번호는 수업 편의상 통일 — 실무에선 절대 공유 금지!)
CREATE USER IF NOT EXISTS 'lms_user'@'localhost' IDENTIFIED BY 'lms1234!';
GRANT ALL PRIVILEGES ON lmsdb.* TO 'lms_user'@'localhost';

-- FastAPI 색인 배치용 읽기 전용 계정
CREATE USER IF NOT EXISTS 'lms_reader'@'localhost' IDENTIFIED BY 'reader1234!';
GRANT SELECT ON lmsdb.* TO 'lms_reader'@'localhost';

FLUSH PRIVILEGES;

-- 확인: 아래 두 줄이 각각 성공하면 준비 끝
--   mysql -u lms_user  -p'lms1234!'    -e "USE lmsdb; SELECT 1;"
--   mysql -u lms_reader -p'reader1234!' -e "USE lmsdb; SELECT 1;"
