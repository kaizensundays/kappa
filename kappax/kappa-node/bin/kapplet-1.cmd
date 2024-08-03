
set ATOMIX_PROFILE=Consensus
set ATOMIX_BOOTSTRAP=A:localhost:5001;B:localhost:5002;C:localhost:5003
set ATOMIX_NODE_PORT=5001
set ATOMIX_NODE_ID=A

set KAPPLET_SERVER_PORT=7711
set KAPPLET_WEB_PORT=7713

start "Kapplet" %JAVA_17_HOME%/bin/java -Xmx256m ^
	-Dproperties=kapplet.yml ^
	-Dlog4j.configurationFile=file:kapplet-log4j2-1.xml ^
	-Dlog4j2.debug=false ^
	-Dlog4j2.shutdownHookEnabled=false ^
	--add-opens=java.base/sun.nio.ch=ALL-UNNAMED ^
	--add-opens=java.base/java.lang.invoke=ALL-UNNAMED ^
	--add-opens=java.base/java.nio=ALL-UNNAMED ^
	--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED ^
	-jar kappa.jar
