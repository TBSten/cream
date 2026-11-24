package me.tbsten.cream.test.architecture.kmp.js

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.kotlinJsStoreYarnLockRole() =
    "KotlinJsStoreYarnLock".role {
        documented = false
        title = "Kotlin/JS の yarn.lock"
        summary = "Kotlin/JS ターゲットの npm 依存を固定する yarn.lock（kotlinUpgradeYarnLock で更新）"
        example("kotlin-js-store/yarn.lock", "JS 依存の lock")

        layout {
            "kotlin-js-store" / "yarn.lock".file()
        }
    }
