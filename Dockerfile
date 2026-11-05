# ==============================================================================
# Multi-Stage Dockerfile for Spring Boot 3 Blog Application API
# Optimized for minimal image size, security, and Java 21 LTS runtime
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build the application using Maven and Eclipse Temurin JDK 21
# ------------------------------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Cache Maven dependencies layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build executable jar
COPY src ./src
RUN mvn clean package -DskipTests

# ------------------------------------------------------------------------------
# Stage 2: Lightweight JRE 21 Runtime Environment
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Run as non-privileged system user for container security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser:appgroup

# Copy the built jar from builder stage
COPY --from=builder /app/target/blog-application-api-1.0.0.jar app.jar

# Expose Spring Boot default port
EXPOSE 8080

# Configure container memory limits and execution
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]

