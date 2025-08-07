@file:OptIn(KspExperimental::class)

import com.google.devtools.ksp.KspExperimental
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.ksp)
}

android {
    namespace = "me.tbsten.cream.test"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
}

kotlin {
    iosSimulatorArm64()
    jvm()
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":cream-runtime"))
        }
        commonTest.dependencies {
            implementation(libs.kotest)
            implementation(libs.kotlinTest)
        }
    }
}

dependencies {
    listOf(
        "kspCommonMainMetadata",
        "kspJvm",
        "kspJvmTest",
    ).forEach { it(project(":cream-ksp")) }
}

//ksp {
//    arg("cream.copyFunNamePrefix", "transitionTo")
//    arg("cream.copyFunNamingStrategy", "full-name")
//    arg("cream.escapeDot", "replace-to-underscore")
//    arg("cream.notCopyToObject", "true")
//}

fun Project.setupKspForMultiplatformWorkaround() {
    kotlin.sourceSets.commonMain {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }

    tasks.configureEach {
        if(name.startsWith("ksp") && name != "kspCommonMainKotlinMetadata") {
            dependsOn(tasks.named("kspCommonMainKotlinMetadata"))
            enabled = false
        }
    }
}
setupKspForMultiplatformWorkaround()
