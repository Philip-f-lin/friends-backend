# 使用 Java 8 的基礎映像
FROM openjdk:8-jdk-alpine

# 添加應用 jar 文件
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# 啟動 Spring Boot 應用
ENTRYPOINT ["java", "-jar", "/app.jar"]

# 開放應用執行端口
EXPOSE 8080
