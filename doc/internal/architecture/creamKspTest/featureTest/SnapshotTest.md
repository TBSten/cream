[cream.kt](../../README.md) / [cream-ksp tests](../README.md) / [Feature tests](README.md)

# SnapshotTest

generator 駆動スナップショット（`UseCase` + `All patterns`）

- **generator × snapshot**: `scenario/` の curated case（family ごとの `Generator.snapshotScenarios(...)`）を `union` でまとめ、
  `Generator.validCreamOptions()` と `cartesian` で掛け合わせて `runCompileSnapshotTest` で golden 比較する
  （横展開の手順は `.claude/skills/cream-snapshot-test`）。snapshot は決定的に保つこと。
- **UseCase snapshot cases**: family × options の `"All patterns"` に加え、`doc/use-case/` の利用例を固定する
  `"UseCase" - { "<題材>" { ... } }` グループを置く（golden は `<Feat>SnapshotTest/UseCase/<題材>.md`。テスト名に `:` は使わない —
  golden ファイル名になるため Windows 非対応になる）。scenario は `feature/<name>/scenario/UseCases.kt` に doc のクラスを忠実に
  再現して置き（doc が定義しない型は最小 stub で補完）、options は基本 `CreamOptions.default`。doc がその利用例で明示的に option に
  言及している場合のみ `CreamOptions.default.copy(...)` のバリアントケースを追加する。doc パスとの対応・意図的な逸脱は各テスト
  クラスの KDoc に記録する。

## Placement

| Path | When to use |
|---|---|
| `cream-ksp/src/test/kotlin/me/tbsten/cream/ksp/feature/*/*SnapshotTest.kt` |  |

## Constraints

- declares the package its directory spells
- is named after its feature directory
- is an internal kotest FreeSpec

## Examples

- `CopyToSnapshotTest`
