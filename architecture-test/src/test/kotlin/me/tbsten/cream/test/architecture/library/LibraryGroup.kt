package me.tbsten.cream.test.architecture.library

import me.tbsten.cream.test.architecture.library.ksp.kspGroup
import me.tbsten.cream.test.architecture.library.runtime.runtimeGroup
import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.libraryGroup() =
    "Library".group {
        summary = "ライブラリ本体"

        runtimeGroup()
        kspGroup()
    }
