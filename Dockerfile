# Build stage
FROM gradle:8.12.0-jdk21 AS builder
WORKDIR /app

# Copy dependency definitions first 
COPY build.gradle settings.gradle ./       

# Download dependencies only (this layer can be cached)
RUN gradle dependencies

# Copy source code
COPY src ./src

# Build the application
RUN gradle build -x test

# Runtime stage
FROM openjdk:21-slim
WORKDIR /app

# Add non-root user
RUN addgroup --system javauser && adduser --system --group javauser

# Set ownership and switch to non-root user
COPY --from=builder --chown=javauser:javauser /app/build/libs/club-board_server-0.0.1-SNAPSHOT.jar app.jar

# 🛠️ root 권한으로 전환
USER root

# wget 설치 (Debian 기반)
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

# 🛠️ 다시 비-루트 사용자로 전환
USER javauser

# Configure JVM options
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Health check (wget 사용)
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --spider -q http://localhost:80/actuator/health || exit 1

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
