package me.tbsten.cream.test.architecture.creamKspTest.featureTest.scenario

import me.tbsten.cream.test.architecture.support.KSP_TEST_DIR
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.scenario() =
    "Scenario" {
        summary = "Curated snapshot cases: one `*Scenarios()` family per file, plus `UseCases.kt` mirroring `doc/use-case/`"
        example("UseCases")
        layout {
            KSP_TEST_DIR / "feature" / "*" / "scenario" / "*".ktFile()
        }
        packageFollowsDirectory()
    }
