# Section 02: 효과적인 로깅 구현

Spring Boot 애플리케이션에서 체계적이고 효과적인 로깅 시스템을 구현한다.

## 학습 목표
- SLF4J와 Logback을 활용한 로깅 설정
- MDC(Mapped Diagnostic Context)를 통한 요청 추적
- AOP를 활용한 자동 로깅 시스템 구현
- 로그 레벨별 관리 및 파일 분리 전략
- 비즈니스 이벤트 로깅 및 성능 모니터링

## 패키지 구조
```
section02/logging/
├── entity/
│   ├── Member.java          # 회원 엔티티
│   └── Loan.java           # 대출 엔티티
├── dto/
│   └── LoanDTO.java        # 대출 정보 전송 객체
├── repository/
│   ├── MemberRepository.java    # 회원 데이터 접근
│   └── LoanRepository.java     # 대출 데이터 접근
├── service/
│   └── LoanService.java    # 대출 비즈니스 로직
├── controller/
│   └── LoanController.java # 대출 REST API
├── config/
│   └── LoggingConfig.java  # 로깅 설정
├── aspect/
│   └── LoggingAspect.java  # AOP 로깅
└── logging.http            # API 테스트 파일
```

## 주요 시나리오
- 도서 대출 및 반납 프로세스 로깅
- 연체 관리 및 대출 연장 로깅
- 요청별 추적 ID를 통한 로그 추적
- 성능 측정 및 모니터링 로깅
- 예외 상황 및 오류 로깅

## 학습 순서

### 1단계: 로깅 설정 이해
- `logback-spring.xml` 설정 파일 분석
- 콘솔 및 파일 출력 패턴 이해
- 로그 레벨별 색상 구분 및 필터링 설정
- 다중 로그 파일 분리 전략 이해

> **참고: Spring Boot의 Auto-Configuration 항목 중 하나인 DB 초기화 메커니즘**
> 
> Spring Boot는 애플리케이션 시작 시 `classpath` 루트(`src/main/resources/`)에 있는 표준 SQL 스크립트 파일을 찾아 자동으로 실행한다.
> ``` mermaid
> graph LR
>    A["Spring Boot 시작"] --> B("DataSource 연결")
>    B --> C{"defer-init<br>설정 확인"}
>    
>    C -- "true<br>(지연 실행)" --> D["Hibernate:<br>테이블 생성(ddl-auto)"]
>    D --> E["Spring Boot:<br>data.sql 실행"]
>    E --> F["애플리케이션 준비 완료"]
>    
>    C -- "false<br>(기본값)" --> G["Spring Boot:<br>data.sql 실행 시도"]
>    G -- "INSERT INTO..." --> H{"현재<br>테이블이<br>존재하는가?"}
>    H -- "미존재<br>(JPA 사용 시)" --> I["에러 발생:<br>Table not found"]
>    H -- "존재" --> J["Hibernate:<br>테이블 생성(ddl-auto)"]
>    J --> F
> ```
> 
> **1. 표준 스크립트 파일**
> - **`schema.sql`**: 테이블 생성 등 DDL(Data Definition Language) 정의
> - **`data.sql`**: 초기 데이터 삽입 등 DML(Data Manipulation Language) 정의
> 
> **2. 실행 순서와 `defer-datasource-initialization`**
> - **기본 동작 (false)**: Spring Boot는 **Hibernate가 테이블을 만들기 전**에 `data.sql`을 실행한다. JPA Entity로 테이블을 자동 생성하는 경우, `data.sql` 실행 시점에 테이블이 존재하지 않아 `Table not found` 에러가 발생하며 애플리케이션 구동이 실패할 수 있다.
> - **지연 설정 (true)**: `application.yaml`에 `spring.jpa.defer-datasource-initialization: true`를 설정하면, **Hibernate가 테이블 생성을 완료한 후**에 `data.sql`이 실행되도록 순서를 조정한다.
> 
> 이 프로젝트에서는 **로깅 테스트를 위한 회원 데이터 5건**을 이 방식으로 자동 주입하고 있다.

### 2단계: MDC를 통한 요청 추적 시스템
- `LoggingConfig` 클래스의 MDC 설정 분석
- traceId 생성 및 관리 방식 이해
- HTTP 요청 정보 수집 및 로깅
- 요청별 로그 추적 방법 학습

### 3단계: AOP 기반 자동 로깅 시스템
- `LoggingAspect` 클래스 분석
- 메서드 실행 전후 자동 로깅
- 실행 시간 측정 및 성능 모니터링
- 매개변수 및 반환값 로깅 전략

### 4단계: 비즈니스 로직 로깅 구현
- `LoanService`의 비즈니스 이벤트 로깅
- 대출, 반납, 연장 프로세스별 로깅
- 연체 처리 및 예외 상황 로깅
- 로그 메시지 표준화 및 구조화

### 5단계: 로그 파일 분리 및 관리
- 전체 로그 (`library-system.log`)
- 에러 전용 로그 (`library-system-error.log`)
- 비즈니스 로직 로그 (`library-system-business.log`)
- 성능 모니터링 로그 (`library-system-performance.log`)

### 6단계: 실제 API 테스트를 통한 로깅 확인
- `logging.http` 파일의 22개 요청 순차 실행
- 정상 케이스와 예외 케이스 로깅 비교
- 로그 레벨별 동적 변경 테스트
- 성능 로깅 및 모니터링 확인

## 로그 파일 구조
```
logs/
├── library-system.log          # 전체 로그 (모든 레벨)
├── library-system-error.log    # 에러 로그만 (ERROR 레벨)
├── library-system-business.log # 비즈니스 로직 로그
└── library-system-performance.log # 성능 관련 로그
```

## 실습 팁

### 로그 추적하기
1. 요청 시작 시 traceId 확인
2. 동일한 traceId로 전체 요청 흐름 추적
3. 메서드별 실행 시간 비교 분석
4. 예외 발생 시 스택 트레이스 확인

### 로그 레벨 테스트
- DEBUG: 개발 시 상세 디버깅 정보
- INFO: 일반적인 비즈니스 이벤트
- WARN: 주의가 필요한 상황 (연체 등)
- ERROR: 시스템 오류 및 예외 상황

### 성능 모니터링
- 메서드별 실행 시간 측정
- 데이터베이스 쿼리 성능 확인
- 느린 요청 식별 및 분석
- 시스템 부하 상황 모니터링

### 파일 로그 분석
- 실시간 로그 확인: `tail -f logs/library-system.log`
- 에러 로그만 확인: `tail -f logs/library-system-error.log`
- 특정 패턴 검색: `grep "traceId" logs/library-system.log`
- 로그 파일 크기 및 로테이션 확인

## 주요 API 엔드포인트

| 메서드 | 엔드포인트 | 설명 | 로깅 포인트 |
|--------|------------|------|-------------|
| POST | `/api/v1/loans` | 도서 대출 | 대출 생성 이벤트 |
| PATCH | `/api/v1/loans/{id}/return` | 도서 반납 | 반납 처리 및 연체 계산 |
| PATCH | `/api/v1/loans/{id}/extend` | 대출 연장 | 연장 처리 및 제한 확인 |
| GET | `/api/v1/loans/member/{memberId}` | 회원별 대출 목록 | 조회 성능 측정 |
| GET | `/api/v1/loans/overdue` | 연체 대출 목록 | 연체 현황 모니터링 |
| POST | `/api/v1/loans/overdue/process` | 연체 일괄 처리 | 배치 처리 로깅 |
| GET | `/api/v1/loans/status` | 시스템 상태 확인 | 시스템 헬스 체크 |
| POST | `/api/v1/loans/test/logging` | 로깅 테스트 | 로그 레벨별 테스트 |

## 주요 학습 포인트

1. **구조화된 로깅**: 일관된 로그 포맷과 메시지 구조
2. **요청 추적**: MDC를 통한 분산 요청 추적 시스템
3. **자동화된 로깅**: AOP를 활용한 횡단 관심사 처리
4. **성능 모니터링**: 메서드 실행 시간 및 시스템 성능 추적
5. **로그 분리**: 용도별 로그 파일 분리 및 관리 전략
6. **운영 고려사항**: 로그 레벨 관리, 파일 크기 제한, 보안 고려사항

## 실무 적용 가이드

### 로그 레벨 관리 전략
- **개발 환경**: DEBUG 레벨로 상세 정보 확인
- **테스트 환경**: INFO 레벨로 주요 이벤트 추적
- **운영 환경**: WARN 이상으로 설정하여 성능 최적화

### 보안 고려사항
- 개인정보 및 민감 데이터 로깅 금지
- 비밀번호, 토큰 등 보안 정보 마스킹
- 로그 파일 접근 권한 관리

### 성능 최적화
- 과도한 로깅으로 인한 성능 저하 방지
- 비동기 로깅 적용 고려
- 로그 파일 크기 및 보관 정책 설정

### 모니터링 및 알림
- 에러 로그 기반 실시간 알림 시스템
- 로그 분석 도구 연동 (ELK Stack 등)
- 성능 지표 기반 임계값 설정 