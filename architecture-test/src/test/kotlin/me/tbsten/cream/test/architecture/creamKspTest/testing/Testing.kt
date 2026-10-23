package me.tbsten.cream.test.architecture.creamKspTest.testing

import me.tbsten.cream.test.architecture.creamKspTest.testing.caseGenerator.caseGenerator
import me.tbsten.cream.test.architecture.creamKspTest.testing.compileHarness.compileHarness
import me.tbsten.cream.test.architecture.creamKspTest.testing.infrastructureTest.infrastructureTest
import me.tbsten.cream.test.architecture.creamKspTest.testing.inputBuilder.inputBuilder
import me.tbsten.cream.test.architecture.creamKspTest.testing.kotlinCodeLikeString.kotlinCodeLikeString
import me.tbsten.cream.test.architecture.creamKspTest.testing.smokeTest.smokeTest
import me.tbsten.cream.test.architecture.creamKspTest.testing.snapshotAssertion.snapshotAssertion
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.testing() =
    "testing".group {
        title = "Test infrastructure"
        summary = "テスト基盤（feature 非依存）"
        description = "共有ヘルパが必要になったら `testing/`（基盤）/ `testing/generator/`（case 生成）に追加し、この定義に Role がなければ足す。"

        compileHarness()
        snapshotAssertion()
        inputBuilder()
        infrastructureTest()
        caseGenerator()
        kotlinCodeLikeString()
        smokeTest()
    }
