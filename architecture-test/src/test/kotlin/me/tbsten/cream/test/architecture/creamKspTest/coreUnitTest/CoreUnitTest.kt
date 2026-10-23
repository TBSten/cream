package me.tbsten.cream.test.architecture.creamKspTest.coreUnitTest

import me.tbsten.cream.test.architecture.support.KSP_TEST_DIR
import me.tbsten.cream.test.architecture.support.isInternalFreeSpec
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.coreUnitTest() =
    "CoreUnitTest" {
        title = "core unit test"
        summary = "A pure-logic test of core that needs no compilation"
        example("CopyFunctionNameTest")
        layout {
            KSP_TEST_DIR / "core" / "*" / "*Test".ktFile()
        }
        packageFollowsDirectory()
        isInternalFreeSpec()
    }
