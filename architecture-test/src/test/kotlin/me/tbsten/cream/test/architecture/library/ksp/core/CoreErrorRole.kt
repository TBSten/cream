package me.tbsten.cream.test.architecture.library.ksp.core

import me.tbsten.cream.test.architecture.KSP_MAIN
import me.tbsten.cream.test.architecture.support.`1 ファイル 300 行以内`
import me.tbsten.cream.test.architecture.support.`core 層は feature と composition root に依存しない`
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

fun DeclarationContainerScope.coreErrorRole() =
    "CoreError".role {
        title = "例外"
        summary = "cream が投げる CreamException の階層（util 以外に依存しない葉）"
        example("CreamException.kt", "InvalidCreamUsageException などの基底")

        `1 ファイル 300 行以内`()
        `core 層は feature と composition root に依存しない`()

        layout {
            KSP_MAIN / "core" / "error" / "*Exception".ktFile()
        }
    }
