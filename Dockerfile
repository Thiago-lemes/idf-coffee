# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-alpine

RUN apk add --no-cache dumb-init

RUN addgroup -g 1000 spring && adduser -u 1000 -G spring -s /bin/sh -D spring

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

RUN chown -R spring:spring /app

USER spring:spring

EXPOSE 8080

ENV JAVA_OPTS="-Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

ENTRYPOINT ["dumb-init", "--"]

CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=8080 -jar app.jar"]

