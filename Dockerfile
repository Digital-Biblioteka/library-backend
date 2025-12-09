FROM eclipse-temurin:21-jdk

WORKDIR /library

COPY ./build/libs/library-backend-0.0.1-SNAPSHOT.jar .

ENTRYPOINT ["java", "-jar", "library-backend-0.0.1-SNAPSHOT.jar"]