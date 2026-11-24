package me.tbsten.cream.test.architecture.library.ksp

import me.tbsten.cream.test.architecture.KSP_MAIN
import me.tbsten.cream.test.architecture.support.`1 ファイル 300 行以内`
import me.tbsten.cream.test.architecture.support.`util は Kotlin だけに依存する`
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

fun DeclarationContainerScope.utilRole() =
    "Util".role {
        title = "汎用ヘルパ"
        summary = "cream 固有の型にも KSP にも依存しない、Kotlin だけの汎用ヘルパ"
        example("String.kt", "文字列の汎用拡張")

        `1 ファイル 300 行以内`()
        `util は Kotlin だけに依存する`()

        layout {
            KSP_MAIN / "util" / "*".ktFile()
        }
    }
