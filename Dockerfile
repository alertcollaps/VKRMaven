# syntax=docker/dockerfile:1

FROM openjdk:16-alpine3.13

FROM maven

WORKDIR /app

COPY pom.xml ./

COPY src ./src

RUN mvn package

ENV PORT 5000

EXPOSE $PORT

CMD ["java", "-Dserver.port=${PORT}", "-cp", "target/classes:target/dependency/*", "Server.ServerLoader"]

