package me.tbsten.cream.test.architecture.library.ksp.core

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.coreGroup() =
    "Core".group {
        summary = "cream 固有のコード生成（feature から呼ばれる）"

        coreCommonRole()
        coreGeneratorRole()
        coreErrorRole()
    }
