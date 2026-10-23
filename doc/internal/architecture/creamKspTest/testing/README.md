[cream.kt](../../README.md) / [cream-ksp tests](../README.md)

# Test infrastructure

テスト基盤（feature 非依存）

共有ヘルパが必要になったら `testing/`（基盤）/ `testing/generator/`（case 生成）に追加し、この定義に Role がなければ足す。

| Role | Summary |
|---|---|
| [CompileHarness](./CompileHarness.md) | `compileWithCream`（KotlinCompilation + `useKsp2`）/ `runCompileSnapshotTest` / 結果ラッパー |
| [SnapshotAssertion](./SnapshotAssertion.md) | golden 比較（`assertMatchesSnapshot` / facet） |
| [InputBuilder](./InputBuilder.md) | 入力ビルダー（`Prop` / `clazz` / `sealedInterface` / `SnapshotScenario` …) |
| [InfrastructureTest](./InfrastructureTest.md) | A unit test of the test infrastructure itself, next to what it tests |
| [CaseGenerator](./CaseGenerator.md) | generated case 基盤（`Generator` / `union` / `cartesian` / `validCreamOptions`） |
| [KotlinCodeLikeString](./KotlinCodeLikeString.md) | `CreamOptions` → ksp config 文字列などの表示ヘルパ |
| [SmokeTest](./SmokeTest.md) | 基盤が動くことの最小確認 |

## Placement in this group

```
cream-ksp/src/test/kotlin/me/tbsten/cream/ksp/testing/
  compile/*.kt                  CompileHarness
  snapshot/*.kt                 SnapshotAssertion
  poet/
    *Builders.kt                InputBuilder
    *Scenario.kt                InputBuilder
    *Test.kt                    InfrastructureTest
  generator/**/*.kt             CaseGenerator
  kotlincodelikestring/**/*.kt  KotlinCodeLikeString
  smoke/*SmokeTest.kt           SmokeTest
```
