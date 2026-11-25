@file:OptIn(ExperimentalKatachiApi::class)

package me.tbsten.cream.test.architecture.buildSystem

import me.tbsten.cream.test.architecture.buildSystem.buildLogic.buildLogicGroup
import me.tbsten.katachi.ExperimentalKatachiApi
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.gradle.gradle

fun DeclarationContainerScope.buildSystemGroup() =
    "BuildSystem".group {
        documented = false
        summary = "ビルド（Gradle の標準ファイルと、includeBuild している buildLogic）"

        gradle()
        buildLogicGroup()
    }
