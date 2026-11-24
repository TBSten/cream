package me.tbsten.cream.test.architecture.library.ksp.core

import me.tbsten.cream.test.architecture.KSP_MAIN
import me.tbsten.cream.test.architecture.support.`1 ファイル 300 行以内`
import me.tbsten.cream.test.architecture.support.`core 層は feature と composition root に依存しない`
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

fun DeclarationContainerScope.coreGeneratorRole() =
    "CoreGenerator".role {
        title = "生成処理"
        summary = "1 種類の関数生成を組み立てる処理"
        description =
            """
            core/<generator>/ に生成処理ごとのディレクトリを切る。
            ディレクトリ名は lowerCamelCase の複合語にする。1 語だけの小文字ディレクトリ（common / error）は共通部品・例外の置き場なので、この Role には入らない。
            generator を足しても定義は変えなくてよい。
            """.trimIndent()
        example("copyFun/Class.kt", "通常クラスへの copy 関数の生成")
        example("combineFun/CombineToClass.kt", "N 個の元から 1 つのクラスへの combine 関数の生成")
        example("sealedCopy/SealedCopy.kt", "@SealedCopy の生成")
        example("parentOptional/ParentOptionalAccessor.kt", "sealed な親への nullable アクセサの生成")

        `1 ファイル 300 行以内`()
        `core 層は feature と composition root に依存しない`()

        layout {
            KSP_MAIN / "core" / "*[A-Z]*" / "*".ktFile()
        }
    }
