start "Kapplet" %JAVA_17_HOME%/bin/java -Xmx256m ^
	-Dproperties=kapplet.yml ^
	-Dlog4j.configurationFile=classpath:kapplet-log4j2.xml ^
	-Dlog4j2.debug=false ^
	-Dlog4j2.shutdownHookEnabled=false ^
	--add-opens=java.base/sun.nio.ch=ALL-UNNAMED ^
	--add-opens=java.base/java.lang.invoke=ALL-UNNAMED ^
	--add-opens=java.base/java.nio=ALL-UNNAMED ^
	--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED ^
	-jar kappa.jar
