package me.tbsten.cream.test.architecture.creamKsp.compositionRoot.symbolProcessor

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.symbolProcessor() =
    "SymbolProcessor" {
        summary = "`CreamSymbolProcessor`: parses the options and dispatches every feature's `processXxx()`"
        example("CreamSymbolProcessor")
        layout {
            KSP_MAIN_DIR / "CreamSymbolProcessor".ktFile()
        }
    }
