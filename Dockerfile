# ─────────────────────────────────────────────
# Stage 1: build — компилируем JAR через Gradle
# ─────────────────────────────────────────────
FROM eclipse-temurin:21-jdk AS build

WORKDIR /library

# Копируем сначала только файлы для разрешения зависимостей —
# этот слой кешируется пока build.gradle и gradle/ не изменятся
COPY gradlew gradlew.bat build.gradle ./
COPY gradle/ ./gradle/

# Создаём пустой .env чтобы плагин co.uzzu.dotenv.gradle не падал при сборке
RUN touch .env

# Фикс CRLF на случай Windows-окончаний и скачиваем зависимости
RUN sed -i 's/\r$//' gradlew \
    && sh gradlew dependencies --no-daemon -q 2>/dev/null || true

# Теперь копируем исходники — при изменении кода зависимости не качаются заново
COPY src/ ./src/

RUN sh gradlew bootJar --no-daemon -x test

# ─────────────────────────────────────────────
# Stage 2: runtime — только JRE + JAR
# ─────────────────────────────────────────────
FROM eclipse-temurin:21-jre

WORKDIR /library

COPY --from=build /library/build/libs/*-SNAPSHOT.jar app.jar

# Переменные окружения задаются через docker-compose, здесь только JVM-флаги
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
