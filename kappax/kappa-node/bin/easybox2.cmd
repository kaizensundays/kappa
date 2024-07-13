
rem start "EasyBox"
 java -Xmx256m ^
	-cp ".kappa/lib/*" ^
	-Dlog4j.configurationFile=easybox-log4j2.xml ^
        -Dlog4j.debug=false ^
	-Dlog4j.shutdownHookEnabled=false ^
        com.kaizensundays.fusion.kappa.EasyBoxMain
