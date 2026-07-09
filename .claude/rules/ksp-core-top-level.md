---
paths:
  - cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/core/*.kt
---

# cream-ksp core 直下 (`ksp/core/*.kt`)

❌ **`core/` 直下に `.kt` を置かない**。必ず次のサブディレクトリへ
（`core/ArchTest`（Konsist）で強制: core ファイルは下表 5 サブパッケージのみ）。
全体アーキテクチャは `ksp-architecture.md` を参照。

| サブ | 置くもの |
|---|---|
| `core/common/` | 全生成系で共有する部品: 型パラメータ(header/where 描画)・プロパティマッチング・KDoc 生成・命名（shared へのブリッジ）・identifier escape の呼び出し・target 検証・visibility・診断ヘルパ・`GenerateSourceAnnotation` |
| `core/copyFun/` | copy 関数生成（class/object/sealed への dispatch、Class/Object/SealedClass 生成） |
| `core/combineFun/` | combine 関数生成（N source → 1 target、CombineToClass、combine の KDoc 例） |
| `core/sealedCopy/` | `@SealedCopy` 生成（`when(this)` 自己 copy、leaf 分類。肥大ファイルは責務単位で複数に分割） |
| `core/callFrom/` | `@CallFrom` ブリッジ生成（引数ホルダー → 関数呼び出しの同名オーバーロード） |

✅ core がやること: cream 固有の生成ロジック。共有部品を `common` に括り出し、`copyFun`/`combineFun`/`sealedCopy`/`callFrom` が組み合わせる（過度な汎用化はしない）。

❌ core がやってはいけないこと:
- `feature` を import する。
- `ProcessContext` を import する（→ `context(options, logger)` を使う）。
- 汎用化しすぎて util に置くべき物を抱える（汎用は `util/`、cream 固有は `core/common`）。
- 1 ファイル 500 行超（責務分割）。
