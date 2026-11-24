package me.tbsten.cream.test.architecture.library.ksp.core

import me.tbsten.cream.test.architecture.KSP_MAIN
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

fun DeclarationContainerScope.coreErrorRole() =
    "CoreError".role {
        title = "例外"
        summary = "cream が投げる CreamException の階層（util 以外に依存しない葉）"
        example("CreamException.kt", "InvalidCreamUsageException などの基底")

        layout {
            KSP_MAIN / "core" / "error" / "*Exception".ktFile()
        }
    }
