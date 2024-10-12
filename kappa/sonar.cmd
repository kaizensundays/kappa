
set JAVA_HOME="%JAVA_17_HOME%"

mvn verify sonar:sonar %SONAR_OPTS% -Dsonar.projectKey=kaizensundays_kappa -Dsonar.branch.name=dev -P sonar
