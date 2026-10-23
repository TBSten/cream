package me.tbsten.cream.test.architecture.creamKsp.feature.featureEntryPoint

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile
import me.tbsten.katachi.konsist.konsist

internal fun DeclarationContainerScope.featureEntryPoint() =
    "FeatureEntryPoint" {
        title = "Feature entry point"
        summary = "`Process<Name>.kt` holding `context(ctx: ProcessContext) internal fun processXxx(): List<KSAnnotated>`"
        example("ProcessCopyTo", "The entry point of `@CopyTo`")
        layout {
            KSP_MAIN_DIR / "feature" / "*" / "Process*".ktFile()
        }
        packageFollowsDirectory()
        "is named Process<FeatureDirectory>.kt".konsist {
            files.must { file ->
                val feature = file.path.substringBeforeLast('/').substringAfterLast('/')
                file.name == "Process" + feature.replaceFirstChar(Char::uppercaseChar)
            }
        }
    }
