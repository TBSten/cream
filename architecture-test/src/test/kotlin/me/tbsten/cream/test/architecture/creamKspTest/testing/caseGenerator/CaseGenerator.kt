package me.tbsten.cream.test.architecture.creamKspTest.testing.caseGenerator

import me.tbsten.cream.test.architecture.support.KSP_TEST_DIR
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.caseGenerator() =
    "CaseGenerator" {
        summary = "generated case 基盤（`Generator` / `union` / `cartesian` / `validCreamOptions`）"
        example("Generator")
        layout {
            KSP_TEST_DIR / "testing/generator" / "**" / "*".ktFile()
        }
        packageFollowsDirectory()
    }
