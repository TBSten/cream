package me.tbsten.cream.test.architecture.architectureTest.architectureSpec

import me.tbsten.cream.test.architecture.support.ARCHITECTURE_TEST_DIR
import me.tbsten.cream.test.architecture.support.isInternalFreeSpec
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.architectureSpec() =
    "ArchitectureSpec" {
        summary = "The single test calling `projectArchitecture.assert(FileConstraintCheck())`"
        example("ProjectArchitectureTest")
        layout {
            ARCHITECTURE_TEST_DIR / "ProjectArchitectureTest".ktFile()
        }
        packageFollowsDirectory()
        isInternalFreeSpec()
    }
