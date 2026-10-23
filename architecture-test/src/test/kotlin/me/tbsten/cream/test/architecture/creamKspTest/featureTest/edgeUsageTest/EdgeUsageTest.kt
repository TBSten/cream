package me.tbsten.cream.test.architecture.creamKspTest.featureTest.edgeUsageTest

import me.tbsten.cream.test.architecture.support.KSP_TEST_DIR
import me.tbsten.cream.test.architecture.support.isInternalFreeSpec
import me.tbsten.cream.test.architecture.support.isNamedAfterFeatureDirectory
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.edgeUsageTest() =
    "EdgeUsageTest" {
        summary = "レアケース ＋ `@Map` / `@Exclude` 等の意味的ケース"
        example("CopyToEdgeUsageTest")
        layout {
            KSP_TEST_DIR / "feature" / "*" / "*EdgeUsageTest".ktFile()
        }
        packageFollowsDirectory()
        isNamedAfterFeatureDirectory()
        isInternalFreeSpec()
    }
