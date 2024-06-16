FROM bellsoft/liberica-openjdk-alpine:11.0.19-7

RUN apk update; \
    apk add vim; \
    apk add bash; \
    apk add htop; \
    apk add unzip;

WORKDIR /opt

ENV M2_HOME="/opt/3.5.4"

COPY maven-3.5.4.zip .
RUN unzip maven-3.5.4.zip
COPY kappa.jar .
COPY kapplet-test-container.yml .
COPY pom.xml .

EXPOSE 7701

ENTRYPOINT ["java", "-Dproperties=kapplet-test-container.yml", "-Dlog4j2.configurationFile=classpath:kapplet-log4j2.xml", "-Dlog4j.shutdownHookEnabled=false", "-Dlog4j2.debug=false", "-jar", "kappa.jar"]

