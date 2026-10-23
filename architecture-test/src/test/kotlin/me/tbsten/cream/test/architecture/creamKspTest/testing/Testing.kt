package me.tbsten.cream.test.architecture.creamKspTest.testing

import me.tbsten.cream.test.architecture.creamKspTest.testing.caseGenerator.caseGenerator
import me.tbsten.cream.test.architecture.creamKspTest.testing.compileHarness.compileHarness
import me.tbsten.cream.test.architecture.creamKspTest.testing.infrastructureTest.infrastructureTest
import me.tbsten.cream.test.architecture.creamKspTest.testing.inputBuilder.inputBuilder
import me.tbsten.cream.test.architecture.creamKspTest.testing.konsistSupport.konsistSupport
import me.tbsten.cream.test.architecture.creamKspTest.testing.kotlinCodeLikeString.kotlinCodeLikeString
import me.tbsten.cream.test.architecture.creamKspTest.testing.smokeTest.smokeTest
import me.tbsten.cream.test.architecture.creamKspTest.testing.snapshotAssertion.snapshotAssertion
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.testing() =
    "testing".group {
        title = "Test infrastructure"
        summary = "Feature-independent infrastructure shared by the cream-ksp tests"

        compileHarness()
        snapshotAssertion()
        inputBuilder()
        infrastructureTest()
        caseGenerator()
        kotlinCodeLikeString()
        smokeTest()
        konsistSupport()
    }
