
start "Kapplet" java -Xmx256m ^
	-Dlogging.configurationFile=classpath:kapplet-log4j2.xml ^
        -Dlog4j2.debug=false ^
	-Dlog4j2.shutdownHookEnabled=false ^
	-jar kappa.jar
