package me.tbsten.cream.test.architecture.buildLogic.buildLogicSettingsScript

import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktsFile

internal fun DeclarationContainerScope.buildLogicSettingsScript() =
    "BuildLogicSettingsScript" {
        summary = "The settings script of the `buildLogic` included build (it shares the root version catalog)"
        documented = false
        example("buildLogic/settings.gradle.kts")
        layout {
            "buildLogic" / "settings.gradle".ktsFile()
        }
    }
