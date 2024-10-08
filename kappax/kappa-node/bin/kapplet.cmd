
#
# Single node DataGrid
#

set ATOMIX_PROFILE=DataGrid
set ATOMIX_BOOTSTRAP=DG:localhost:5501
set ATOMIX_NODE_PORT=5501
set ATOMIX_NODE_ID=DG

set KAPPLET_SERVER_PORT=7701
set KAPPLET_WEB_PORT=7703


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
