package me.tbsten.cream.test.architecture.creamKsp.feature.featureDiagnostics

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.dependsOnlyOn
import me.tbsten.cream.test.architecture.support.doesNotImportAnotherFeature
import me.tbsten.cream.test.architecture.support.isNamedAfterFeatureDirectory
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.cream.test.architecture.support.staysWithinLineBudget
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.featureDiagnostics() =
    "FeatureDiagnostics" {
        title = "Feature diagnostics"
        summary = "その注釈に固有の補助: 引数 parse・注釈固有の検証 / 診断ヘルパ"
        example("ChildOptionalsDiagnostics")
        layout {
            KSP_MAIN_DIR / "feature" / "*" / "*Diagnostics".ktFile()
        }
        packageFollowsDirectory()
        staysWithinLineBudget()
        isNamedAfterFeatureDirectory()
        doesNotImportAnotherFeature()
        dependsOnlyOn("feature.", "core.", "options.", "util.", "ProcessContext", runtime = true, kspApi = true)
    }
