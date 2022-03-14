# syntax=docker/dockerfile:1

FROM openjdk:16-alpine3.13


WORKDIR /app

COPY target/VKRMaven-1.0-SNAPSHOT.jar ./vkrmaven.jar

ENV PORT 5000

EXPOSE $PORT

CMD ["java", "-Dserver.port=${PORT}", "-jar", "./vkrmaven.jar"]

