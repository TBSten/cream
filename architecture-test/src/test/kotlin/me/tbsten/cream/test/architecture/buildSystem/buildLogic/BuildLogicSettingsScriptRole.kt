package me.tbsten.cream.test.architecture.buildSystem.buildLogic

import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktsFile

fun DeclarationContainerScope.buildLogicSettingsScriptRole() =
    "BuildLogicSettingsScript".role {
        documented = false
        title = "buildLogic の settings"
        summary = "includeBuild される buildLogic ビルド自身の settings スクリプト"
        example("buildLogic/settings.gradle.kts", "buildLogic の settings")

        layout {
            "buildLogic" / "settings.gradle".ktsFile()
        }
    }
