# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS build

# Allow caller to override the app directory in the repo (default: project is in ./)
ARG APP_DIR=.

# Set the working directory
WORKDIR /app

# Copy only the files required to download dependencies first - speeds up cache
COPY ${APP_DIR}/pom.xml ./
# Optionally copy mvnw and .mvn if present (helps with projects that include wrapper)
COPY ${APP_DIR}/mvnw ./mvnw
COPY ${APP_DIR}/.mvn .mvn

# Download dependencies - this layer will be cached unless pom.xml (or .mvn) changes
RUN mvn -B -f pom.xml dependency:go-offline

# Copy the source code
COPY ${APP_DIR}/src ./src

# Build the application
RUN mvn -B -f pom.xml clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre-alpine

# Install dumb-init for proper signal handling and create non-root user in a single layer
RUN apk add --no-cache dumb-init \
    && addgroup -g 1000 spring \
    && adduser -u 1000 -G spring -s /bin/sh -D spring

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership of the application files
RUN chown -R spring:spring /app

# Switch to the non-root user
USER spring:spring

# Expose the port Spring Boot runs on
ENV PORT=8080
EXPOSE ${PORT}

# Use dumb-init to run the application
ENTRYPOINT ["dumb-init", "--"]

# Run the Spring Boot application with Java 21 optimizations
CMD ["java", "-XX:+UseZGC", "-XX:MaxRAMPercentage=75.0", "-Dserver.port=${PORT}", "-jar", "app.jar"]
