import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.jfrog.artifactory")
    `maven-publish`
}

group = "com.kaizensundays.fusion.kappa"
version = "0.0.0-SNAPSHOT"

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<Test> {
    useJUnitPlatform()
    filter {
        excludeTestsMatching("*RemoteTest")
    }
}

java {
    withSourcesJar()
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
        create<MavenPublication>("java") {
            from(components["java"])
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
            publications("java")
            setPublishArtifacts(true)
            setPublishPom(true)
        }
    }
}
