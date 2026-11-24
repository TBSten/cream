package me.tbsten.cream.test.architecture.library.ksp.core

import me.tbsten.cream.test.architecture.KSP_MAIN
import me.tbsten.cream.test.architecture.support.`1 ファイル 300 行以内`
import me.tbsten.cream.test.architecture.support.`core 層は feature と composition root に依存しない`
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

fun DeclarationContainerScope.coreCommonRole() =
    "CoreCommon".role {
        title = "生成の共通部品"
        summary = "各 generator が組み合わせる共通部品"
        example("FindMatchedProperty.kt", "コンストラクタ引数と元プロパティの対応付け")
        example("CopyFunctionName.kt", "生成する関数の命名")
        example("CopyFunctionTypeParameters.kt", "型パラメータの引き継ぎ")
        example("KDoc.kt", "生成する関数の KDoc")
        example("GenerateSourceAnnotation.kt", "生成のきっかけになったアノテーションと、その生成ルール")

        `1 ファイル 300 行以内`()
        `core 層は feature と composition root に依存しない`()

        layout {
            KSP_MAIN / "core" / "common" / "*".ktFile()
        }
    }
