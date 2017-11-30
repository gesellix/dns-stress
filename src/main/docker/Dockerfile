FROM openjdk:8u92-jre-alpine

ENV TZ Europe/Berlin

ENV SLACK_WEBHOOK_URL http://localhost

RUN mkdir /app
WORKDIR /app

ENTRYPOINT ["bin/dns-stress"]
CMD ["1", "google.de"]

COPY ./bin/ /app/bin
COPY ./lib/ /app/lib
