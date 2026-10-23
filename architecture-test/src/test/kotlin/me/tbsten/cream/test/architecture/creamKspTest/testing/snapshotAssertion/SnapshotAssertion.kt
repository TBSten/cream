package me.tbsten.cream.test.architecture.creamKspTest.testing.snapshotAssertion

import me.tbsten.cream.test.architecture.support.KSP_TEST_DIR
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.snapshotAssertion() =
    "SnapshotAssertion" {
        summary = "The golden comparison: `assertMatchesSnapshot` and its facets"
        example("SnapshotAssertion")
        layout {
            KSP_TEST_DIR / "testing/snapshot" / "*".ktFile()
        }
        packageFollowsDirectory()
    }
