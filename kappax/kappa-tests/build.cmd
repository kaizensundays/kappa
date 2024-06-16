
set JAVA_HOME=%JAVA_17_HOME%

gradle clean :kappa-tests:test --tests "com.kaizensundays.fusion.kappa.SomeTest"

@rem gradle clean :kappa-tests:test --tests "com.kaizensundays.fusion.kappa.KappletContainerTest" -Pprofile=tests

