package me.tbsten.cream.test.architecture.creamKsp.compositionRoot.symbolProcessorProvider

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.symbolProcessorProvider() =
    "SymbolProcessorProvider" {
        summary = "`CreamSymbolProcessorProvider`: the KSP provider that builds the `ProcessContext`"
        example("CreamSymbolProcessorProvider")
        layout {
            KSP_MAIN_DIR / "CreamSymbolProcessorProvider".ktFile()
        }
    }
