
plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.springframework.boot")
    id("com.jfrog.artifactory")
    id("dependency-management")
    `maven-publish`
}

group = "com.kaizensundays.fusion.kappa"
version = "0.0.0-SNAPSHOT"

dependencies {
    implementation(project(":kappa-service")) {
        exclude("ch.qos.logback")
    }

    implementation(libs.atomix)

    implementation(libs.webjars.htmx)

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    withSourcesJar()
}

tasks.withType<Test> {
    useJUnitPlatform()
    filter {
        excludeTestsMatching("*RemoteTest")
    }
}

tasks.bootJar {
    archiveFileName.set("kappa.jar")
    destinationDirectory.set(file("bin"))
}

tasks.publish {
    dependsOn("assemble")
}

publishing {
    repositories {
        mavenLocal()
    }
}

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("bootJava") {
            from(components["java"])
            artifact(tasks.bootJar) {
                artifactId = "kappa-node"
            }
        }
    }
}

artifactory {
    setContextUrl(project.properties["artifactory.url"] as String)
    publish {
        repository {
            setRepoKey("libs-snapshot-local")
            setUsername(project.properties["artifactory.username"] as String)
            setPassword(project.properties["artifactory.password"] as String)
            setMavenCompatible(true)
        }
        defaults {
            publications("bootJava")
            setPublishArtifacts(true)
            setPublishPom(true)
        }
    }
}
