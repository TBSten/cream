package me.tbsten.cream.test.architecture.creamKsp.compositionRoot.serviceRegistration

import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.serviceRegistration() =
    "ServiceRegistration" {
        summary = "The `META-INF/services` entry through which KSP discovers `CreamSymbolProcessorProvider`"
        example("com.google.devtools.ksp.processing.SymbolProcessorProvider")
        layout {
            "cream-ksp/src/main/resources/META-INF/services" / "com.google.devtools.ksp.processing.SymbolProcessorProvider".file()
        }
    }
