pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// Mirror the root build: auto-provision the JDK 17 toolchain for this included build too, so
// `jvmToolchain(17)` in convention/build.gradle.kts resolves even without a matching local JDK.
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "cream-buildLogic"

include("convention")
