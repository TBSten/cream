package me.tbsten.cream.test.architecture.creamKsp.feature.featureEntryPoint

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.declaresFeatureEntryPoint
import me.tbsten.cream.test.architecture.support.dependsOnlyOn
import me.tbsten.cream.test.architecture.support.doesNotImportAnotherFeature
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.cream.test.architecture.support.staysWithinLineBudget
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile
import me.tbsten.katachi.konsist.konsist

internal fun DeclarationContainerScope.featureEntryPoint() =
    "FeatureEntryPoint" {
        title = "Feature entry point"
        summary = "`Process<Name>.kt`: top-level `context(ctx: ProcessContext) internal fun processXxx(): List<KSAnnotated>`"
        example("ProcessCopyTo", "The entry point of `@CopyTo`")
        layout {
            KSP_MAIN_DIR / "feature" / "*" / "Process*".ktFile()
        }
        packageFollowsDirectory()
        staysWithinLineBudget()
        "is named Process<FeatureDirectory>.kt".konsist {
            files.must { file ->
                val feature = file.path.substringBeforeLast('/').substringAfterLast('/')
                file.name == "Process" + feature.replaceFirstChar(Char::uppercaseChar)
            }
        }
        declaresFeatureEntryPoint()
        doesNotImportAnotherFeature()
        dependsOnlyOn("feature.", "core.", "options.", "util.", "ProcessContext", runtime = true, kspApi = true)
    }
