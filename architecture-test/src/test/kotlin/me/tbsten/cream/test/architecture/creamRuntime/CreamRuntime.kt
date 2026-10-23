package me.tbsten.cream.test.architecture.creamRuntime

import me.tbsten.cream.test.architecture.creamRuntime.runtimeApi.runtimeApi
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.creamRuntime() =
    "creamRuntime".group {
        title = "cream-runtime"
        summary = "The multiplatform artifact users depend on: the annotations and the constants their arguments take"

        runtimeApi()
    }
