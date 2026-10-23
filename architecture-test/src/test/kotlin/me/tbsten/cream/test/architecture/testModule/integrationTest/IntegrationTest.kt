package me.tbsten.cream.test.architecture.testModule.integrationTest

import me.tbsten.cream.test.architecture.support.INTEGRATION_TEST_DIR
import me.tbsten.cream.test.architecture.support.isFreeSpec
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.integrationTest() =
    "IntegrationTest" {
        title = "Integration test"
        summary = "A kotest spec calling the generated functions: `<feature>/<Name>Test.kt`, named after the data it uses"
        example("BasicTest", "`copyTo/BasicTest.kt`")
        layout {
            INTEGRATION_TEST_DIR / "*" / "*Test".ktFile()
        }
        packageFollowsDirectory()
        isFreeSpec()
    }
