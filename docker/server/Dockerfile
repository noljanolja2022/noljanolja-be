FROM eclipse-temurin:17-jre-alpine

ARG SERVICE

WORKDIR /app

EXPOSE 10080

ADD "./$SERVICE/build/libs/$SERVICE.jar" "./server.jar"

CMD ["java", "-jar", "server.jar"]
