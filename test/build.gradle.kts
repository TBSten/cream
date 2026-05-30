@file:OptIn(KspExperimental::class)

import com.google.devtools.ksp.KspExperimental
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.ksp)
    // kotest must be applied after ksp: its multiplatform framework wiring is KSP-based.
    alias(libs.plugins.kotest)
    id("buildLogic.lint")
}

android {
    namespace = "me.tbsten.cream.test"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
    }
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
            implementation(libs.kotestFrameworkEngine)
        }
        jvmTest.dependencies {
            implementation(libs.kotestRunnerJunit5)
        }
        // Android unit tests run on the JVM via the JUnit Platform too, but androidUnitTest does not
        // inherit jvmTest, so it needs the kotest JUnit5 runner independently to discover FunSpec.
        androidUnitTest.dependencies {
            implementation(libs.kotestRunnerJunit5)
        }
    }
}

// kotest runs on the JUnit Platform for the JVM and Android unit-test tasks.
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    // cream only processes commonMain annotations (collapsed into kspCommonMainKotlinMetadata by the
    // workaround below). It is deliberately NOT wired into any *Test KSP configuration so that the
    // kotest KSP processor owns the test source sets without a second processor running alongside it.
    listOf(
        "kspCommonMainMetadata",
        "kspJvm",
    ).forEach { it(project(":cream-ksp")) }
}

// ksp {
//    arg("cream.copyFunNamePrefix", "transitionTo")
//    arg("cream.copyFunNamingStrategy", "full-name")
//    arg("cream.escapeDot", "replace-to-underscore")
//    arg("cream.notCopyToObject", "true")
// }

fun Project.setupKspForMultiplatformWorkaround() {
    kotlin.sourceSets.commonMain {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }

    tasks.configureEach {
        if (name.startsWith("ksp") && name != "kspCommonMainKotlinMetadata") {
            dependsOn(tasks.named("kspCommonMainKotlinMetadata"))
            // Disable only cream's redundant per-platform *main* generation; keep the *Test KSP tasks
            // alive so the kotest framework can generate its per-target spec launchers (required for
            // FunSpec to start on native/Android, and harmless on JVM).
            if (!name.contains("Test")) {
                enabled = false
            }
        }
    }
}
setupKspForMultiplatformWorkaround()

fun ktlintWithKspWorkaround() {
    tasks.named("runKtlintFormatOverCommonMainSourceSet") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
    tasks.named("runKtlintCheckOverCommonMainSourceSet") {
        dependsOn("kspCommonMainKotlinMetadata")
    }

    ktlint {
        filter {
            exclude("**/build/generated/**")
        }
    }
}
ktlintWithKspWorkaround()
