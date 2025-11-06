FROM openjdk:21-jre-slim

WORKDIR /library

COPY build/libs/my-application.jar .

CMD ["java", "-jar", "my-application.jar"]