package me.tbsten.cream.test.architecture.creamKspTest.konsistArchTest

import me.tbsten.cream.test.architecture.support.KSP_TEST_DIR
import me.tbsten.cream.test.architecture.support.isInternalFreeSpec
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.konsistArchTest() =
    "KonsistArchTest" {
        title = "Konsist architecture spec"
        summary = "A Konsist spec enforcing the import boundaries of feature / core / util"
        example("AllKotlinFilesTest")
        layout {
            KSP_TEST_DIR {
                "AllKotlinFilesTest".ktFile()
                "feature" / "ArchTest".ktFile()
                "core" / "ArchTest".ktFile()
            }
        }
        packageFollowsDirectory()
        isInternalFreeSpec()
    }
