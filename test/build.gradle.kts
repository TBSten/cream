@file:OptIn(KspExperimental::class)

import com.google.devtools.ksp.KspExperimental
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.ksp)
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
            enabled = false
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

fun Project.setupGeneratedSourceVerify() {
    tasks.register("dumpGeneratedSource") {
        group = "cream"
        description = "Dump KSP generated source code to __generated__ packages for visibility"

        dependsOn("kspCommonMainKotlinMetadata")

        val generatedSourceDir = layout.buildDirectory.dir("generated/ksp/metadata/commonMain/kotlin/me/tbsten/cream/test")
        val targetSourceDir = layout.projectDirectory.dir("src/commonMain/kotlin/me/tbsten/cream/test")

        doLast {
            val generatedDir = generatedSourceDir.get().asFile
            val srcDir = targetSourceDir.asFile

            if (!generatedDir.exists()) {
                logger.warn("Generated source directory does not exist: ${generatedDir.absolutePath}")
                return@doLast
            }

            // Get all subdirectories in the generated source directory
            generatedDir.listFiles()?.filter { it.isDirectory }?.forEach { generatedSubDir ->
                val subDirName = generatedSubDir.name
                val targetDir = srcDir.resolve("$subDirName/__generated__")

                // Clean and create target directory
                if (targetDir.exists()) {
                    targetDir.deleteRecursively()
                }
                targetDir.mkdirs()

                // Copy all generated files with .kt.generated extension to avoid IDE errors
                generatedSubDir.listFiles()?.filter { it.isFile }?.forEach { generatedFile ->
                    val targetFileName =
                        if (generatedFile.name.endsWith(".kt")) {
                            generatedFile.name + ".generated"
                        } else {
                            generatedFile.name
                        }
                    val targetFile = targetDir.resolve(targetFileName)
                    generatedFile.copyTo(targetFile, overwrite = true)
                    logger.info("Copied: ${generatedFile.name} -> ${targetFile.relativeTo(srcDir)}")
                }

                // Generate README.md (English)
                val readmeFile = targetDir.resolve("README.md")
                readmeFile.writeText(
                    """
                    # Generated Source Code

                    This directory contains KSP-generated source code that has been automatically copied by the `dumpGeneratedSource` Gradle task.

                    **⚠️ DO NOT EDIT FILES IN THIS DIRECTORY MANUALLY**

                    These files are automatically generated and copied from:
                    - Source: `test/build/generated/ksp/metadata/commonMain/kotlin/me/tbsten/cream/test/$subDirName/`
                    - Target: `test/src/commonMain/kotlin/me/tbsten/cream/test/$subDirName/__generated__/`

                    ## Purpose

                    This directory exists to make KSP-generated code visible in the source tree, helping developers understand what code is being automatically generated during the build process.

                    ## File Extension

                    All Kotlin files (`.kt`) are copied with the `.kt.generated` extension to prevent IDE errors and compilation conflicts with the actual KSP-generated code in `build/generated/ksp/`.

                    ## Updating Generated Code

                    To update the files in this directory:
                    1. Run: `./gradlew :test:dumpGeneratedSource`
                    2. The task will regenerate all files from the latest KSP output

                    ## Generated by

                    Gradle task: `dumpGeneratedSource`

                    ---

                    [日本語版はこちら / Japanese version](README.ja.md)
                    """.trimIndent(),
                )

                // Generate README.ja.md (Japanese)
                val readmeJaFile = targetDir.resolve("README.ja.md")
                readmeJaFile.writeText(
                    """
                    # 自動生成されたソースコード

                    このディレクトリには、`dumpGeneratedSource` Gradle タスクによって自動的にコピーされた KSP 生成のソースコードが含まれています。

                    **⚠️ このディレクトリ内のファイルを手動で編集しないでください**

                    これらのファイルは自動生成され、以下の場所からコピーされています：
                    - コピー元: `test/build/generated/ksp/metadata/commonMain/kotlin/me/tbsten/cream/test/$subDirName/`
                    - コピー先: `test/src/commonMain/kotlin/me/tbsten/cream/test/$subDirName/__generated__/`

                    ## 目的

                    このディレクトリは、ビルドプロセス中に自動生成されるコードをソースツリー内で可視化し、開発者が生成されるコードを理解しやすくするために存在します。

                    ## ファイル拡張子について

                    全ての Kotlin ファイル（`.kt`）は、IDE のエラー表示やコンパイル時の衝突を防ぐために `.kt.generated` 拡張子でコピーされています。実際の KSP 生成コードは `build/generated/ksp/` に存在します。

                    ## 生成コードの更新方法

                    このディレクトリ内のファイルを更新するには：
                    1. 次のコマンドを実行: `./gradlew :test:dumpGeneratedSource`
                    2. タスクが最新の KSP 出力から全てのファイルを再生成します

                    ## 生成元

                    Gradle タスク: `dumpGeneratedSource`

                    ---

                    [English version](README.md)
                    """.trimIndent(),
                )

                logger.lifecycle("Dumped generated sources for '$subDirName' to: ${targetDir.relativeTo(srcDir)}")
            }

            logger.lifecycle("✓ dumpGeneratedSource completed successfully")
        }
    }
}
setupGeneratedSourceVerify()
