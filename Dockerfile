# syntax=docker/dockerfile:1

FROM openjdk:16-alpine3.13

FROM maven

WORKDIR /app

COPY pom.xml ./

COPY src ./src

RUN mvn package


EXPOSE 8888

CMD ["ls"]
CMD ["java", "-cp", "target/classes:target/dependency/*", "Server.ServerLoader"]

RUN: docker build --tag java-server .