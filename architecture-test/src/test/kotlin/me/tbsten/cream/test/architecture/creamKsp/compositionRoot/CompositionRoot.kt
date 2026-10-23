package me.tbsten.cream.test.architecture.creamKsp.compositionRoot

import me.tbsten.cream.test.architecture.creamKsp.compositionRoot.processContext.processContext
import me.tbsten.cream.test.architecture.creamKsp.compositionRoot.serviceRegistration.serviceRegistration
import me.tbsten.cream.test.architecture.creamKsp.compositionRoot.symbolProcessor.symbolProcessor
import me.tbsten.cream.test.architecture.creamKsp.compositionRoot.symbolProcessorProvider.symbolProcessorProvider
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.compositionRoot() =
    "compositionRoot".group {
        title = "Composition root"
        summary = "The KSP entry point and the context it hands to every feature: the only files directly in `me.tbsten.cream.ksp`"
        description =
            """
            `ksp/` 直下は **オーケストレーション + 横断型のみ**。生成ロジックは置かない。
            置いてよいのはこの group の 3 ファイルだけで、それ以外は deny by default で `[UnexpectedFile]` になる。

            ❌ いけないこと:
            - 生成ロジックを書く（→ `core/`）
            - 注釈ごとの処理を書く（→ `feature/<name>/`）
            - 汎用ヘルパを書く（→ `util/`）
            - 例外を書く（→ `core/error/`。`CreamException` 階層はそこにある）
            - KSP オプションのモデル・パースを書く（→ `options/`）
            - `ProcessContext.kt` から feature / core / util を import する

            > 補足: `GenerateSourceAnnotation` は core が生成時に使う型なので `core/common/` に置く（直下ではない）。
            """.trimIndent()

        symbolProcessor()
        symbolProcessorProvider()
        processContext()
        serviceRegistration()
    }
