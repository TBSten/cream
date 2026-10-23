package me.tbsten.cream.test.architecture.architectureTest

import me.tbsten.cream.test.architecture.architectureTest.architectureDefinition.architectureDefinition
import me.tbsten.cream.test.architecture.architectureTest.architectureDocs.architectureDocs
import me.tbsten.cream.test.architecture.architectureTest.architectureSpec.architectureSpec
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.architectureTest() =
    "architectureTest".group {
        title = "architecture-test"
        summary = "This katachi definition, the one test that checks the repository against it, and the pages it generates"
        description =
            """
            cream のアーキテクチャ規約（置き場所・依存方向・命名・テスト / doc の規約）の SSoT。`./gradlew :architecture-test:test` が
            リポジトリ全体をこの定義に照らして検査し、`./gradlew :architecture-test:katachiDocs` が同じ定義から `doc/internal/architecture/`
            を再生成する。
            """.trimIndent()

        architectureDefinition()
        architectureSpec()
        architectureDocs()
    }
