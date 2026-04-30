# Section 03: Bean Validation 활용

도서관 시스템의 데이터 검증을 Bean Validation을 활용하여 체계적으로 구현한다.

## 학습 목표
- 도서 등록/수정 시 기본 검증 어노테이션 적용
- 커스텀 Validator 구현 (ISBN 검증, 출간일 검증)
- 검증 그룹을 활용한 시나리오별 검증
- 중첩 객체 검증 (저자 정보, 출판사 정보)

## 패키지 구조
```
section03/
├── validator/        # 커스텀 Validator 클래스들
├── annotation/       # 커스텀 검증 어노테이션
├── group/           # 검증 그룹 인터페이스
├── controller/      # 회원 관리 컨트롤러
├── service/         # 회원 관리 서비스
├── entity/          # 회원 관련 엔티티
└── dto/             # 회원 관련 DTO
```

## 주요 시나리오
- 회원 가입 시 입력값 검증
- 회원 정보 수정 시 검증
- 도서 등록 시 ISBN 검증
- 출간일 유효성 검증
- 저자/출판사 정보 중첩 검증 