package me.tbsten.cream.test.architecture.test

import me.tbsten.cream.test.architecture.test.architectureTest.architectureTestGroup
import me.tbsten.cream.test.architecture.test.integrationTest.integrationTestGroup
import me.tbsten.cream.test.architecture.test.kspTest.kspTestGroup
import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.testGroup() =
    "Test".group {
        summary = "テスト（cream-ksp 自身のテスト、生成コードの結合テスト、この katachi の定義）"

        kspTestGroup()
        integrationTestGroup()
        architectureTestGroup()
    }
