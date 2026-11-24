package me.tbsten.cream.test.architecture.buildSystem.buildLogic

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.buildLogicGroup() =
    "BuildLogic".group {
        documented = false
        summary = "includeBuild している buildLogic（convention plugin）"

        buildLogicSettingsScriptRole()
        buildLogicBuildScriptRole()
        conventionPluginSourceRole()
    }
