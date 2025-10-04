pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "cream"
include(":cream-runtime")
include(":cream-ksp")
include(":cream-ksp:shared")
include(":test")
