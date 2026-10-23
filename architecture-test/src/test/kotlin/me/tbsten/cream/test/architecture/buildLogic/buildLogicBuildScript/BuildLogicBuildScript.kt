package me.tbsten.cream.test.architecture.buildLogic.buildLogicBuildScript

import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktsFile

internal fun DeclarationContainerScope.buildLogicBuildScript() =
    "BuildLogicBuildScript" {
        summary = "The build script of a project inside the `buildLogic` included build"
        documented = false
        example("buildLogic/convention/build.gradle.kts")
        layout {
            "buildLogic" / "*" / "build.gradle".ktsFile()
        }
    }
