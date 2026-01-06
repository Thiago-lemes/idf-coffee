# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# Copy Maven files first for better layer caching
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw.cmd ./
# Download dependencies
RUN mvn dependency:go-offline -B -q 2>/dev/null || true
# Copy source code
COPY src ./src
# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests -q
# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre-alpine
# Install dumb-init for proper signal handling and curl for healthcheck
RUN apk add --no-cache dumb-init curl
# Create non-root user for security
RUN addgroup -g 1000 spring && adduser -u 1000 -G spring -s /bin/sh -D spring
WORKDIR /app
# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar
RUN chown -R spring:spring /app
# Switch to non-root user
USER spring:spring
# Health check for Fly.io and Docker
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1
EXPOSE 8080
# JVM optimization for containerized environments
ENV JAVA_OPTS="-Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:G1NewCollectionHeuristicPercent=25 -XX:InitiatingHeapOccupancyPercent=35 -Djava.awt.headless=true -Dfile.encoding=UTF-8"
# Use dumb-init to handle signals properly (graceful shutdown)
ENTRYPOINT ["dumb-init", "--"]
# Run the Spring Boot application
CMD ["sh", "-c", "java ${JAVA_OPTS} -Dserver.port=8080 -jar app.jar"]
