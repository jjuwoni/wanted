-- Section 02: 로깅 시스템 테스트를 위한 초기 데이터

-- 테스트용 회원 데이터 생성
-- 로깅 테스트를 위한 기본 회원 정보를 미리 생성한다

INSERT INTO tbl_member (member_name, email, phone, join_date, active) VALUES
('김도서', 'kim.doseo@example.com', '010-1234-5678', CURRENT_TIMESTAMP, true),
('이독서', 'lee.dokseo@example.com', '010-2345-6789', CURRENT_TIMESTAMP, true),
('박책방', 'park.chaekbang@example.com', '010-3456-7890', CURRENT_TIMESTAMP, true),
('최문학', 'choi.munhak@example.com', '010-4567-8901', CURRENT_TIMESTAMP, false),
('정지식', 'jung.jisik@example.com', '010-5678-9012', CURRENT_TIMESTAMP, true);