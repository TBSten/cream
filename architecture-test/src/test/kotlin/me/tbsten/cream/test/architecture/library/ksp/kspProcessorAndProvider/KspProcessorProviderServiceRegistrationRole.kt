package me.tbsten.cream.test.architecture.library.ksp.kspProcessorAndProvider

import me.tbsten.cream.test.architecture.KSP_MAIN_RESOURCES
import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.kspProcessorProviderServiceRegistrationRole() =
    "KspProcessorProviderServiceRegistration".role {
        documented = false
        layout {
            // cream-ksp/src/main/resources/META-INF/services/com.google.devtools.ksp.processing.SymbolProcessorProvider
            KSP_MAIN_RESOURCES / "META-INF/services/com.google.devtools.ksp.processing.SymbolProcessorProvider".file()
        }
    }
