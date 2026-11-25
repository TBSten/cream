package me.tbsten.cream.test.architecture.buildSystem.buildLogic

import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktsFile

fun DeclarationContainerScope.buildLogicBuildScriptRole() =
    "BuildLogicBuildScript".role {
        documented = false
        title = "convention のビルドスクリプト"
        summary = "convention plugin モジュール自身のビルドスクリプト"
        example("buildLogic/convention/build.gradle.kts", "convention のビルド")

        layout {
            "buildLogic" / "convention" / "build.gradle".ktsFile()
        }
    }
