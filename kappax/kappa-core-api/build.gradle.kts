import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.jfrog.artifactory")
    id("dependency-management")
    `maven-publish`
}

group = "com.kaizensundays.fusion.kappa"
version = "0.0.0-SNAPSHOT"

dependencies {
    implementation(libs.kotlinx.serialization)
    implementation(libs.jackson.databind)
    implementation(libs.commons.compress)

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
