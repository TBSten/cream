package me.tbsten.cream.test.architecture.creamKspTest.testing.smokeTest

import me.tbsten.cream.test.architecture.support.KSP_TEST_DIR
import me.tbsten.cream.test.architecture.support.isInternalFreeSpec
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.smokeTest() =
    "SmokeTest" {
        summary = "A minimal check that a piece of the infrastructure works at all"
        example("CreamCompilationSmokeTest")
        layout {
            KSP_TEST_DIR / "testing/smoke" / "*SmokeTest".ktFile()
        }
        packageFollowsDirectory()
        isInternalFreeSpec()
    }
