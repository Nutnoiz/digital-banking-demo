FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/digital-banking-api-0.0.1-SNAPSHOT.jar app.jar

RUN mkdir -p /app/data

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]