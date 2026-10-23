package me.tbsten.cream.test.architecture.creamKsp.feature.featureDiagnostics

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.isNamedAfterFeatureDirectory
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.featureDiagnostics() =
    "FeatureDiagnostics" {
        title = "Feature diagnostics"
        summary = "Validation and diagnostic helpers specific to one annotation"
        example("ChildOptionalsDiagnostics")
        layout {
            KSP_MAIN_DIR / "feature" / "*" / "*Diagnostics".ktFile()
        }
        packageFollowsDirectory()
        isNamedAfterFeatureDirectory()
    }
