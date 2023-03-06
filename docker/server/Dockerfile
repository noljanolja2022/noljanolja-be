FROM openjdk:11-jre-slim

ARG SERVICE

WORKDIR /app

EXPOSE 10080

ADD "./$SERVICE/build/libs/$SERVICE.jar" "./server.jar"

CMD ["java", "-jar", "server.jar"]
