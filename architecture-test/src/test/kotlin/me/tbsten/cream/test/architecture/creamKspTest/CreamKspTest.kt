package me.tbsten.cream.test.architecture.creamKspTest

import me.tbsten.cream.test.architecture.creamKspTest.coreUnitTest.coreUnitTest
import me.tbsten.cream.test.architecture.creamKspTest.crossFeatureTest.crossFeatureTest
import me.tbsten.cream.test.architecture.creamKspTest.featureTest.featureTest
import me.tbsten.cream.test.architecture.creamKspTest.optionTest.optionTest
import me.tbsten.cream.test.architecture.creamKspTest.snapshotGolden.snapshotGolden
import me.tbsten.cream.test.architecture.creamKspTest.testing.testing
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.creamKspTest() =
    "creamKspTest".group {
        title = "cream-ksp tests"
        summary = "JVM-only kctfork compilation tests and their shared infrastructure"
        description =
            """
            [kctfork](https://github.com/zacsweers/kotlin-compile-testing)（`dev.zacsweers.kctfork:core` / `:ksp`）を使った JVM 専用の
            end-to-end テスト。マルチプラットフォームの `test/` モジュールでは表現しにくいシナリオを補完する。

            テストは [kotest](https://kotest.io) の `FreeSpec` スタイルで書く（`internal class XxxTest : FreeSpec({ "..." { ... } })`、
            ネストは `"group" - { "..." { ... } }`）。無効化テストは `"...".config(enabled = false) { }`。assert は kotest matcher を使い、
            **語順は `actual shouldBe expected`**（kotlin.test の `assertEquals(expected, actual)` とは逆）。失敗時メッセージは
            `withClue(message) { ... }` で保持する。cream-ksp は JVM のみなので `kotest-runner-junit5` + `tasks.test { useJUnitPlatform() }`
            で動く（KSP も io.kotest プラグインも不要）。

            新しいテストを足すとき:

            - 対象の feature が決まっているなら `feature/<name>/` の該当 5 種ファイルに追加する。
            - feature 横断のシナリオ（複数アノテ併用 / 複数エラー）は `MultipleDiagnosticsTest.kt` に置く。
            - KSP 型に依存しない core の純ロジックは `core/<sub>/` 配下のテストに置く（コンパイル不要）。
            - 共有ヘルパーは `testing/` 配下へ。
            """.trimIndent()

        crossFeatureTest()
        optionTest()
        featureTest()
        coreUnitTest()
        snapshotGolden()
        testing()
    }
