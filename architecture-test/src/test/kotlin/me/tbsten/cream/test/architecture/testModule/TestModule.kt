package me.tbsten.cream.test.architecture.testModule

import me.tbsten.cream.test.architecture.testModule.integrationTest.integrationTest
import me.tbsten.cream.test.architecture.testModule.testData.testData
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.testModule() =
    "testModule".group {
        title = "test module"
        summary = "Multiplatform integration tests: annotated classes in commonMain, run through KSP, called from commonTest"

        testData()
        integrationTest()
    }
