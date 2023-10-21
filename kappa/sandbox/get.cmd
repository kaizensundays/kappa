

@REM mvn dependency:get -DgroupId=com.kaizensundays.particles -DartifactId=fusion-mu -Dversion=0.0.0-SNAPSHOT -Dpackaging=jar -Dtransitive=false


@REM mvn kappa:get-artifact -DgroupId=com.kaizensundays.particles -DartifactId=fusion-mu -Dversion=0.0.0-SNAPSHOT -Dpackaging=jar -Dtransitive=false


mvn kappa:get-artifact -Dartifact=com.kaizensundays.particles:fusion-mu:0.0.0-SNAPSHOT:jar -Dtransitive=false
