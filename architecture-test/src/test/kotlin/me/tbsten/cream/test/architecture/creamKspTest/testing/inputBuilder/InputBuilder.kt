package me.tbsten.cream.test.architecture.creamKspTest.testing.inputBuilder

import me.tbsten.cream.test.architecture.support.KSP_TEST_DIR
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.inputBuilder() =
    "InputBuilder" {
        summary = "Builders of compilation inputs (`Prop` / `clazz` / `sealedInterface` / `SnapshotScenario` ...)"
        example("ClassBuilders")
        layout {
            KSP_TEST_DIR /
                "testing/poet" {
                    "*Builders".ktFile()
                    "*Scenario".ktFile()
                }
        }
        packageFollowsDirectory()
    }
