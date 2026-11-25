package me.tbsten.cream.test.architecture.library.ksp

import me.tbsten.cream.test.architecture.KSP_MAIN
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

fun DeclarationContainerScope.utilRole() =
    "Util".role {
        title = "汎用ヘルパ"
        summary = "cream 固有の型を持たない汎用ヘルパ（stdlib / KSP API の拡張）"
        example("String.kt", "文字列の汎用拡張")
        example("ksp/SealedHierarchy.kt", "KSP API の汎用拡張")

        layout {
            KSP_MAIN / "util" / "**" / "*".ktFile()
        }
    }
