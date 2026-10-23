package me.tbsten.cream.test.architecture.creamKsp

import me.tbsten.cream.test.architecture.creamKsp.compositionRoot.compositionRoot
import me.tbsten.cream.test.architecture.creamKsp.core.core
import me.tbsten.cream.test.architecture.creamKsp.feature.feature
import me.tbsten.cream.test.architecture.creamKsp.kspOption.kspOption
import me.tbsten.cream.test.architecture.creamKsp.util.util
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.creamKsp() =
    "creamKsp".group {
        title = "cream-ksp"
        summary = "The KSP processor: three layers (feature → core → util) plus a composition root and its context"
        description =
            """
            `cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/**` は **3 層 + composition root + context** で構成する。
            公開 API（runtime 注釈 / shared options）は変えない `internal` 実装の規約。

            ### Dependency direction (one-way)

            ```
            CreamSymbolProcessor (root)
               ├─▶ feature/<name> ─▶ core/<sub> ─▶ util
               └─▶ ProcessContext (leaf)
            feature ─▶ ProcessContext   （唯一の上向き依存。ProcessContext は leaf なので循環しない）
            ```

            | Layer | import してよい | import 禁止 |
            |---|---|---|
            | `util/`（直下） | Kotlin stdlib のみ | core, feature, top-level, **cream-runtime**, **KSP API**（KSP 依存ヘルパは `util/ksp/` へ）, cream 固有型 |
            | `util/ksp/` | Kotlin stdlib / KSP API（汎用範囲） | core, feature, top-level, **cream-runtime**, cream 固有型 |
            | `core/`（`error` 以外） | util, options, `core/error`, `cream-runtime`, KSP API | **feature**, `CreamSymbolProcessor` / `Provider`, **`ProcessContext`** |
            | `core/error/` | util のみ | core の他サブ, options, feature, top-level, KSP API |
            | `options/` | util, `core/error`（パース失敗の送出）, `cream-runtime` | core の他サブ, feature, top-level, KSP API |
            | `feature/<name>/` | core, options, util, runtime, KSP API, **`ProcessContext`** | **他の `feature/<name>`（feature 間依存禁止）**, `CreamSymbolProcessor` / `Provider` |
            | root（`CreamSymbolProcessor` / `Provider`） | feature, core, options, util, runtime, KSP API, ProcessContext | 生成ロジック・注釈個別処理 |
            | `ProcessContext`（leaf） | KSP API, options（`CreamOptions`） | feature, core, util, `CreamSymbolProcessor` |

            各 Role の `imports from cream / KSP only: ...` / `does not import another feature` 制約がこの表を強制する（`./gradlew :architecture-test:test`）。
            表を変えたら該当 Role の制約も変えること。

            ### ProcessContext & context parameters

            - `-Xcontext-parameters`（Kotlin 2.2.20、要 flag）。
            - **層別 context（必要な capability だけ宣言）**:
              - feature: `context(ctx: ProcessContext) fun processXxx(): List<KSAnnotated>`
              - core: `context(options: CreamOptions, logger: KSPLogger) fun Appendable.appendXxx(...)`（resolver / codeGenerator は受け取らない）
            - per-call の値（source / target / omitPackages / funNameTemplate / `GenerateSourceAnnotation` 等）は通常の関数引数のまま。

            ### Naming

            - feature: ファイル `Process<Name>.kt`、関数 `processXxx`（top-level / lowerCamel）。
            - core 生成関数: `appendXxx`（`Appendable` 拡張、文字列 append ベース。KotlinPoet 不使用）。

            ### Cross-cutting rules

            - **診断**: ユーザー誤用は `throw` せず `logger.error(message, ksNode)` で clean COMPILATION_ERROR。直後に `return` / `return@forEach`
              で部分生成を防ぐ。内部想定外のみ例外。
            - **`when` は `else` を使わず全分岐を列挙**（sealed / enum 網羅をコンパイラに守らせる）。ただし `GenerateSourceAnnotation` は
              api 公開のため意図的に sealed ではない — 注釈ごとの分岐は `when` ではなく interface の overridable member か、独立した関数型を
              引数で渡す形で表現する。**unsafe cast `as` を書かない／生成しない**。**`firstOrNull()` > `first()`**、**`mapOf` > `listOf(Pair)`**。
            - **SSoT**: 命名ロジックは `core/common`、token const は runtime、options は `options/` に一元化。
            - **ファイル分量** 10〜300 行目安・最大 500（超過は責務分割）。各 Role の `stays within ... lines` 制約で強制。
            - **生成**: 空ファイルを作らない（`createNewKotlinFile` の空 buffer スキップ）。`Dependencies(aggregating = true, ...)` を維持。
              識別子は `escapeKotlinIdentifier()` を通す。生成ファイルには `import me.tbsten.cream.*`。
            """.trimIndent()

        compositionRoot()
        feature()
        core()
        kspOption()
        util()
    }
