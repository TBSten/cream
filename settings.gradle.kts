pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// Auto-provision the JDK 17 toolchain (`jvmToolchain(17)`) for contributors who do not have a
// matching JDK installed locally, so the toolchain spec never fails with "No matching toolchains".
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "cream"
includeBuild("./buildLogic")
include(":cream-runtime")
include(":cream-ksp")
include(":cream-ksp:shared")
include(":optionBuilder:sharedUI")
include(":optionBuilder:desktopApp")
include(":optionBuilder:webApp")
include(":test")
