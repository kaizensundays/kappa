import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "com.kaizensundays.fusion.kappa"
version = "0.0.0-SNAPSHOT"

dependencies {
    implementation(project(":kappa-core-api"))
    implementation(project(":kappa-core"))

    implementation(libs.kotlinx.serialization)
    implementation(libs.logback.classic)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.commons.compress)

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation(libs.testcontainers)
    testImplementation(libs.fusion.ktor)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

val profile: String? by project
println("profile=$profile")

tasks.withType<Test> {
    useJUnitPlatform() {
        if (profile == "tests") {
            includeTags("container-test")
        } else {
            excludeTags("container-test")
        }
    }
    filter {
        excludeTestsMatching("*RemoteTest")
    }
}
