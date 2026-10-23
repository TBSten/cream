package me.tbsten.cream.test.architecture.creamKsp.compositionRoot.symbolProcessorProvider

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.staysWithinLineBudget
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.symbolProcessorProvider() =
    "SymbolProcessorProvider" {
        summary = "`CreamSymbolProcessorProvider`: KSP provider。環境から `ProcessContext` の材料を取り出す"
        example("CreamSymbolProcessorProvider")
        layout {
            KSP_MAIN_DIR / "CreamSymbolProcessorProvider".ktFile()
        }
        staysWithinLineBudget()
    }
