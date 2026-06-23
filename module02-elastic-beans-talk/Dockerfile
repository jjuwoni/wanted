# 1. 실행용 베이스 이미지 선택
# JAR 빌드는 호스트의 Gradle에서 수행하므로 컨테이너에는 JRE만 있으면 된다.
FROM eclipse-temurin:17-jre-alpine

# 2. 작업 디렉토리 설정
# 컨테이너 내부에서 모든 작업이 수행될 기본 디렉토리
# /app 디렉토리가 없으면 자동으로 생성된다
WORKDIR /app

# 3. JAR 파일 복사
# build/libs/*.jar - Gradle 빌드 결과물 (와일드카드로 버전 무관하게 복사)
# app.jar - 컨테이너 내부에서 사용할 파일명 (단순화)
COPY build/libs/*.jar app.jar

# 4. 컨테이너 시작 시 실행할 명령어
# java -jar app.jar - Spring Boot 애플리케이션 실행
# CMD는 컨테이너 실행 시 기본 명령어 (docker run에서 오버라이드 가능)
CMD ["java", "-jar", "app.jar"]