package me.tbsten.cream.test.architecture.kmp.js

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.kotlinWasmStoreYarnLockRole() =
    "KotlinWasmStoreYarnLock".role {
        documented = false
        title = "Kotlin/Wasm の yarn.lock"
        summary = "Kotlin/Wasm ターゲットの npm 依存を固定する yarn.lock（kotlinWasmUpgradeYarnLock で更新。まだコミットされていない）"
        example("kotlin-js-store/wasm/yarn.lock", "Wasm 依存の lock")

        layout {
            "kotlin-js-store" / "wasm" / "yarn.lock".file().optional()
        }
    }
