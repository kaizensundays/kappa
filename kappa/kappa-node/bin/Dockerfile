FROM bellsoft/liberica-openjdk-alpine:11.0.19-7

RUN apk update; \
    apk add vim; \
    apk add bash; \
    apk add htop;

COPY kappa.jar /opt/
COPY application.properties /opt/

WORKDIR /opt

EXPOSE 7701

ENTRYPOINT ["java", "-Dlog4j2.configurationFile=classpath:kapplet-log4j2.xml", "-Dlog4j.shutdownHookEnabled=false", "-Dlog4j2.debug=false", "-jar", "kappa.jar"]

