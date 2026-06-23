# 1. 실행용 베이스 이미지 선택
# alpine 은 경량 버전으로서 이미지 생성 시
# 용량을 낮출 수 있다.
FROM eclipse-temurin:17-alpine

# 2. 작업 디렉토리 설정
# 컨테이너 내부에서 모든 작업이 수행 될 기본 디렉토리
WORKDIR /app

# 3. JAR 파일 복사
# jar 파일이란? 우리 프로젝트를 zip 으로 압축했다고 보면 된다.
# 우리 프로젝트를 build (압축 파일) 하게 되면
# build/libs 하위에 ~~~.jar 파일이 생성된다.
# app.jar 는 컨테이너 내부에서 사용할 파일명이다.
# docker-0.0.1-SNAPSHOT.jar -> app.jar
COPY build/libs/*.jar app.jar

# 4. Docker Network 내부의  MYSQL 접속 정보
# ENV 로 만들어두기 [Options]
ENV DB_HOST=mysql-db \
    DB_PORT=3306

# 5. 해당 이미지의 노출 PORT 지정
EXPOSE 8080

# 6. 컨테이너 실행 시 실행 할 명령어
CMD ["java", "-jar", "app.jar"]