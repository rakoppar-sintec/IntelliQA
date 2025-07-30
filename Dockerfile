FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/IntelliQA-1.0-SNAPSHOT.jar intelliQA.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "intelliQA.jar"]