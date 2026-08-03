
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/epic-auth-service-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
