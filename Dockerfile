# 使用 Java 8 的基礎映像
FROM maven:3.5-jdk-8-alpine as builder

# Copy local code to the container image.
COPY friends-backend-0.0.1-SNAPSHOT.jar /friends-backend-0.0.1-SNAPSHOT.jar

EXPOSE 8080

# Run the web service on container startup.
ENTRYPOINT ["java","-jar","/friends-backend-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]
