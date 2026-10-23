package me.tbsten.cream.test.architecture.creamKsp.core

import me.tbsten.cream.test.architecture.creamKsp.core.combineFunGenerator.combineFunGenerator
import me.tbsten.cream.test.architecture.creamKsp.core.copyFunGenerator.copyFunGenerator
import me.tbsten.cream.test.architecture.creamKsp.core.coreCommon.coreCommon
import me.tbsten.cream.test.architecture.creamKsp.core.creamException.creamException
import me.tbsten.cream.test.architecture.creamKsp.core.parentOptionalGenerator.parentOptionalGenerator
import me.tbsten.cream.test.architecture.creamKsp.core.sealedCopyGenerator.sealedCopyGenerator
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.core() =
    "core".group {
        title = "core layer"
        summary = "cream-specific code generation, split into fixed sub-packages. Nothing lives directly in `core/`"
        description =
            """
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
            """.trimIndent()

        coreCommon()
        copyFunGenerator()
        combineFunGenerator()
        sealedCopyGenerator()
        parentOptionalGenerator()
        creamException()
    }
