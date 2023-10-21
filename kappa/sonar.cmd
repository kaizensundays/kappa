
set JAVA_HOME=%JAVA_11_HOME%

mvn verify sonar:sonar %SONAR_OPTS% -Dsonar.projectKey=kaizensundays_kappa -Dsonar.branch.name=dev -P sonar
