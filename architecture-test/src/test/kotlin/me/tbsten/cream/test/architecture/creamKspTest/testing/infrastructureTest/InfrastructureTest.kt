package me.tbsten.cream.test.architecture.creamKspTest.testing.infrastructureTest

import me.tbsten.cream.test.architecture.support.KSP_TEST_DIR
import me.tbsten.cream.test.architecture.support.isInternalFreeSpec
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.infrastructureTest() =
    "InfrastructureTest" {
        summary = "A unit test of the test infrastructure itself, next to what it tests"
        example("SnapshotScenarioTest")
        layout {
            KSP_TEST_DIR / "testing/poet" / "*Test".ktFile()
        }
        packageFollowsDirectory()
        isInternalFreeSpec()
    }
