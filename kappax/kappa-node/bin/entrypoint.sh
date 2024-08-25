#!/bin/bash

export M2_HOME="/opt/3.5.4"

java -Dproperties=$KAPPLET_PROPERTIES -Dlog4j2.configurationFile=file:kapplet-log4j2.xml -Dlog4j.shutdownHookEnabled=false -Dlog4j2.debug=false -jar kappa.jar
