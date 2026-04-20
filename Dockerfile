FROM eclipse-temurin:21-jdk AS build
WORKDIR /library
COPY . .
RUN sed -i 's/\r$//' gradlew && sh gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre
WORKDIR /library
COPY --from=build /library/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]