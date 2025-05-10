# Use an official Maven image to build the application
FROM maven:3.9.9-eclipse-temurin-17 AS build
COPY pom.xml .
COPY src ./src

# Build the application with Maven
RUN mvn -f pom.xml clean package
FROM eclipse-temurin:17-jre
COPY --from=build /target/csa-final-project-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
