FROM bellsoft/liberica-openjdk-alpine:11.0.19-7

RUN apk update; \
    apk add vim; \
    apk add bash; \
    apk add htop; \
    apk add unzip;


WORKDIR /opt

COPY .bashrc /root/.bashrc

COPY maven-3.5.4.zip .
RUN unzip maven-3.5.4.zip
COPY kapplet-test-container.yml .
COPY kapplet-0.yml .
COPY kapplet-log4j2.xml .
COPY entrypoint.sh .
COPY pom.xml .
COPY kappa.jar .

EXPOSE 7701
EXPOSE 7703

ENTRYPOINT ["./entrypoint.sh"]

