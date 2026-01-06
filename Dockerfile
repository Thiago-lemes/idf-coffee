FROM maven:3.9-eclipse-temurin-21 AS build

ARG APP_DIR=coffee
WORKDIR /build

COPY ${APP_DIR}/pom.xml ./pom.xml
RUN mvn -B dependency:go-offline

COPY ${APP_DIR}/src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache dumb-init \
    && addgroup -g 1000 spring \
    && adduser -u 1000 -G spring -s /bin/sh -D spring

WORKDIR /app

# 🔴 caminho correto
COPY --from=build /target/*.jar app.jar

RUN chown -R spring:spring /app
USER spring:spring

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["dumb-init", "--"]
CMD ["java", "-Xmx512m", "-jar", "app.jar"]
