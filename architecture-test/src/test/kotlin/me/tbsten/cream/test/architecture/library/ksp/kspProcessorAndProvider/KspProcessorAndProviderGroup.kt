package me.tbsten.cream.test.architecture.library.ksp.kspProcessorAndProvider

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.kspProcessorAndProviderGroup() =
    "KspProcessorAndProvider".group {
        summary = "KSP から呼ばれる入口（SymbolProcessor / Provider とその登録）と、各 feature に渡す ProcessContext"

        kspProcessorRole()
        kspProcessorProviderRole()
        kspProcessorProviderServiceRegistrationRole()
        kspProcessContextRole()
    }
