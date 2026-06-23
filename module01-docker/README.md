# Docker Network, Volume, Spring Boot 실습

이 문서는 Docker Network와 Volume을 학습한 뒤, MySQL과 Spring Boot 컨테이너를 연결하는 전체 실습 과정이다. 위에서부터 순서대로 명령어를 실행한다.

모든 명령어는 Bash 기준이다. Windows에서는 Git Bash 또는 WSL을 사용할 수 있다.

## 학습 목표

- Docker Network의 역할을 이해하고 사용자 정의 Network를 생성한다.
- Docker Volume을 이용해 MySQL 데이터를 영구 저장한다.
- MySQL 컨테이너를 삭제하고 재생성해도 데이터가 유지되는지 확인한다.
- Spring Boot와 MySQL 컨테이너를 같은 Network로 연결한다.
- 브라우저에서 Spring Boot API를 호출해 컨테이너 연동을 확인한다.

## 최종 구성

```text
브라우저
   │
   │ localhost:8080
   ▼
Spring Boot Container (test-docker-app)
   │
   │ mysql-db:3306
   ▼
MySQL Container (mysql-db)
   │
   │ /var/lib/mysql
   ▼
Docker Volume (mysql-volume)

두 컨테이너는 wanted-network에 연결된다.
```

## 실습에서 사용할 이름

| 구분 | 이름 |
|---|---|
| Docker Network | `wanted-network` |
| Docker Volume | `mysql-volume` |
| MySQL 컨테이너 | `mysql-db` |
| MySQL 이미지 | `mysql:8.4` |
| 데이터베이스 | `menudb` |
| DB 사용자/비밀번호 | `wanted` / `wanted` |
| Spring Boot 컨테이너 | `test-docker-app` |
| Spring Boot 이미지 | `test-docker:1.0` |

---

# 1. Docker Network 이론 및 실습

## 1-1. Docker Network란?

Docker Network는 컨테이너가 서로 통신할 수 있도록 연결하는 가상 네트워크다.

사용자 정의 bridge network에 연결된 컨테이너는 Docker DNS를 통해 **컨테이너 이름**으로 서로를 찾을 수 있다. 컨테이너 IP는 재생성할 때 변경될 수 있으므로 IP보다 컨테이너 이름을 사용하는 것이 적절하다.

이번 실습에서 Spring Boot는 MySQL에 다음 주소로 접속한다.

```text
mysql-db:3306
```

- `mysql-db`: MySQL 컨테이너 이름
- `3306`: MySQL 컨테이너 내부 포트

## 1-2. Network 생성

```bash
docker network create wanted-network
```

이 명령은 Docker Engine에 `wanted-network`라는 사용자 정의 bridge network를 생성하도록 요청한다. 아직 컨테이너를 연결하지 않았기 때문에 생성 직후 Network의 `Containers` 항목은 비어 있다.

- `docker network create`: 새로운 Network 생성
- `wanted-network`: 생성할 Network 이름

생성 결과를 확인한다.

```bash
docker network ls
docker network inspect wanted-network
```

- `docker network ls`는 Docker에 존재하는 모든 Network의 ID, 이름, 드라이버를 출력한다.
- `docker network inspect`는 IP 대역, Gateway, 연결된 컨테이너 등 특정 Network의 상세 정보를 JSON 형식으로 출력한다.

`docker network ls` 결과에 `wanted-network`가 표시되어야 한다.

---

# 2. Docker Volume 이론 및 실습

## 2-1. Docker Volume이란?

컨테이너의 writable layer에 저장된 데이터는 컨테이너를 중지하거나 다시 시작할 때는 유지된다. 하지만 컨테이너를 **삭제하면** 해당 데이터도 함께 제거된다.

Docker Volume은 컨테이너와 별도로 관리되는 저장 공간이다. MySQL의 데이터 디렉터리 `/var/lib/mysql`을 Volume에 연결하면 MySQL 컨테이너를 삭제해도 데이터는 유지된다.

```text
mysql-db:/var/lib/mysql
          │
          ▼
      mysql-volume
```

## 2-2. Volume 생성

```bash
docker volume create mysql-volume
```

이 명령은 Docker가 관리하는 저장 공간을 만들고 이름을 `mysql-volume`로 지정한다. 아직 MySQL과 연결하지 않았지만 Volume 자체는 컨테이너와 독립된 Docker 리소스로 존재한다.

생성 결과를 확인한다.

```bash
docker volume ls
docker volume inspect mysql-volume
```

- `docker volume ls`는 생성된 Volume 목록을 출력한다.
- `docker volume inspect`는 Volume의 드라이버와 Docker 호스트 내부 저장 위치 등의 상세 정보를 출력한다.
- Docker Desktop 환경에서는 표시된 실제 저장 경로가 Docker Desktop의 Linux VM 내부 경로일 수 있다.

`docker volume ls` 결과에 `mysql-volume`이 표시되어야 한다.

---

# 3. MySQL 컨테이너와 데이터 생성


## 3-1. MySQL 컨테이너 실행

```bash
docker run -d \
  --name mysql-db \
  --network wanted-network \
  -v mysql-volume:/var/lib/mysql \
  -e MYSQL_ROOT_PASSWORD=1234 \
  -e MYSQL_DATABASE=menudb \
  -e MYSQL_USER=wanted \
  -e MYSQL_PASSWORD=wanted \
  -p 3306:3306 \
  mysql:8.4
```

`docker run`은 이미지로부터 새로운 컨테이너를 생성한 후 실행한다. 로컬에 `mysql:8.4` 이미지가 없다면 Docker Hub에서 먼저 내려받는다.

실행 과정은 다음과 같다.

1. `mysql:8.4` 이미지로 `mysql-db` 컨테이너를 생성한다.
2. 컨테이너를 `wanted-network`에 연결한다.
3. `mysql-volume`을 컨테이너의 `/var/lib/mysql`에 마운트한다.
4. 환경변수를 MySQL 이미지의 초기화 스크립트에 전달한다.
5. 호스트와 컨테이너의 3306 포트를 연결한다.
6. `-d` 옵션에 따라 컨테이너를 백그라운드에서 실행하고 컨테이너 ID를 출력한다.

### 옵션 설명

| 옵션 | 설명 |
|---|---|
| `--name mysql-db` | MySQL 컨테이너 이름 지정 |
| `--network wanted-network` | 사용자 정의 Network 연결 |
| `-v mysql-volume:/var/lib/mysql` | MySQL 데이터 경로에 Volume 연결 |
| `MYSQL_ROOT_PASSWORD` | root 계정 비밀번호 설정 |
| `MYSQL_DATABASE` | 최초 실행 시 `menudb` 생성 |
| `MYSQL_USER` | 최초 실행 시 `wanted` 사용자 생성 |
| `MYSQL_PASSWORD` | `wanted` 사용자의 비밀번호 설정 |
| `-p 3306:3306` | 호스트 3306 포트를 컨테이너 3306 포트에 연결 |

`-p`의 형식은 `호스트 포트:컨테이너 포트`이다. 이 포트 매핑은 호스트의 DB 도구가 MySQL에 접속할 때 사용한다. 같은 Network의 Spring Boot 컨테이너는 포트 매핑을 거치지 않고 `mysql-db:3306`으로 직접 접속한다.

MySQL 초기화 환경변수는 비어 있는 Volume으로 처음 실행할 때 적용된다. 기존 데이터가 있는 Volume을 연결하면 기존 DB와 계정을 그대로 사용한다.

## 3-2. MySQL 실행 상태 확인

```bash
docker ps
docker logs -f mysql-db
```

- `docker ps`는 현재 실행 중인 컨테이너만 보여준다. `mysql-db`의 상태가 `Up`인지 확인한다.
- `docker logs`는 컨테이너의 표준 출력과 표준 오류 로그를 조회한다.
- `-f`는 새 로그가 생길 때마다 계속 출력하는 follow 옵션이다.

로그에 `ready for connections`가 출력되면 MySQL을 사용할 수 있다. `Ctrl+C`를 눌러 로그 조회만 종료한다. MySQL 컨테이너는 계속 실행된다.

> 호스트의 3306 포트를 이미 사용 중이라면 `-p 3307:3306`으로 변경한다. 컨테이너 간 통신에는 계속 `mysql-db:3306`을 사용한다.

## 3-3. MySQL 접속

```bash
docker exec -it mysql-db mysql -uroot -p
```

`docker exec`는 이미 실행 중인 컨테이너 안에서 새로운 명령을 실행한다. 이 명령은 `mysql-db` 안에 설치된 `mysql` 클라이언트를 실행하여 MySQL 서버에 접속한다.

- `-i`: 표준 입력을 열린 상태로 유지한다.
- `-t`: 입력할 수 있는 터미널을 할당한다.
- `mysql-db`: 명령을 실행할 컨테이너 이름
- `mysql`: 컨테이너 안에서 실행할 프로그램
- `-uroot`: `root` 사용자로 접속
- `-p`: 비밀번호를 화면에 직접 작성하지 않고 입력 프롬프트로 받음

비밀번호를 묻는 메시지가 나오면 다음 값을 입력한다.

```text
사용자: root
비밀번호: 1234
```

## 3-4. 테이블 생성 및 데이터 삽입

MySQL 셸에서 다음 SQL을 순서대로 실행한다.

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

각 SQL이 수행하는 작업은 다음과 같다.

- `USE menudb`: 이후 SQL을 실행할 데이터베이스를 선택한다.
- `CREATE TABLE`: 메뉴를 저장할 `menu` 테이블을 생성한다.
- `AUTO_INCREMENT`: 데이터 삽입 시 `id` 값을 MySQL이 자동으로 증가시킨다.
- `INSERT INTO`: 두 개의 메뉴 레코드를 Volume에 연결된 MySQL 데이터 영역에 저장한다.
- `SELECT`: 저장된 레코드를 조회하여 삽입 결과를 확인한다.

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

`exit`는 MySQL 클라이언트만 종료한다. MySQL 서버가 실행 중인 Docker 컨테이너는 종료되지 않는다.

---

# 4. Volume 데이터 영속성 확인

단순히 컨테이너를 중지하고 다시 시작하면 컨테이너 자체가 남아 있기 때문에 Volume의 효과를 명확하게 확인하기 어렵다. 이번 실습에서는 MySQL 컨테이너를 **중지하고 삭제한 뒤**, 같은 Volume을 연결해 새 컨테이너를 만든다.

## 4-1. MySQL 컨테이너 중지 및 삭제

```bash
docker stop mysql-db
docker rm mysql-db
```

- `docker stop`은 MySQL 프로세스가 정상적으로 종료할 시간을 준 뒤 컨테이너를 중지한다. 컨테이너 객체는 아직 남아 있다.
- `docker rm`은 중지된 컨테이너 객체와 writable layer를 삭제한다.
- `mysql-volume`은 별도의 Docker 리소스이므로 `docker rm`으로 삭제되지 않는다.

컨테이너가 삭제되었는지 확인한다.

```bash
docker ps -a
```

`-a`를 사용하면 실행 중인 컨테이너뿐 아니라 중지된 컨테이너도 표시한다. 삭제가 완료되었다면 목록에 `mysql-db`가 없어야 한다.

Volume은 삭제하지 않았으므로 계속 남아 있어야 한다.

```bash
docker volume ls
docker volume inspect mysql-volume
```

이 단계에서는 컨테이너가 없어도 `mysql-volume`이 남아 있음을 확인한다. Volume 안에는 앞에서 생성한 `menudb`, `menu` 테이블, 메뉴 레코드가 저장되어 있다.

## 4-2. 같은 Volume으로 MySQL 컨테이너 재생성

```bash
docker run -d \
  --name mysql-db \
  --network wanted-network \
  -v mysql-volume:/var/lib/mysql \
  -e MYSQL_ROOT_PASSWORD=1234 \
  -e MYSQL_DATABASE=menudb \
  -e MYSQL_USER=wanted \
  -e MYSQL_PASSWORD=wanted \
  -p 3306:3306 \
  mysql:8.4
```

Docker는 같은 이름의 새 `mysql-db` 컨테이너를 만들지만, 기존 `mysql-volume`을 다시 `/var/lib/mysql`에 연결한다. MySQL은 비어 있지 않은 데이터 디렉터리를 발견하므로 새 DB를 초기화하지 않고 기존 데이터를 읽어 서버를 시작한다.

MySQL이 준비될 때까지 로그를 확인한다.

```bash
docker logs -f mysql-db
```

컨테이너가 생성된 직후에는 MySQL 초기화와 서버 시작에 시간이 필요하다. 데이터 조회 전에 이 명령으로 접속 준비가 완료되었는지 확인한다.

`ready for connections`가 출력되면 `Ctrl+C`를 누른다.

## 4-3. 기존 데이터 확인

다음 명령은 MySQL 셸에 들어가지 않고 바로 데이터를 조회한다.

```bash
docker exec mysql-db \
  mysql -uwanted -pwanted menudb \
  -e "SELECT * FROM menu;"
```

이 명령도 `docker exec`로 컨테이너 내부의 `mysql` 프로그램을 실행하지만, 대화형 셸에는 들어가지 않는다.

- `-uwanted`: `wanted` 사용자로 접속
- `-pwanted`: 비밀번호 `wanted`를 명령행으로 전달
- `menudb`: 접속 후 사용할 데이터베이스
- `-e`: 뒤에 작성한 SQL을 실행하고 결과를 출력한 뒤 클라이언트 종료

교육 실습에서는 간단한 확인을 위해 비밀번호를 명령행에 작성한다. 실제 운영 환경에서는 명령 기록과 프로세스 정보에 비밀번호가 노출될 수 있으므로 Secret 관리 방식을 사용해야 한다.

`Americano`와 `Latte`가 다시 조회되면 다음 사실을 확인한 것이다.

```text
MySQL 컨테이너 삭제
        │
        ▼
mysql-volume 유지
        │
        ▼
새 MySQL 컨테이너에서 기존 데이터 조회
```

---

# 5. Spring Boot 애플리케이션 준비

## 5-1. 프로젝트 기능

이 프로젝트는 다음 API를 제공한다.

| Method | URL | 기능 |
|---|---|---|
| `GET` | `/health` | Spring Boot 애플리케이션 상태 확인 |
| `GET` | `/menus` | MySQL의 전체 메뉴 조회 |

`/menus` 요청은 Spring Data JPA를 통해 MySQL의 `menu` 테이블을 조회한다.

## 5-2. 데이터베이스 설정

`application.yaml`은 환경변수를 이용해 DB 접속 정보를 변경할 수 있다.

```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:menudb}
    username: ${DB_USERNAME:wanted}
    password: ${DB_PASSWORD:wanted}
```

Dockerfile에는 다음 환경변수가 설정되어 있다.

```dockerfile
ENV DB_HOST=mysql-db \
    DB_PORT=3306
```

따라서 Spring Boot 컨테이너의 최종 JDBC URL은 다음과 같다.

```text
jdbc:mysql://mysql-db:3306/menudb
```

## 5-3. Spring Boot JAR 빌드

프로젝트 최상위 디렉터리에서 실행한다.

```bash
./gradlew clean bootJar
```

이 명령은 프로젝트에 포함된 Gradle Wrapper를 사용한다.

- `./gradlew`: 프로젝트가 지정한 Gradle 버전으로 작업 실행
- `clean`: 기존 `build` 디렉터리의 산출물 제거
- `bootJar`: 의존성과 애플리케이션 코드를 포함한 실행 가능한 Spring Boot JAR 생성

명령이 성공하면 `build/libs`에 JAR 파일이 만들어진다. 이 파일은 다음 단계의 Docker 이미지에 포함된다.

Windows Git Bash에서 실행 권한 오류가 발생하면 다음 명령을 먼저 실행한다.

```bash
chmod +x gradlew
./gradlew clean bootJar
```

`chmod +x gradlew`는 Gradle Wrapper 스크립트에 실행 권한을 추가한다. 한 번 권한을 부여했다면 이후에는 반복할 필요가 없다.

빌드 결과를 확인한다.

```bash
ls -al build/libs
```

`ls -al`은 숨김 파일을 포함한 상세 파일 목록을 보여준다. JAR의 파일명, 크기, 생성 시간을 확인할 수 있다.

`test-docker-0.0.1-SNAPSHOT.jar` 파일이 있어야 한다.

## 5-4. Spring Boot Docker 이미지 생성

```bash
docker build -t test-docker:1.0 .
docker image ls test-docker
```

- `docker build`는 현재 디렉터리의 Dockerfile을 읽어 이미지를 생성한다.
- `-t test-docker:1.0`은 이미지 이름을 `test-docker`, 태그를 `1.0`으로 지정한다.
- 마지막 `.`은 현재 디렉터리를 build context로 전달한다는 의미다.
- Dockerfile의 `COPY`는 build context의 `build/libs/*.jar`를 이미지 내부 `/app/app.jar`로 복사한다.
- `docker image ls test-docker`는 생성된 이미지의 태그, ID, 크기를 확인한다.

---

# 6. Spring Boot와 MySQL 컨테이너 연결

## 6-1. MySQL 컨테이너 확인

```bash
docker ps
```

Spring Boot를 실행하기 전에 MySQL이 `Up` 상태인지 확인한다. 중지되어 있다면 `docker start mysql-db`로 다시 시작한다.

`mysql-db`가 실행 중이어야 한다.

## 6-2. Spring Boot 컨테이너 실행

MySQL과 동일한 `wanted-network`에 연결한다.

```bash
docker run -d \
  --name test-docker-app \
  --network wanted-network \
  -p 8080:8080 \
  test-docker:1.0
```

Docker는 `test-docker:1.0` 이미지로 `test-docker-app` 컨테이너를 생성한다. Dockerfile의 `CMD`에 따라 `java -jar app.jar`가 실행되며 Spring Boot가 시작된다.

`--network wanted-network`를 통해 MySQL과 같은 Network에 연결된다. Dockerfile의 `DB_HOST=mysql-db` 설정을 Spring Boot가 읽으면 Docker DNS가 `mysql-db`를 MySQL 컨테이너의 내부 IP로 변환한다.

`-p 8080:8080`은 브라우저의 `localhost:8080` 요청을 Spring Boot 컨테이너의 8080 포트로 전달한다.

현재 실행 중인 컨테이너를 확인한다.

```bash
docker ps
```

출력에서 두 컨테이너의 `STATUS`가 `Up`이어야 한다. `PORTS` 항목에서는 MySQL의 `3306->3306`, Spring Boot의 `8080->8080` 매핑을 확인할 수 있다.

`mysql-db`와 `test-docker-app`이 모두 표시되어야 한다.

## 6-3. Spring Boot 로그 확인

```bash
docker logs -f test-docker-app
```

이 로그에는 Spring Boot 초기화, JPA 설정, MySQL 연결, 내장 Tomcat 시작 과정이 출력된다. DB 연결에 실패했다면 `Communications link failure` 또는 인증 관련 오류가 표시된다.

Spring Boot가 정상적으로 시작되면 로그에 다음과 비슷한 내용이 출력된다.

```text
Started TestDockerApplication
```

`Ctrl+C`를 눌러 로그 조회만 종료한다.

## 6-4. Network 연결 상태 확인

```bash
docker network inspect wanted-network
```

Docker가 Network의 상세 정보를 JSON으로 출력한다. `Containers` 항목에 두 컨테이너의 이름과 내부 IP가 등록되어 있는지 확인한다. 이 IP를 직접 설정할 필요는 없다.

출력의 `Containers` 항목에서 다음 두 컨테이너를 확인한다.

```text
mysql-db
test-docker-app
```

---

# 7. 브라우저에서 최종 연동 확인

## 7-1. Health Check

브라우저 주소창에 다음 주소를 입력한다.

<http://localhost:8080/health>

예상 결과:

```json
{"status":"UP"}
```

이 결과는 Spring Boot 컨테이너가 정상적으로 실행 중임을 의미한다.

## 7-2. 전체 메뉴 조회

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

이 결과를 통해 다음 연결이 모두 정상임을 확인할 수 있다.

```text
브라우저
  -> Spring Boot 컨테이너
  -> wanted-network
  -> MySQL 컨테이너
  -> mysql-volume의 기존 데이터
```

명령어로도 확인할 수 있다.

```bash
curl http://localhost:8080/health
curl http://localhost:8080/menus
```

`curl`은 HTTP 요청을 보내고 응답 본문을 터미널에 출력한다. 첫 번째 요청은 Spring Boot 자체의 실행 상태를, 두 번째 요청은 Spring Boot에서 MySQL까지의 전체 연결을 확인한다.

---

# 8. 문제 해결

## MySQL 컨테이너가 바로 종료되는 경우

```bash
docker logs mysql-db
```

종료 원인이 되는 초기화 오류, 포트 문제, 데이터 파일 문제 등을 확인한다. `-f`가 없으므로 현재까지 저장된 로그를 출력하고 명령이 종료된다.

동일한 이름의 컨테이너가 이미 있으면 기존 컨테이너를 확인하거나 삭제한다.

```bash
docker ps -a
docker rm -f mysql-db
```

`docker rm -f`는 실행 여부와 관계없이 컨테이너를 강제로 중지하고 삭제한다. 데이터가 필요한 상황에서는 Volume 연결 여부를 먼저 확인해야 한다.

## `port is already allocated` 오류가 발생하는 경우

호스트의 3306 포트를 다른 프로그램이 사용 중인 상태다. MySQL 실행 명령의 포트를 다음과 같이 변경할 수 있다.

```bash
-p 3307:3306
```

Spring Boot 컨테이너는 같은 Network에서 `mysql-db:3306`으로 직접 접속하므로 다른 설정을 변경할 필요가 없다.

## Spring Boot 컨테이너가 MySQL에 연결하지 못하는 경우

두 컨테이너가 실행 중인지 확인한다.

```bash
docker ps
```

두 컨테이너가 같은 Network에 연결되어 있는지 확인한다.

```bash
docker network inspect wanted-network
```

각 컨테이너의 로그를 확인한다.

```bash
docker logs mysql-db
docker logs test-docker-app
```

## `/menus` 결과가 빈 배열인 경우

MySQL 데이터가 있는지 확인한다.

```bash
docker exec mysql-db \
  mysql -uwanted -pwanted menudb \
  -e "SELECT * FROM menu;"
```

---

# 9. 실습 종료 및 리소스 정리

## 9-1. 컨테이너 삭제

```bash
docker rm -f test-docker-app mysql-db
```

두 컨테이너를 강제로 중지하고 삭제한다. 이미지, Network, Volume은 삭제되지 않는다.

## 9-2. Network 삭제

```bash
docker network rm wanted-network
```

사용자 정의 Network를 삭제한다. 실행 중인 컨테이너가 연결되어 있으면 삭제할 수 없으므로 컨테이너를 먼저 제거해야 한다.

## 9-3. Volume 삭제

Volume을 삭제하면 MySQL 데이터도 제거된다. 데이터 영속성 실습을 모두 마친 후 실행한다.

```bash
docker volume rm mysql-volume
```

Volume과 그 안의 MySQL 데이터를 삭제한다. 컨테이너가 Volume을 사용 중이면 삭제할 수 없다.

## 9-4. 실습 이미지 삭제 선택 사항

```bash
docker image rm test-docker:1.0 mysql:8.4
```

실습에서 사용한 Spring Boot 이미지와 MySQL 이미지를 로컬에서 삭제한다. 해당 이미지를 사용하는 컨테이너가 남아 있으면 먼저 컨테이너를 삭제해야 한다.

## 핵심 정리

- Docker Network는 컨테이너 간 통신을 담당한다.
- 사용자 정의 Network에서는 컨테이너 이름으로 통신할 수 있다.
- 컨테이너 중지와 삭제는 서로 다른 작업이다.
- Docker Volume은 컨테이너의 생명주기와 데이터를 분리한다.
- `/var/lib/mysql`을 Volume에 연결하면 MySQL 컨테이너를 삭제해도 데이터가 유지된다.
- 호스트에서 컨테이너에 접근할 때는 공개 포트를 사용한다.
- 같은 Network의 컨테이너끼리는 컨테이너 이름과 내부 포트를 사용한다.
- `/menus` 조회 결과를 통해 Spring Boot, Docker Network, MySQL, Volume의 전체 연결을 확인할 수 있다.
