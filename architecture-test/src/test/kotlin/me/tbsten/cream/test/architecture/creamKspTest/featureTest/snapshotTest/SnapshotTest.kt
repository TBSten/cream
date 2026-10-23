package me.tbsten.cream.test.architecture.creamKspTest.featureTest.snapshotTest

import me.tbsten.cream.test.architecture.support.KSP_TEST_DIR
import me.tbsten.cream.test.architecture.support.isInternalFreeSpec
import me.tbsten.cream.test.architecture.support.isNamedAfterFeatureDirectory
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.snapshotTest() =
    "SnapshotTest" {
        summary = "Generator-driven golden snapshot tests (`UseCase` + `All patterns`)"
        example("CopyToSnapshotTest")
        layout {
            KSP_TEST_DIR / "feature" / "*" / "*SnapshotTest".ktFile()
        }
        packageFollowsDirectory()
        isNamedAfterFeatureDirectory()
        isInternalFreeSpec()
    }
