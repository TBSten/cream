package me.tbsten.cream.test.architecture.creamKsp.feature

import me.tbsten.cream.test.architecture.creamKsp.feature.featureDiagnostics.featureDiagnostics
import me.tbsten.cream.test.architecture.creamKsp.feature.featureEntryPoint.featureEntryPoint
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.feature() =
    "feature".group {
        title = "feature layer"
        summary = "One directory per annotation: discover → extract arguments → validate → call core. No generation logic"
        description =
            """
            ❌ **`feature/` 直下に `.kt` を置かない**。必ず `feature/<name>/` サブディレクトリに置く（1 注釈 = 1 ディレクトリ）。

            ✅ feature がやること:
            - `resolver.getSymbolsWithAnnotation(...)` で発見 → `validate()` で partition。
            - 注釈引数の抽出（targets / sources、KDoc、visibility、funName template 等）と検証。
            - core の生成関数を呼び、`createNewKotlinFile` で書き出し。
            - 未解決シンボル（invalid）を返す（KSP の次ラウンド用）。

            ❌ feature がやってはいけないこと:
            - 生成コードの組み立て（型 param / where / プロパティマッチ / 本体）を書く（→ `core/`）。
            - 他の `feature/<name>` を import する（feature 間依存禁止）。
            - composition root（`CreamSymbolProcessor` / `Provider`）を import する（上向きに依存してよいのは `ProcessContext` のみ）。
            - 注釈固有でない汎用処理を書く（→ `core/common` か `util/`）。
            """.trimIndent()

        featureEntryPoint()
        featureDiagnostics()
    }
