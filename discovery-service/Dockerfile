FROM openjdk:17-oracle
# 포트 설정 (내부 포트)
EXPOSE 8761

# JAR 파일 인자 설정
ARG JAR_FILE=build/libs/*.jar

# JAR 파일을 이미지 내로 복사
COPY ${JAR_FILE} app.jar

# 실행 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]
