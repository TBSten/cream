[cream.kt](../README.md)

# cream-ksp tests

JVM-only kctfork compilation tests and their shared infrastructure

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

| Role | Summary |
|---|---|
| [Cross-feature test](./CrossFeatureTest.md) | フィーチャー横断（複数アノテ併用 / 複数エラー同時） |
| [Option test](./OptionTest.md) | option 系: パース・不正 option の診断・各 option が切り替える挙動 |
| [core unit test](./CoreUnitTest.md) | KSP 型に依存しない core の純ロジックのテスト（コンパイル不要） |
| [Snapshot golden file](./SnapshotGolden.md) | `assertMatchesSnapshot` が比較する golden Markdown（`snapshots/<TestName>/<testCase>.md`） |

## Placement in this group

```
cream-ksp/src/test/
  kotlin/me/tbsten/cream/ksp/
    MultipleDiagnosticsTest.kt       Cross-feature test
    *OptionTest.kt                   Option test
    Options*Test.kt                  Option test
    options/*Test.kt                 Option test
    core/*/*Test.kt                  core unit test
  resources/snapshots/*Test/**/*.md  Snapshot golden file
```

## Groups

- [Feature tests](./featureTest/README.md)
- [Test infrastructure](./testing/README.md)
