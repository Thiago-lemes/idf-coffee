# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy the pom.xml and download dependencies first to leverage Docker cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-alpine

# Install dumb-init for proper signal handling
RUN apk add --no-cache dumb-init

# Create a non-root user to run the application
RUN addgroup -g 1000 spring && adduser -u 1000 -G spring -s /bin/sh -D spring

WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership of the application files
RUN chown -R spring:spring /app

# Switch to the non-root user
USER spring:spring

# Expose the port Spring Boot runs on
EXPOSE 8080

# Set Java options for better performance
ENV JAVA_OPTS="-Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Use dumb-init to run the application
ENTRYPOINT ["dumb-init", "--"]

# Run the Spring Boot application
CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=8080 -jar app.jar"]
