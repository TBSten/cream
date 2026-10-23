package me.tbsten.cream.test.architecture.creamKsp.compositionRoot.symbolProcessor

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.staysWithinLineBudget
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.symbolProcessor() =
    "SymbolProcessor" {
        summary = "`CreamSymbolProcessor`: `process()` で全 feature を順に dispatch する。option パース・round / deferred 集約"
        example("CreamSymbolProcessor")
        layout {
            KSP_MAIN_DIR / "CreamSymbolProcessor".ktFile()
        }
        staysWithinLineBudget()
    }
