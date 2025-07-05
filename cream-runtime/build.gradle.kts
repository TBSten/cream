import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
}

kotlin {
    jvm()
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    linuxX64()

    js(IR) {
        browser()
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlinTest)
            }
        }
    }
}

android {
    namespace = "me.tbsten.cream"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    if (!(gradle.startParameter.taskNames.contains("publishToMavenLocal"))) {
        signAllPublications()
    }

    coordinates(group.toString(), "cream-runtime", version.toString())

    pom {
        name = "cream.kt runtime"
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
