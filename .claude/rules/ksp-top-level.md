---
paths:
  - cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/*.kt
---

# cream-ksp top-level (`ksp/*.kt`)

`ksp/` 直下は **オーケストレーション + 横断型のみ**。生成ロジックは置かない。
全体アーキテクチャは `ksp-architecture.md` を参照。

直下に置いてよいもの:

- `CreamSymbolProcessor.kt` — `process()` で全 feature を順に dispatch。option パース・round/deferred 集約。
- `CreamSymbolProcessorProvider.kt` — KSP provider（環境から ProcessContext の材料を取り出す）。
- `ProcessContext.kt` — `{resolver, options, codeGenerator, logger}`。leaf infra（feature/core を import しない）。
- `GenerateSourceAnnotation.kt` — 横断型（生成元注釈の sealed）。

> 補足: `GenerateSourceAnnotation` は core が生成時に使う型のため `core/common` への配置が依存方向上は自然（`ksp-architecture.md` の依存表参照）。配置はリファクタ実装時に中身の責務で判断する。

❌ いけないこと:
- 生成ロジックを書く（→ `core/`）
- 注釈ごとの処理を書く（→ `feature/<name>/`）
- 汎用ヘルパを書く（→ `util/`）
- `ProcessContext.kt` から feature/core を import する
