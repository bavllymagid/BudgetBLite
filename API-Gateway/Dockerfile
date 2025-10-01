FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["sh", "-c", "java -jar app.jar"]