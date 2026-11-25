package me.tbsten.cream.test.architecture.library.runtime

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.runtimeGroup() =
    "Runtime".group {
        summary = "利用者が依存するアノテーション"

        runtimeApiRole()
    }
