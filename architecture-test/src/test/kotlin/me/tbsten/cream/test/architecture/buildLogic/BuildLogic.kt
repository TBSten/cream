package me.tbsten.cream.test.architecture.buildLogic

import me.tbsten.cream.test.architecture.buildLogic.buildLogicBuildScript.buildLogicBuildScript
import me.tbsten.cream.test.architecture.buildLogic.buildLogicSettingsScript.buildLogicSettingsScript
import me.tbsten.cream.test.architecture.buildLogic.conventionDsl.conventionDsl
import me.tbsten.cream.test.architecture.buildLogic.conventionPlugin.conventionPlugin
import me.tbsten.cream.test.architecture.buildLogic.kotlinJsYarnLock.kotlinJsYarnLock
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.buildLogic() =
    "buildLogic".group {
        title = "Build logic"
        summary = "The `buildLogic` included build, and the build-tool files `gradle()` does not cover"

        buildLogicSettingsScript()
        buildLogicBuildScript()
        conventionPlugin()
        conventionDsl()
        kotlinJsYarnLock()
    }
