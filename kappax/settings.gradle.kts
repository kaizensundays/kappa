pluginManagement {
    includeBuild("gradle/plugins")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        mavenLocal()
    }
}

rootProject.name = "kappax"

include(":kappa-attic")
include(":kappa-core")
include(":kappa-core-api")
include(":kappa-service")
include(":kappa-tests")
