FROM eclipse-temurin:21-jdk

WORKDIR /library

COPY ./build/libs/library-backend-0.0.1-SNAPSHOT.jar .

CMD ["java", "-jar", "library-backend-0.0.1-SNAPSHOT.jar"]