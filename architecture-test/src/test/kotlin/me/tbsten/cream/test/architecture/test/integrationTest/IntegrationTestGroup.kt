package me.tbsten.cream.test.architecture.test.integrationTest

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.integrationTestGroup() =
    "IntegrationTest".group {
        summary = "KSP で実際に生成したコードを呼んで確かめる結合テスト（test モジュール）"

        integrationTestDataRole()
        integrationTestRole()
    }
