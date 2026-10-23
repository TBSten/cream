package me.tbsten.cream.test.architecture.buildLogic.kotlinJsYarnLock

import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.kotlinJsYarnLock() =
    "KotlinJsYarnLock" {
        summary = "The yarn lock the Kotlin Gradle plugin keeps for Kotlin/JS (updated by `kotlinUpgradeYarnLock`)"
        documented = false
        example("kotlin-js-store/yarn.lock")
        layout {
            "kotlin-js-store" / "yarn.lock".file()
        }
    }
