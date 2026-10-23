package me.tbsten.cream.test.architecture.creamKspTest.featureTest.basicUsageTest

import me.tbsten.cream.test.architecture.support.KSP_TEST_DIR
import me.tbsten.cream.test.architecture.support.isInternalFreeSpec
import me.tbsten.cream.test.architecture.support.isNamedAfterFeatureDirectory
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.basicUsageTest() =
    "BasicUsageTest" {
        summary = "正常系 (example-based)"
        example("CopyToBasicUsageTest")
        layout {
            KSP_TEST_DIR / "feature" / "*" / "*BasicUsageTest".ktFile()
        }
        packageFollowsDirectory()
        isNamedAfterFeatureDirectory()
        isInternalFreeSpec()
    }
