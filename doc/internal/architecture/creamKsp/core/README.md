[cream.kt](../../README.md) / [cream-ksp](../README.md)

# core layer

cream-specific code generation, split into fixed sub-packages. Nothing lives directly in `core/`

❌ **`core/` 直下に `.kt` を置かない**。必ず下の 6 サブディレクトリ（= この group の Role）のどれかに置く。

✅ core がやること: cream 固有の生成ロジック。共有部品を `common` に括り出し、`copyFun` / `combineFun` / `sealedCopy` /
`parentOptional` が組み合わせる（過度な汎用化はしない）。

❌ core がやってはいけないこと:
- `feature` を import する。
- `ProcessContext` / `CreamSymbolProcessor` / `Provider` を import する（→ `context(options, logger)` を使う）。
- `core/error/` から `util` 以外を import する（葉のまま保つ）。
- 汎用化しすぎて util に置くべき物を抱える（汎用は `util/`、cream 固有は `core/common`）。
- 1 ファイル 500 行超（責務分割）。

生成関数は `context(options: CreamOptions, logger: KSPLogger) fun Appendable.appendXxx(...)`（文字列 append ベース。KotlinPoet 不使用）。
resolver / codeGenerator は受け取らない。

| Role | Summary |
|---|---|
| [core/common](./CoreCommon.md) | 全生成系で共有する部品: 型パラメータ・sealed 型レンダリング・プロパティマッチング・KDoc・命名・target 検証・visibility・診断ヘルパ |
| [core/copyFun](./CopyFunGenerator.md) | copy 関数生成（class / object / sealed への dispatch、Class / Object / SealedClass 生成） |
| [core/combineFun](./CombineFunGenerator.md) | combine 関数生成（N source → 1 target、CombineToClass、combine の KDoc 例） |
| [core/sealedCopy](./SealedCopyGenerator.md) | `@SealedCopy` 生成（`when(this)` 自己 copy、leaf 分類。肥大ファイルは責務単位で複数に分割） |
| [core/parentOptional](./ParentOptionalGenerator.md) | `@ParentOptional` / `@ChildOptionals` 生成（sealed 親への nullable 拡張プロパティ accessor。両 feature が共有） |
| [core/error](./CreamException.md) | 例外階層（`CreamException` / `InvalidCreamUsageException` / `InvalidCreamOptionException` / `UnknownCreamException` と `reportToGithub`） |

## Placement in this group

```
cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/core/
  common/*.kt          core/common
  copyFun/*.kt         core/copyFun
  combineFun/*.kt      core/combineFun
  sealedCopy/*.kt      core/sealedCopy
  parentOptional/*.kt  core/parentOptional
  error/*.kt           core/error
```
