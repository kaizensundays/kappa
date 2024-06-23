
allprojects {
    group = "com.kaizensundays.fusion.kappa"
    version = "0.0.0-SNAPSHOT"
}

tasks.named("build") {
    finalizedBy("publishToMavenLocal")
}
