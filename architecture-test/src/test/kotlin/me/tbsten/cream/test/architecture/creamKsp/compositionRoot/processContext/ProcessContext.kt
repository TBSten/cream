package me.tbsten.cream.test.architecture.creamKsp.compositionRoot.processContext

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.dependsOnlyOn
import me.tbsten.cream.test.architecture.support.staysWithinLineBudget
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.processContext() =
    "ProcessContext" {
        summary = "`ProcessContext = {resolver, options, codeGenerator, logger}`: every feature receives it as a context parameter"
        description =
            """
            leaf infra: KSP API と `options`（`CreamOptions`）だけに依存し、feature / core / util / `CreamSymbolProcessor` を import しない。
            **`logger` は必須**（`KSPLogger?` 禁止）。
            """.trimIndent()
        example("ProcessContext")
        layout {
            KSP_MAIN_DIR / "ProcessContext".ktFile()
        }
        staysWithinLineBudget()
        dependsOnlyOn("options.", runtime = false, kspApi = true)
    }
