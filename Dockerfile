# syntax=docker/dockerfile:1

FROM openjdk:16-alpine3.13


WORKDIR /app

COPY pom.xml ./

COPY src ./src

ENV PORT 5000

EXPOSE $PORT

CMD ["java", "-Dserver.port=${PORT}", "-jar", "target/VKRMaven-1.0-SNAPSHOT.jar"]

