package me.tbsten.cream.test.architecture.creamKspTest.testing.konsistSupport

import me.tbsten.cream.test.architecture.support.KSP_TEST_DIR
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.konsistSupport() =
    "KonsistSupport" {
        summary = "The Konsist scope and layer helpers shared by the three architecture specs"
        example("KonsistSupport")
        layout {
            KSP_TEST_DIR / "testing/konsist" / "*".ktFile()
        }
        packageFollowsDirectory()
    }
