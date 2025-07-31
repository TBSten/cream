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
        commonTest.dependencies {
            implementation(libs.kotest)
            implementation(libs.kotlinTest)
            implementation(project(":cream-runtime"))
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":cream-ksp"))
    add("kspAndroid", project(":cream-ksp"))
    add("kspJvm", project(":cream-ksp"))
    add("kspJvmTest", project(":cream-ksp"))
}

fun Project.setupKspForMultiplatformWorkaround() {
    kotlin {
        sourceSets.named("commonMain").configure {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
    }
}
setupKspForMultiplatformWorkaround()

ksp {
//    arg("cream.copyFunNamePrefix", "transitionTo")
//    arg("cream.copyFunNamingStrategy", "full-name")
//    arg("cream.escapeDot", "replace-to-underscore")
//    arg("cream.notCopyToObject", "true")
    useKsp2 = false
}
