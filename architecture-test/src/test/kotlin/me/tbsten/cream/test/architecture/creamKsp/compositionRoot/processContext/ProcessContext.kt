package me.tbsten.cream.test.architecture.creamKsp.compositionRoot.processContext

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.importsOnly
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.processContext() =
    "ProcessContext" {
        summary = "`ProcessContext = {resolver, options, codeGenerator, logger}`: a leaf every feature receives as a context parameter"
        example("ProcessContext")
        layout {
            KSP_MAIN_DIR / "ProcessContext".ktFile()
        }
        importsOnly("me.tbsten.cream.ksp.options.", forbidKspApi = false)
    }
