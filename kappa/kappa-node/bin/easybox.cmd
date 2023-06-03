
start "EasyBox" java -Xmx256m ^
	-cp kappa.jar ^
	-Dlog4j.configurationFile=easybox-log4j2.xml ^
        -Dlog4j.debug=false ^
	-Dlog4j.shutdownHookEnabled=false ^
	-Dloader.main=com.kaizensundays.fusion.kappa.EasyBoxMain ^
         org.springframework.boot.loader.PropertiesLauncher
