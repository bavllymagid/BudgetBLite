FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]