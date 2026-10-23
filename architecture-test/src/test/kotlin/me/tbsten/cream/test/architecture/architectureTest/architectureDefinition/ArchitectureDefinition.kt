package me.tbsten.cream.test.architecture.architectureTest.architectureDefinition

import me.tbsten.cream.test.architecture.support.ARCHITECTURE_TEST_DIR
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.architectureDefinition() =
    "ArchitectureDefinition" {
        summary = "The `architecture { }` definition: one file per group (`<group>/<Group>.kt`) and per role (`<group>/<role>/<Role>.kt`)"
        description = "shared paths and constraints live in `support/`. Do not make the split functions `inline` (katachi reads declaration sites off the stack trace)."
        example("ProjectArchitecture")
        layout {
            ARCHITECTURE_TEST_DIR {
                "ProjectArchitecture".ktFile()
                "*" / "**" / "*".ktFile()
            }
        }
        packageFollowsDirectory()
    }
