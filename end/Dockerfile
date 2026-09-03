# syntax=docker/dockerfile:1
#
# Build context is the repository root, because the Gradle build needs the root
# settings file, the vendored convention plugins in buildSrc/ and the shared
# API spec in api/.
#
#   docker build -f end/Dockerfile -t pokemon-end .

# ---------- build ----------
# Tag matches gradle/wrapper/gradle-wrapper.properties, so the image builds with
# the same Gradle the developers and CI use.
FROM gradle:9.2.0-jdk21 AS build
WORKDIR /workspace

# backend/ and frontend/ are deliberately not copied, :end:bootJar does not need
# them and settings.gradle skips modules that are absent.
COPY settings.gradle build.gradle gradle.properties ./
COPY gradle/ gradle/
COPY buildSrc/ buildSrc/
COPY api/ api/
COPY end/ end/

RUN gradle --no-daemon :end:bootJar \
    && cp end/build/libs/*.jar /workspace/app.jar

# ---------- runtime ----------
FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app

COPY --from=build --chown=spring:spring /workspace/app.jar app.jar

USER spring
EXPOSE 8080

# The domain enum still carries no AWT, but headless keeps any future
# java.awt usage from trying to reach a display that does not exist.
ENV JAVA_TOOL_OPTIONS="-Djava.awt.headless=true"

HEALTHCHECK --interval=10s --timeout=3s --start-period=30s --retries=5 \
    CMD wget -qO- http://localhost:8080/actuator/health/readiness || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
