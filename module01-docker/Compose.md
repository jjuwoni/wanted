# Docker Compose, Spring Boot, MySQL 실습

이 문서는 앞선 Docker Network와 Volume 실습에서 직접 실행했던 여러 `docker run` 명령을 Docker Compose로 정리하는 과정이다. Compose를 사용하면 Spring Boot와 MySQL처럼 여러 컨테이너로 구성된 애플리케이션을 하나의 YAML 파일로 정의하고 실행할 수 있다.

모든 명령어는 Bash 기준이다. Windows에서는 Git Bash 또는 WSL을 사용할 수 있다.

## 학습 목표

- Docker Compose의 역할을 이해한다.
- `compose.yaml` 파일로 서비스, 네트워크, 볼륨을 선언한다.
- MySQL과 Spring Boot 컨테이너를 한 번에 실행한다.
- Compose의 서비스 이름을 이용한 컨테이너 간 통신을 확인한다.
- `docker compose up`, `ps`, `logs`, `exec`, `down` 명령을 실습한다.

## 최종 구성

```text
브라우저
   │
   │ localhost:8080
   ▼
Compose Service: app
   │
   │ db:3306
   ▼
Compose Service: db
   │
   │ /var/lib/mysql
   ▼
Compose Volume: mysql-volume

두 서비스는 wanted-network에 연결된다.
```

## 실습에서 사용할 이름

| 구분 | 이름 |
|---|---|
| Compose 파일 | `compose.yaml` |
| Compose 프로젝트 | `test-docker` |
| Spring Boot 서비스 | `app` |
| MySQL 서비스 | `db` |
| Docker Network | `wanted-network` |
| Docker Volume | `mysql-volume` |
| 데이터베이스 | `menudb` |
| DB 사용자/비밀번호 | `wanted` / `wanted` |
| Spring Boot 이미지 | `test-docker:1.0` |
| MySQL 이미지 | `mysql:8.4` |

---

# 1. Docker Compose 이론

## 1-1. Docker Compose란?

Docker Compose는 여러 컨테이너로 구성된 애플리케이션을 하나의 설정 파일로 정의하고 실행하는 도구다.

앞선 실습에서는 다음 작업을 각각 명령어로 실행했다.

- Docker Network 생성
- Docker Volume 생성
- MySQL 컨테이너 실행
- Spring Boot 이미지 빌드
- Spring Boot 컨테이너 실행
- 로그 확인
- 컨테이너 삭제

Compose를 사용하면 이 구성을 `compose.yaml`에 선언하고 다음 한 줄로 실행할 수 있다.

```bash
docker compose up -d
```

Compose는 파일에 작성된 서비스를 읽고 필요한 이미지, 컨테이너, 네트워크, 볼륨을 생성한다.

## 1-2. Compose의 주요 개념

| 항목 | 설명 |
|---|---|
| `services` | 실행할 컨테이너 목록 |
| `image` | 컨테이너를 만들 때 사용할 이미지 |
| `build` | Dockerfile로 이미지를 빌드할 위치 |
| `ports` | 호스트 포트와 컨테이너 포트 연결 |
| `environment` | 컨테이너에 전달할 환경변수 |
| `depends_on` | 서비스 실행 순서 지정 |
| `networks` | 서비스가 연결될 네트워크 |
| `volumes` | 컨테이너 데이터 저장 공간 |

Compose에서는 컨테이너 이름보다 **서비스 이름**이 더 중요하다. 같은 Compose 네트워크에 있는 서비스는 서비스 이름으로 서로 통신할 수 있다.

이번 실습에서 Spring Boot는 MySQL에 다음 주소로 접속한다.

```text
db:3306
```

- `db`: MySQL 서비스 이름
- `3306`: MySQL 컨테이너 내부 포트

---

# 2. Compose 파일 작성

## 2-1. compose.yaml 생성

프로젝트 최상위 디렉터리에 `compose.yaml` 파일을 만든다고 가정한다.

```yaml
services:
  db:
    image: mysql:8.4
    container_name: mysql-db
    environment:
      MYSQL_ROOT_PASSWORD: 1234
      MYSQL_DATABASE: menudb
      MYSQL_USER: wanted
      MYSQL_PASSWORD: wanted
    ports:
      - "3306:3306"
    volumes:
      - mysql-volume:/var/lib/mysql
    networks:
      - wanted-network
    healthcheck:
      test: [ "CMD", "mysqladmin", "ping", "-h", "localhost", "-uroot", "-p1234" ]
      interval: 5s
      timeout: 3s
      retries: 10

  app:
    build:
      # Docekr build 디렉토리 설정
      context: .
    image: test-docker:1.0
    container_name: test-docker-app
    environment:
      DB_HOST: db
      DB_PORT: 3306
      DB_NAME: menudb
      DB_USERNAME: wanted
      DB_PASSWORD: wanted
    ports:
      - "8080:8080"
    depends_on:
      db:
        condition: service_healthy
    networks:
      - wanted-network

volumes:
  mysql-volume:

networks:
  wanted-network:
```

이 파일은 앞선 README에서 직접 실행했던 Docker 명령을 선언형 설정으로 바꾼 것이다.

## 2-2. 서비스 설명

### db 서비스

`db` 서비스는 MySQL 컨테이너를 담당한다.

| 설정 | 설명 |
|---|---|
| `image: mysql:8.4` | MySQL 8.4 이미지 사용 |
| `container_name: mysql-db` | 실제 컨테이너 이름 지정 |
| `environment` | MySQL 초기 DB, 계정, 비밀번호 설정 |
| `ports` | 호스트의 3306 포트를 MySQL 컨테이너에 연결 |
| `volumes` | MySQL 데이터 디렉터리를 Volume에 저장 |
| `networks` | `wanted-network`에 연결 |
| `healthcheck` | MySQL 접속 준비 상태 확인 |

MySQL 이미지의 초기화 환경변수는 비어 있는 Volume으로 처음 실행할 때 적용된다. 이미 데이터가 들어 있는 Volume을 다시 사용하면 기존 DB와 계정이 유지된다.

### app 서비스

`app` 서비스는 Spring Boot 컨테이너를 담당한다.

| 설정 | 설명 |
|---|---|
| `build.context: .` | 현재 디렉터리의 Dockerfile로 이미지 빌드 |
| `image: test-docker:1.0` | 빌드 결과 이미지 이름 지정 |
| `container_name: test-docker-app` | 실제 컨테이너 이름 지정 |
| `environment` | Spring Boot에 DB 접속 정보 전달 |
| `ports` | 호스트의 8080 포트를 Spring Boot 컨테이너에 연결 |
| `depends_on` | `db` 서비스가 준비된 뒤 실행 |
| `networks` | `wanted-network`에 연결 |

`DB_HOST` 값이 `db`인 이유는 Compose 네트워크 안에서 MySQL 서비스 이름이 `db`이기 때문이다. 이 값은 `src/main/resources/application.yaml`의 다음 설정에 주입된다.

```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:menudb}
    username: ${DB_USERNAME:wanted}
    password: ${DB_PASSWORD:wanted}
```

따라서 컨테이너 안에서 최종 JDBC URL은 다음과 같다.

```text
jdbc:mysql://db:3306/menudb
```

---

# 3. Compose 실행 전 준비

## 3-1. 기존 컨테이너 정리

앞선 README 실습에서 실행한 컨테이너가 남아 있으면 이름이나 포트가 충돌할 수 있다.

```bash
docker rm -f test-docker-app mysql-db
```

기존 Network와 Volume까지 완전히 새로 시작하려면 다음 명령도 실행한다.

```bash
docker network rm wanted-network
docker volume rm mysql-volume
```

Volume을 삭제하면 MySQL 데이터도 삭제된다. 기존 데이터를 유지하고 싶다면 `docker volume rm mysql-volume`은 실행하지 않는다.

## 3-2. JAR 빌드

이 프로젝트의 Dockerfile은 이미 빌드된 JAR 파일을 이미지에 복사한다. Compose로 이미지를 빌드하기 전에 JAR을 먼저 생성한다.

```bash
./gradlew clean bootJar
```

빌드 결과를 확인한다.

```bash
ls -al build/libs
```

`test-docker-0.0.1-SNAPSHOT.jar` 파일이 있어야 한다.

---

# 4. Compose로 전체 실행

## 4-1. 서비스 실행

```bash
docker compose up -d
```

이 명령은 `compose.yaml`을 읽고 다음 작업을 수행한다.

1. `app` 서비스 이미지를 Dockerfile로 빌드한다.
2. `mysql:8.4` 이미지를 로컬에 없으면 내려받는다.
3. `wanted-network`를 생성한다.
4. `mysql-volume`을 생성한다.
5. `db` 서비스를 실행한다.
6. `db` 서비스가 healthcheck를 통과하면 `app` 서비스를 실행한다.

`-d` 옵션은 컨테이너를 백그라운드에서 실행한다.

## 4-2. 실행 상태 확인

```bash
docker compose ps
```

`db`와 `app` 서비스가 모두 실행 중이어야 한다.

일반 Docker 명령으로도 확인할 수 있다.

```bash
docker ps
```

출력에서 `mysql-db`와 `test-docker-app` 컨테이너가 모두 `Up` 상태인지 확인한다.

## 4-3. 로그 확인

전체 서비스 로그를 확인한다.

```bash
docker compose logs -f
```

특정 서비스의 로그만 확인할 수도 있다.

```bash
docker compose logs -f db
docker compose logs -f app
```

MySQL 로그에는 `ready for connections`가 출력되어야 한다. Spring Boot 로그에는 다음과 비슷한 내용이 출력되어야 한다.

```text
Started TestDockerApplication
```

`Ctrl+C`를 눌러 로그 조회만 종료한다. 컨테이너는 계속 실행된다.

---

# 5. MySQL 데이터 생성

## 5-1. MySQL 접속

Compose 서비스 이름을 사용해 MySQL 컨테이너 안에서 명령을 실행한다.

```bash
docker compose exec db mysql -uroot -p
```

비밀번호를 묻는 메시지가 나오면 다음 값을 입력한다.

```text
1234
```

## 5-2. 테이블 생성 및 데이터 삽입

MySQL 셸에서 다음 SQL을 실행한다.

```sql
USE menudb;

CREATE TABLE menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price INT NOT NULL
);

INSERT INTO menu (name, price) VALUES
('Americano', 4500),
('Latte', 5000);

SELECT * FROM menu;
```

다음 결과가 조회되어야 한다.

```text
+----+-----------+-------+
| id | name      | price |
+----+-----------+-------+
|  1 | Americano |  4500 |
|  2 | Latte     |  5000 |
+----+-----------+-------+
```

MySQL 셸을 종료한다.

```sql
exit
```

## 5-3. 한 줄 명령으로 데이터 확인

MySQL 셸에 들어가지 않고 바로 조회할 수도 있다.

```bash
docker compose exec db \
  mysql -uwanted -pwanted menudb \
  -e "SELECT * FROM menu;"
```

---

# 6. 브라우저에서 연동 확인

## 6-1. Health Check

브라우저 주소창에 다음 주소를 입력한다.

<http://localhost:8080/health>

예상 결과:

```json
{"status":"UP"}
```

명령어로 확인할 수도 있다.

```bash
curl http://localhost:8080/health
```

## 6-2. 전체 메뉴 조회

브라우저 주소창에 다음 주소를 입력한다.

<http://localhost:8080/menus>

예상 결과:

```json
[
  {
    "id": 1,
    "name": "Americano",
    "price": 4500
  },
  {
    "id": 2,
    "name": "Latte",
    "price": 5000
  }
]
```

이 결과는 다음 흐름이 정상 동작한다는 의미다.

```text
브라우저
  -> app 서비스
  -> wanted-network
  -> db 서비스
  -> mysql-volume의 데이터
```

---

# 7. Compose 주요 명령어

## 7-1. 서비스 목록 확인

```bash
docker compose ps
```

현재 Compose 프로젝트의 서비스 상태를 확인한다.

## 7-2. 로그 확인

```bash
docker compose logs
docker compose logs -f app
docker compose logs -f db
```

`-f` 옵션을 사용하면 새 로그가 계속 출력된다.

## 7-3. 컨테이너 내부 명령 실행

```bash
docker compose exec app sh
docker compose exec db mysql -uwanted -pwanted menudb
```

`docker compose exec`는 실행 중인 서비스 컨테이너 안에서 명령을 실행한다.

## 7-4. 서비스 중지

```bash
docker compose stop
```

컨테이너를 중지하지만 삭제하지 않는다. 다시 시작할 수 있다.

```bash
docker compose start
```

## 7-5. 서비스 삭제

```bash
docker compose down
```

Compose가 만든 컨테이너와 네트워크를 삭제한다. 기본적으로 named volume은 삭제하지 않는다.

Volume까지 삭제하려면 다음 명령을 사용한다.

```bash
docker compose down -v
```

`-v` 옵션을 사용하면 `mysql-volume`과 MySQL 데이터도 삭제된다.

---

# 8. 문제 해결

## `port is already allocated` 오류가 발생하는 경우

호스트의 3306 또는 8080 포트를 다른 프로그램이 사용 중인 상태다. `compose.yaml`의 포트 매핑을 변경한다.

```yaml
ports:
  - "3307:3306"
```

```yaml
ports:
  - "8081:8080"
```

왼쪽 값은 호스트 포트이고 오른쪽 값은 컨테이너 포트다. Spring Boot와 MySQL 사이의 내부 통신은 `db:3306`을 사용하므로 MySQL의 호스트 포트를 바꾸어도 `DB_HOST`, `DB_PORT`를 변경할 필요는 없다.

## app 서비스가 db 서비스에 연결하지 못하는 경우

먼저 두 서비스 상태를 확인한다.

```bash
docker compose ps
```

로그를 확인한다.

```bash
docker compose logs db
docker compose logs app
```

`app` 서비스의 `DB_HOST`가 `db`인지 확인한다.

```yaml
environment:
  DB_HOST: db
```

Compose 네트워크 안에서는 컨테이너 이름보다 서비스 이름을 사용하는 것이 자연스럽다.

## `/menus` 결과가 빈 배열인 경우

MySQL에 데이터가 있는지 확인한다.

```bash
docker compose exec db \
  mysql -uwanted -pwanted menudb \
  -e "SELECT * FROM menu;"
```

데이터가 없으면 5장의 SQL을 다시 실행한다.

## Dockerfile 또는 JAR 변경이 반영되지 않는 경우

JAR을 다시 빌드하고 Compose 이미지를 다시 빌드한다.

```bash
./gradlew clean bootJar
docker compose build --no-cache app
docker compose up -d
```

---

# 9. 실습 종료 및 리소스 정리

## 9-1. 컨테이너와 네트워크 삭제

```bash
docker compose down
```

컨테이너와 Compose 네트워크를 삭제한다. Volume은 유지된다.

## 9-2. Volume까지 삭제

```bash
docker compose down -v
```

MySQL 데이터까지 삭제한다. 데이터 영속성 확인이 끝난 뒤에만 실행한다.

## 9-3. 이미지 삭제 선택 사항

```bash
docker image rm test-docker:1.0 mysql:8.4
```

실습에서 사용한 이미지를 로컬에서 삭제한다.

## 핵심 정리

- Docker Compose는 여러 컨테이너 구성을 YAML 파일로 관리한다.
- `services`에는 실행할 컨테이너 구성을 작성한다.
- 같은 Compose 네트워크의 서비스는 서비스 이름으로 통신할 수 있다.
- Spring Boot 컨테이너는 `db:3306`으로 MySQL에 접속한다.
- `docker compose up -d`는 필요한 컨테이너, 네트워크, 볼륨을 생성하고 실행한다.
- `docker compose down`은 컨테이너와 네트워크를 삭제하지만 기본적으로 Volume은 유지한다.
- `docker compose down -v`는 Volume까지 삭제하므로 MySQL 데이터도 제거된다.

## 참고 공식 문서

- Docker Compose: <https://docs.docker.com/compose/>
- Compose file reference: <https://docs.docker.com/reference/compose-file/>
- Compose networking: <https://docs.docker.com/compose/how-tos/networking/>
- `docker compose up`: <https://docs.docker.com/reference/cli/docker/compose/up/>
- `docker compose down`: <https://docs.docker.com/reference/cli/docker/compose/down/>
