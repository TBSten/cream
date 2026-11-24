package me.tbsten.cream.test.architecture.library.ksp

import me.tbsten.cream.test.architecture.library.ksp.core.coreGroup
import me.tbsten.cream.test.architecture.library.ksp.kspProcessorAndProvider.kspProcessorAndProviderGroup
import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.kspGroup() =
    "Ksp".group {
        summary = "KSP プロセッサの本体"

        kspProcessorAndProviderGroup()

        featureProcessRole()
        featureSupportRole()
        kspOptionRole()
        utilRole()
        kspUtilRole()
        coreGroup()
    }
