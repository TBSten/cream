package me.tbsten.cream.test.architecture.creamKspTest.optionTest

import me.tbsten.cream.test.architecture.support.KSP_TEST_DIR
import me.tbsten.cream.test.architecture.support.isInternalFreeSpec
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.optionTest() =
    "OptionTest" {
        title = "Option test"
        summary = "option 系: パース・不正 option の診断・各 option が切り替える挙動"
        example("DefaultVisibilityOptionTest")
        layout {
            KSP_TEST_DIR {
                "*OptionTest".ktFile()
                "Options*Test".ktFile()
                "options" / "*Test".ktFile()
            }
        }
        packageFollowsDirectory()
        isInternalFreeSpec()
    }
