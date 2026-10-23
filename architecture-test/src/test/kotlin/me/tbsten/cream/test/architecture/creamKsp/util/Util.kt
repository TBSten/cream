package me.tbsten.cream.test.architecture.creamKsp.util

import me.tbsten.cream.test.architecture.creamKsp.util.genericUtil.genericUtil
import me.tbsten.cream.test.architecture.creamKsp.util.kspUtil.kspUtil
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.util() =
    "util".group {
        title = "util layer"
        summary = "他プロジェクトでも使える汎用ヘルパのみ（cream 固有型を含まない）"
        description =
            """
            `util/` も `util/ksp/` も core / feature / options / cream-runtime / cream 固有型（`CreamOptions` / `CreamException` /
            `CopyVisibility` / `GenerateSourceAnnotation` / `TargetValidation` 等）を参照しない。KSP 依存のヘルパは `util/ksp/` へ。
            """.trimIndent()

        genericUtil()
        kspUtil()
    }
