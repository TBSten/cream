package me.tbsten.cream.test.architecture.creamKsp.compositionRoot

import me.tbsten.cream.test.architecture.creamKsp.compositionRoot.processContext.processContext
import me.tbsten.cream.test.architecture.creamKsp.compositionRoot.serviceRegistration.serviceRegistration
import me.tbsten.cream.test.architecture.creamKsp.compositionRoot.symbolProcessor.symbolProcessor
import me.tbsten.cream.test.architecture.creamKsp.compositionRoot.symbolProcessorProvider.symbolProcessorProvider
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.compositionRoot() =
    "compositionRoot".group {
        title = "Composition root"
        summary = "The KSP entry point and the context it hands to every feature: the only files directly in `me.tbsten.cream.ksp`"
        description = "See `.claude/rules/ksp-top-level.md`."

        symbolProcessor()
        symbolProcessorProvider()
        processContext()
        serviceRegistration()
    }
