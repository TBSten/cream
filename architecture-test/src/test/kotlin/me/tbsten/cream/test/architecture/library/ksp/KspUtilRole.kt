package me.tbsten.cream.test.architecture.library.ksp

import me.tbsten.cream.test.architecture.KSP_MAIN
import me.tbsten.cream.test.architecture.support.`1 ファイル 300 行以内`
import me.tbsten.cream.test.architecture.support.`KSP の util は KSP と Kotlin だけに依存する`
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

fun DeclarationContainerScope.kspUtilRole() =
    "KspUtil".role {
        title = "KSP の汎用ヘルパ"
        summary = "cream 固有の型に依存しない、KSP API の汎用拡張"
        example("ksp/SealedHierarchy.kt", "sealed 階層をたどる拡張")

        `1 ファイル 300 行以内`()
        `KSP の util は KSP と Kotlin だけに依存する`()

        layout {
            KSP_MAIN / "util" / "ksp" / "*".ktFile()
        }
    }
