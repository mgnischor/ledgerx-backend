# syntax=docker/dockerfile:1

FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

COPY gradlew ./
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
RUN chmod +x gradlew && ./gradlew --no-daemon help

COPY src src
RUN ./gradlew --no-daemon clean bootJar -x test

FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

RUN groupadd --system ledgerx && useradd --system --gid ledgerx ledgerx

COPY --from=build /workspace/build/libs/*.jar app.jar

USER ledgerx
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
