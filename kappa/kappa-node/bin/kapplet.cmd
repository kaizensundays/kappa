
start "Kapplet" %JAVA_11_HOME%/bin/java -Xmx256m ^
	-Dlog4j2.configurationFile=classpath:kapplet-log4j2.xml ^
        -Dlog4j2.debug=false ^
	-Dlog4j2.shutdownHookEnabled=false ^
	-jar kappa.jar
