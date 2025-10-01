FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8081

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["sh", "-c", "java -jar app.jar"]