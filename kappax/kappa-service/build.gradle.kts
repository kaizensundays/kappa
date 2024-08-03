
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
    api(project(":kappa-core-api"))
    api(project(":kappa-core"))

    implementation(libs.fusion.ktor)

    implementation(libs.kotlinx.serialization)

    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.server.cio.jvm)
    implementation(libs.ktor.server.cors.jvm)
    implementation(libs.ktor.server.status.pages.jvm)
    implementation(libs.ktor.server.content.negotiation.jvm)
    implementation(libs.ktor.serialization.kotlinx.json.jvm)

    api(libs.spring.context.support)

    implementation(libs.javax.annotation)

    implementation(libs.jackson.databind)
    implementation(libs.jackson.dataformat.yaml)
    implementation(libs.jackson.module.kotlin)

    api(libs.jcache)

    implementation(libs.nuprocess)
    implementation(libs.jna)
    implementation(libs.winp)

    implementation(libs.commons.compress)

    implementation(libs.maven.invoker)

    implementation(libs.ktor.server.webjars)
    implementation("io.ktor:ktor-server-html-builder:2.2.4")
    //implementation("org.jetbrains.kotlinx:kotlinx-html:0.11.0")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))

    testImplementation(libs.spring.test)
    testImplementation(libs.archunit) {
        exclude("org.slf4j")
    }
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
