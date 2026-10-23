package me.tbsten.cream.test.architecture.creamKspTest.featureTest.snapshotTest

import me.tbsten.cream.test.architecture.support.KSP_TEST_DIR
import me.tbsten.cream.test.architecture.support.isInternalFreeSpec
import me.tbsten.cream.test.architecture.support.isNamedAfterFeatureDirectory
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.snapshotTest() =
    "SnapshotTest" {
        summary = "generator 駆動スナップショット（`UseCase` + `All patterns`）"
        description =
            """
            - **generator × snapshot**: `scenario/` の curated case（family ごとの `Generator.snapshotScenarios(...)`）を `union` でまとめ、
              `Generator.validCreamOptions()` と `cartesian` で掛け合わせて `runCompileSnapshotTest` で golden 比較する
              （横展開の手順は `.claude/skills/cream-snapshot-test`）。snapshot は決定的に保つこと。
            - **UseCase snapshot cases**: family × options の `"All patterns"` に加え、`doc/use-case/` の利用例を固定する
              `"UseCase" - { "<題材>" { ... } }` グループを置く（golden は `<Feat>SnapshotTest/UseCase/<題材>.md`。テスト名に `:` は使わない —
              golden ファイル名になるため Windows 非対応になる）。scenario は `feature/<name>/scenario/UseCases.kt` に doc のクラスを忠実に
              再現して置き（doc が定義しない型は最小 stub で補完）、options は基本 `CreamOptions.default`。doc がその利用例で明示的に option に
              言及している場合のみ `CreamOptions.default.copy(...)` のバリアントケースを追加する。doc パスとの対応・意図的な逸脱は各テスト
              クラスの KDoc に記録する。
            """.trimIndent()
        example("CopyToSnapshotTest")
        layout {
            KSP_TEST_DIR / "feature" / "*" / "*SnapshotTest".ktFile()
        }
        packageFollowsDirectory()
        isNamedAfterFeatureDirectory()
        isInternalFreeSpec()
    }
