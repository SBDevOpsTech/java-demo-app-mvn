# Stage 1: Build the application using Maven
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .

RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Run the application using a minimal OpenJDK image
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the application's port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
