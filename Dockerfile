# syntax=docker/dockerfile:1.7

FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /workspace

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew

COPY src src

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew clean bootJar --no-daemon


FROM eclipse-temurin:17-jre-jammy

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system --gid 1001 spring \
    && useradd --system --uid 1001 --gid spring spring

WORKDIR /app

COPY --from=builder \
    --chown=spring:spring \
    /workspace/build/libs/E-Commerce-0.0.1-SNAPSHOT.jar \
    /app/app.jar

USER spring:spring

EXPOSE 8080

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
    CMD curl --fail --silent http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
