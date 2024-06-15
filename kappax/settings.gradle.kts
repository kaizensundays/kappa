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

include(":kappa-core")
include(":kappa-core-api")
include(":kappa-tests")
