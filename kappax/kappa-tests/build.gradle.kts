import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
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
