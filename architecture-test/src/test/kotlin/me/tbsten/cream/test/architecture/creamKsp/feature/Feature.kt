package me.tbsten.cream.test.architecture.creamKsp.feature

import me.tbsten.cream.test.architecture.creamKsp.feature.featureDiagnostics.featureDiagnostics
import me.tbsten.cream.test.architecture.creamKsp.feature.featureEntryPoint.featureEntryPoint
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.feature() =
    "feature".group {
        title = "feature layer"
        summary = "One directory per annotation: discover → extract arguments → validate → call core. No generation logic"
        description = "See `.claude/rules/ksp-feature-top-level.md`. Nothing lives directly in `feature/`."

        featureEntryPoint()
        featureDiagnostics()
    }
