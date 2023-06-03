
rem start "Kappa" 
java -Xmx64m ^
	-Dlogging.config=log4j2.xml ^
	-Dlog4j.shutdownHookEnabled=false ^
        -Dspring.profiles.active=dev ^
        -Dserver.port=7707 ^
	-Djava.net.preferIPv4Stack=true ^
	-jar kappa.jar
