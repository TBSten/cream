package me.tbsten.cream.test.architecture.architectureTest

import me.tbsten.cream.test.architecture.architectureTest.architectureDefinition.architectureDefinition
import me.tbsten.cream.test.architecture.architectureTest.architectureSpec.architectureSpec
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.architectureTest() =
    "architectureTest".group {
        title = "architecture-test"
        summary = "This katachi definition and the one test that checks the repository against it"

        architectureDefinition()
        architectureSpec()
    }
