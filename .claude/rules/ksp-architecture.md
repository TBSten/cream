---
paths:
  - cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/**/*.kt
---

# cream-ksp Architecture (feature / core / util)

`cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/**` は **3 層 + composition root + context** で構成する。
公開 API（runtime 注釈 / shared options）は変えない `internal` 実装の規約。

## Layers

| Layer | 場所 | 責務 |
|---|---|---|
| top-level (root) | `ksp/*.kt` | KSP エントリ + 横断 infra（`CreamSymbolProcessor` / `Provider` / `ProcessContext` / `GenerateSourceAnnotation`）。詳細は `ksp-top-level.md` |
| feature | `ksp/feature/<name>/` | 注釈ごとの入口「発見 → 引数抽出 → 検証 → core 呼び出し」。生成ロジックは持たない。詳細は `ksp-feature-top-level.md` |
| core | `ksp/core/<sub>/` | cream 固有の生成ロジック（`common` / `copyFun` / `combineFun` / `sealedCopy`）。詳細は `ksp-core-top-level.md` |
| util | `ksp/util/` | 他プロジェクトでも使える汎用ヘルパのみ（cream 固有型を含まない） |

## Dependency direction (one-way)

```
CreamSymbolProcessor (root)
   ├─▶ feature/<name> ─▶ core/<sub> ─▶ util
   └─▶ ProcessContext (leaf)
feature ─▶ ProcessContext   （唯一の上向き依存。ProcessContext は leaf なので循環しない）
```

| Layer | import してよい | import 禁止 |
|---|---|---|
| `util/`（直下） | Kotlin stdlib のみ | core, feature, top-level, **cream-runtime**, **KSP API**（KSP 依存ヘルパは `util/ksp/` へ）, cream 固有型（`CreamOptions` / `CreamException` / `CopyVisibility` / `GenerateSourceAnnotation` / `TargetValidation` 等） |
| `util/ksp/` | Kotlin stdlib / KSP API（汎用範囲） | core, feature, top-level, **cream-runtime**, cream 固有型（同上） |
| `core/` | util, `cream-ksp:shared`, `cream-runtime`, KSP API | **feature**, `CreamSymbolProcessor` / `Provider`, **`ProcessContext`** |
| `feature/<name>/` | core, util, shared, runtime, KSP API, **`ProcessContext`** | **他の `feature/<name>`（feature 間依存禁止）**, `CreamSymbolProcessor` / `Provider` |
| root (`CreamSymbolProcessor` / `Provider`) | feature, core, util, shared, runtime, KSP API, ProcessContext | 生成ロジック・注釈個別処理 |
| `ProcessContext`（leaf） | KSP API, shared（`CreamOptions`） | feature, core, util, `CreamSymbolProcessor` |

- **唯一の上向き依存は `feature → ProcessContext` のみ**。`core` は `ProcessContext` に依存しない（層別 context を使う）。
- **root パッケージ (`me.tbsten.cream.ksp` 直下) には `CreamSymbolProcessor` / `CreamSymbolProcessorProvider` / `ProcessContext` の 3 ファイルのみ**。生成ロジック・ヘルパ・例外等を直下に置かない（`CreamException` 階層は `:cream-ksp:shared` 側）。
- 境界は [Konsist](https://github.com/LemonAppDev/konsist) の architecture test
  (`cream-ksp/src/test/.../architecture/LayeringArchitectureTest.kt`, issue #130) で自動強制している。
  この表の依存方向を変えたら同テストを更新すること。

## ProcessContext & context parameters

- `-Xcontext-parameters`（Kotlin 2.2.20、要 flag）。`ProcessContext = {resolver, options, codeGenerator, logger}`。**`logger` は必須**（`KSPLogger?` 禁止）。
- **層別 context（必要な capability だけ宣言）**:
  - feature: `context(ctx: ProcessContext) fun processXxx(): List<KSAnnotated>`
  - core: `context(options: CreamOptions, logger: KSPLogger) fun BufferedWriter.appendXxx(...)`（resolver/codeGenerator は受け取らない）
- per-call の値（source/target/omitPackages/funNameTemplate/`GenerateSourceAnnotation` 等）は通常の関数引数のまま。

## Naming

- feature: ファイル `Process<Name>.kt`、関数 `processXxx`（top-level / lowerCamel）。
- core 生成関数: `appendXxx`（`BufferedWriter` 拡張、文字列 append ベース。KotlinPoet 不使用）。
- `GenerateSourceAnnotation`（sealed, `ksp/GenerateSourceAnnotation.kt`, package `me.tbsten.cream.ksp`）: 8 実装で生成元注釈を識別。新注釈追加時は網羅する。

## Cross-cutting rules

- **診断**: ユーザー誤用は `throw` せず `logger.error(message, ksNode)` で clean COMPILATION_ERROR。直後に `return` / `return@forEach` で部分生成を防ぐ。内部想定外のみ例外。
- **`when` は `else` を使わず全分岐を列挙**（sealed/enum 網羅をコンパイラに守らせる）。**unsafe cast `as` を書かない／生成しない**。**`firstOrNull()` > `first()`**、**`mapOf` > `listOf(Pair)`**。
- **SSoT**: 命名ロジックは shared、token const は runtime、options は shared に一元化。
- **ファイル分量** 10〜300 行目安・最大 500（超過は責務分割）。
- **生成**: 空ファイルを作らない（`createNewKotlinFile` の空 buffer スキップ）。`Dependencies(aggregating = true, ...)` を維持。識別子は `escapeKotlinIdentifier()` を通す。生成ファイルには `import me.tbsten.cream.*`。

## Adding a new annotation

1. `cream-runtime` に注釈を定義。
2. `ksp/GenerateSourceAnnotation.kt` の `GenerateSourceAnnotation` sealed interface に実装を追加（sealed なので網羅される）。
3. `feature/<name>/Process<Name>.kt` に `context(ctx) fun processXxx()` を追加。
4. 生成は core（copyFun/combineFun/sealedCopy）を再利用。足りなければ core 側に追加（feature に生成ロジックを置かない）。
5. `CreamSymbolProcessor.process()` に dispatch を追加。
6. `test/`（test data + commonTest）と `cream-ksp` の snapshot/diagnostic test を追加（`ksp-test.md` 準拠）。

> 規約の正本（SSoT）はプロジェクトルートの `CLAUDE.md` と、リファクタ作業中は `.local/brushup/rules.md`。本ファイルは path-scoped な誘導役で、重複定義はしない。`cream-ksp:shared` は別モジュールで本リファクタ対象外（KSP 非依存・`ClassDeclarationInfo` 境界を維持）。
