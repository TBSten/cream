package me.tbsten.cream.test.architecture.creamKsp.core

import me.tbsten.cream.test.architecture.creamKsp.core.combineFunGenerator.combineFunGenerator
import me.tbsten.cream.test.architecture.creamKsp.core.copyFunGenerator.copyFunGenerator
import me.tbsten.cream.test.architecture.creamKsp.core.coreCommon.coreCommon
import me.tbsten.cream.test.architecture.creamKsp.core.creamException.creamException
import me.tbsten.cream.test.architecture.creamKsp.core.parentOptionalGenerator.parentOptionalGenerator
import me.tbsten.cream.test.architecture.creamKsp.core.sealedCopyGenerator.sealedCopyGenerator
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.core() =
    "core".group {
        title = "core layer"
        summary = "cream-specific code generation, split into fixed sub-packages. Nothing lives directly in `core/`"
        description = "See `.claude/rules/ksp-core-top-level.md`."

        coreCommon()
        copyFunGenerator()
        combineFunGenerator()
        sealedCopyGenerator()
        parentOptionalGenerator()
        creamException()
    }
