FROM openjdk:8u92-jre-alpine

ENV TZ Europe/Berlin

ENV SERVER_PORT 8080
EXPOSE $SERVER_PORT

RUN mkdir /app
WORKDIR /app

ENTRYPOINT ["java", "-Xms64m", "-Xmx64m", "-server", "-jar", "app.jar"]
CMD ["1", "google.de"]

COPY app.jar /app/
RUN ls
