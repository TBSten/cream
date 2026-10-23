package me.tbsten.cream.test.architecture.creamKspTest.featureTest

import me.tbsten.cream.test.architecture.creamKspTest.featureTest.basicUsageTest.basicUsageTest
import me.tbsten.cream.test.architecture.creamKspTest.featureTest.edgeUsageTest.edgeUsageTest
import me.tbsten.cream.test.architecture.creamKspTest.featureTest.invalidUsageTest.invalidUsageTest
import me.tbsten.cream.test.architecture.creamKspTest.featureTest.propertyTest.propertyTest
import me.tbsten.cream.test.architecture.creamKspTest.featureTest.scenario.scenario
import me.tbsten.cream.test.architecture.creamKspTest.featureTest.snapshotTest.snapshotTest
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.featureTest() =
    "featureTest".group {
        title = "Feature tests"
        summary = "Every `feature/<name>/` carries the same five kinds of test, plus the curated scenarios its snapshot test uses"

        basicUsageTest()
        invalidUsageTest()
        edgeUsageTest()
        propertyTest()
        snapshotTest()
        scenario()
    }
