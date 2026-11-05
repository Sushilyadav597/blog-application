# Multi-stage Docker build for Spring Boot 3 & Java 21 on Render / Cloud
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy Maven wrapper and project definition
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Copy source code and package the jar (skipping tests for faster cloud build)
COPY src/ src/
RUN ./mvnw clean package -DskipTests

# Production runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/blog-application-api-1.0.0.jar app.jar

# Expose port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
