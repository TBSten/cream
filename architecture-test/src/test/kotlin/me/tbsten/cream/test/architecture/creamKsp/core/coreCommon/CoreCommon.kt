package me.tbsten.cream.test.architecture.creamKsp.core.coreCommon

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.dependsOnlyOn
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.cream.test.architecture.support.staysWithinLineBudget
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.coreCommon() =
    "CoreCommon" {
        title = "core/common"
        summary = "全生成系で共有する部品: 型パラメータ・sealed 型レンダリング・プロパティマッチング・KDoc・命名・target 検証・visibility・診断ヘルパ"
        description =
            """
            - 型パラメータ（header / where 描画）、sealed 型レンダリング（receiver / `is` 分岐型、`SealedTypeRendering.kt`。sealedCopy / parentOptional が共有）
            - プロパティマッチング、KDoc 生成、命名（`CopyFunctionName.kt` / `FunctionNameTemplate.kt`）、identifier escape の呼び出し
            - `GenerateSourceAnnotation` と、その 10 実装 `<Name>SourceAnnotation`（`CopySourceAnnotation.kt` / `CombineSourceAnnotation.kt` /
              `MappingSourceAnnotation.kt` / `ParentOptionalSourceAnnotation.kt` に family 別）。api 公開のため **意図的に sealed ではない** —
              注釈ごとの差分は `when` ではなく overridable member（安全な既定値つき）で表す
            - パラメータ単位の挙動 `FindMappedSourceProperty` / `IsExcluded` は interface のメンバではなく独立した関数型。
              core の `appendXxx` はこれらを**通常の引数**で受け取り、GSA からは読まない（注釈以外から生成を駆動できるようにするため）
            """.trimIndent()
        example("GenerateSourceAnnotation")
        example("FindMatchedProperty")
        layout {
            KSP_MAIN_DIR / "core/common" / "*".ktFile()
        }
        packageFollowsDirectory()
        staysWithinLineBudget()
        dependsOnlyOn("core.", "options.", "util.", runtime = true, kspApi = true)
    }
