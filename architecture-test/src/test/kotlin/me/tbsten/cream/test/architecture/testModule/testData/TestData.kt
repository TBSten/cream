package me.tbsten.cream.test.architecture.testModule.testData

import me.tbsten.cream.test.architecture.support.INTEGRATION_TEST_DATA_DIR
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.testData() =
    "TestData" {
        title = "Integration test data"
        summary = "Annotated classes cream generates code from, one directory per feature"
        example("Basic", "`copyTo/Basic.kt`, used by `copyTo/BasicTest.kt`")
        layout {
            INTEGRATION_TEST_DATA_DIR / "*" / "**" / "*".ktFile()
        }
        packageFollowsDirectory()
    }
