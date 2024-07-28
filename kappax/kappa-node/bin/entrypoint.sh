#!/bin/bash

java -Dproperties=$KAPPLET_PROPERTIES -Dlog4j2.configurationFile=classpath:kapplet-log4j2.xml -Dlog4j.shutdownHookEnabled=false -Dlog4j2.debug=false -jar kappa.jar
