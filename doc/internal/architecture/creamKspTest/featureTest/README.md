[cream.kt](../../README.md) / [cream-ksp tests](../README.md)

# Feature tests

Every `feature/<name>/` carries the same five kinds of test, plus the curated scenarios its snapshot test uses

- **feature 5 種**: 各 feature は `BasicUsage` / `InvalidUsage` / `EdgeUsage` / `Property` / `Snapshot` の 5 ファイルを持つ
  （正常系 / 不正系 / エッジ / PBT / snapshot）。テストを足すときは該当ファイルに追加し、まだ `xtest` スタブのファイルは
  中身を実装し直す形で置き換える。
- **現状 (#127)**: 全 feature の `<Feat>SnapshotTest`（`scenario/` 付き）は実装済み。`BasicUsage` / `InvalidUsage` / `EdgeUsage` /
  `Property` は一部を除きまだ `xtest` の空スタブ（`// TODO(#127): reimplement ...`）で、順次実装し直す。

| Role | Summary |
|---|---|
| [BasicUsageTest](./BasicUsageTest.md) | 正常系 (example-based) |
| [InvalidUsageTest](./InvalidUsageTest.md) | 不正利用 → エラー (diagnostic) |
| [EdgeUsageTest](./EdgeUsageTest.md) | レアケース ＋ `@Map` / `@Exclude` 等の意味的ケース |
| [PropertyTest](./PropertyTest.md) | PBT（可視性 / エスケープ / オプションは generator 次元で網羅） |
| [SnapshotTest](./SnapshotTest.md) | generator 駆動スナップショット（`UseCase` + `All patterns`） |
| [Scenario](./Scenario.md) | curated case（family ごとの `*Scenarios()` + `UseCases.kt`） |

## Placement in this group

```
cream-ksp/src/test/kotlin/me/tbsten/cream/ksp/feature/*/
  *BasicUsageTest.kt    BasicUsageTest
  *InvalidUsageTest.kt  InvalidUsageTest
  *EdgeUsageTest.kt     EdgeUsageTest
  *PropertyTest.kt      PropertyTest
  *SnapshotTest.kt      SnapshotTest
  scenario/*.kt         Scenario
```
