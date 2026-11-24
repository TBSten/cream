package me.tbsten.cream.test.architecture.kmp.js

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.jsGroup() =
    "Js".group {
        documented = false
        summary = "Kotlin/JS・Wasm ターゲットのために Kotlin Gradle Plugin が書き出すファイル（kotlin-js-store/）"

        kotlinJsStoreYarnLockRole()
        kotlinWasmStoreYarnLockRole()
    }
