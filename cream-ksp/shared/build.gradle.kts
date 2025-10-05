import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm()
    js(IR) {
        browser()
        nodejs()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }

    compilerOptions.optIn.addAll(
        "me.tbsten.cream.InternalCreamApi",
    )

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":cream-runtime"))
                implementation(libs.kotlinxSerializationCore)
            }
        }
        val commonTest by getting {
            dependencies {
            }
        }
    }
}

mavenPublishing {
    publishToMavenCentral()

    if (!(gradle.startParameter.taskNames.contains("publishToMavenLocal"))) {
        signAllPublications()
    }

    coordinates(group.toString(), "cream-ksp-shared", version.toString())

    pom {
        name = "cream.kt ksp plugin shared logic"
        description = "cream.kt is a KSP Plugin that makes it easy to copy across classes."
        inceptionYear = "2025"
        url = "https://github.com/TBSten/cream/"
        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id = "TBSten"
                name = "TBSten"
                url = "https://github.com/TBSten/"
            }
        }
        scm {
            url.set("https://github.com/TBSten/cream/")
            connection.set("scm:git:git://github.com/TBSten/cream/.git")
            developerConnection.set("scm:git:git://github.com/TBSten/cream/.git")
        }
    }
}
