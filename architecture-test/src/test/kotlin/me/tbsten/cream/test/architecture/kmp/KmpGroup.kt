package me.tbsten.cream.test.architecture.kmp

import me.tbsten.cream.test.architecture.kmp.js.jsGroup
import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.kmpGroup() =
    "Kmp".group {
        documented = false
        summary = "Kotlin Multiplatform の仕組みが書き出すファイル"

        jsGroup()
    }
