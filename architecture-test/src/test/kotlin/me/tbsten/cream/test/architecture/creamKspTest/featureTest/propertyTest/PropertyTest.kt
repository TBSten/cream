package me.tbsten.cream.test.architecture.creamKspTest.featureTest.propertyTest

import me.tbsten.cream.test.architecture.support.KSP_TEST_DIR
import me.tbsten.cream.test.architecture.support.isInternalFreeSpec
import me.tbsten.cream.test.architecture.support.isNamedAfterFeatureDirectory
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.propertyTest() =
    "PropertyTest" {
        summary = "Property-based tests over generated inputs"
        example("CopyToPropertyTest")
        layout {
            KSP_TEST_DIR / "feature" / "*" / "*PropertyTest".ktFile()
        }
        packageFollowsDirectory()
        isNamedAfterFeatureDirectory()
        isInternalFreeSpec()
    }
