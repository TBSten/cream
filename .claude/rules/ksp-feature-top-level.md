---
paths:
  - cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/feature/*.kt
---

# cream-ksp feature 直下 (`ksp/feature/*.kt`)

❌ **`feature/` 直下に `.kt` を置かない**。必ず `feature/<name>/` サブディレクトリに置く。
全体アーキテクチャは `ksp-architecture.md` を参照。

`<name>` は 8 注釈: `copyTo` `copyFrom` `copyToChildren` `sealedCopy` `combineTo` `combineFrom` `copyMapping` `combineMapping`（1 注釈 = 1 ディレクトリ）。

各 `feature/<name>/` に置くもの:
- `Process<Name>.kt`: top-level 関数 `context(ctx: ProcessContext) fun processXxx(): List<KSAnnotated>`。
- その注釈に固有の補助（引数 parse、注釈固有の検証ヘルパ等）。

✅ feature がやること:
- `resolver.getSymbolsWithAnnotation(...)` で発見 → `validate()` で partition。
- 注釈引数の抽出（targets/sources、KDoc、visibility、funName template 等）と検証。
- core の生成関数を呼び、`createNewKotlinFile` で書き出し。
- 未解決シンボル（invalid）を返す（KSP の次ラウンド用）。

❌ feature がやってはいけないこと:
- 生成コードの組み立て（型param/where/プロパティマッチ/本体）を書く（→ `core/`）。
- 他の `feature/<name>` を import する（feature 間依存禁止）。
- 注釈固有でない汎用処理を書く（→ `core/common` か `util/`）。
