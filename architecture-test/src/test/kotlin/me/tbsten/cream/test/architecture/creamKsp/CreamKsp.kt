package me.tbsten.cream.test.architecture.creamKsp

import me.tbsten.cream.test.architecture.creamKsp.compositionRoot.compositionRoot
import me.tbsten.cream.test.architecture.creamKsp.core.core
import me.tbsten.cream.test.architecture.creamKsp.feature.feature
import me.tbsten.cream.test.architecture.creamKsp.kspOption.kspOption
import me.tbsten.cream.test.architecture.creamKsp.util.util
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.creamKsp() =
    "creamKsp".group {
        title = "cream-ksp"
        summary = "The KSP processor: three layers (feature → core → util) plus a composition root and its context"
        description =
            """
            The layering and its dependency table are described in `.claude/rules/ksp-architecture.md`.
            The import boundaries between feature / core / util are enforced by the Konsist specs in `cream-ksp`'s
            own tests (`AllKotlinFilesTest` / `feature/ArchTest` / `core/ArchTest`); this definition owns where each file may live.
            """.trimIndent()

        compositionRoot()
        feature()
        core()
        kspOption()
        util()
    }
