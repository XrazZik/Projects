FROM ubuntu:latest
LABEL authors="Jackl"

FROM openjdk:17-jdk-slim as build
WORKDIR /app
COPY . /app
RUN ./mvnw clean package -DskipTests
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/task_service-0.0.1-SNAPSHOT.jar /app/task_service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/task_service.jar"]