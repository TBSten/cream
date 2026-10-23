package me.tbsten.cream.test.architecture.creamKspTest.featureTest.invalidUsageTest

import me.tbsten.cream.test.architecture.support.KSP_TEST_DIR
import me.tbsten.cream.test.architecture.support.isInternalFreeSpec
import me.tbsten.cream.test.architecture.support.isNamedAfterFeatureDirectory
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.invalidUsageTest() =
    "InvalidUsageTest" {
        summary = "Invalid usage that must end in a compilation error, compared against `*.output.md` golden files"
        example("CopyToInvalidUsageTest")
        layout {
            KSP_TEST_DIR / "feature" / "*" / "*InvalidUsageTest".ktFile()
        }
        packageFollowsDirectory()
        isNamedAfterFeatureDirectory()
        isInternalFreeSpec()
    }
