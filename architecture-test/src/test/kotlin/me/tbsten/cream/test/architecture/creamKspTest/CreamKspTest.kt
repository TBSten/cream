package me.tbsten.cream.test.architecture.creamKspTest

import me.tbsten.cream.test.architecture.creamKspTest.coreUnitTest.coreUnitTest
import me.tbsten.cream.test.architecture.creamKspTest.crossFeatureTest.crossFeatureTest
import me.tbsten.cream.test.architecture.creamKspTest.featureTest.featureTest
import me.tbsten.cream.test.architecture.creamKspTest.konsistArchTest.konsistArchTest
import me.tbsten.cream.test.architecture.creamKspTest.optionTest.optionTest
import me.tbsten.cream.test.architecture.creamKspTest.snapshotGolden.snapshotGolden
import me.tbsten.cream.test.architecture.creamKspTest.testing.testing
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.creamKspTest() =
    "creamKspTest".group {
        title = "cream-ksp tests"
        summary = "JVM-only kctfork compilation tests, the Konsist architecture specs and their shared infrastructure"
        description = "The layout is described in `.claude/rules/ksp-test.md`."

        konsistArchTest()
        crossFeatureTest()
        optionTest()
        featureTest()
        coreUnitTest()
        snapshotGolden()
        testing()
    }
