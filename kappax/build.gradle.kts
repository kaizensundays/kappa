plugins {
    kotlin("jvm") version KotlinVersion apply false
    kotlin("plugin.serialization") version KotlinVersion apply false
    id("org.springframework.boot") version "2.7.18" apply false
    id("com.jfrog.artifactory") version JFrogArtifactoryPluginVersion apply false
}
