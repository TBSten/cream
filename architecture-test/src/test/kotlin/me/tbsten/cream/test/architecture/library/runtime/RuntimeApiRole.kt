@file:OptIn(ExperimentalKatachiApi::class)

package me.tbsten.cream.test.architecture.library.runtime

import me.tbsten.cream.test.architecture.RUNTIME_MAIN
import me.tbsten.katachi.ExperimentalKatachiApi
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.RoleScope
import me.tbsten.katachi.dsl.kotlin.ktFile
import me.tbsten.katachi.konsist.declarations.ktFileContent

fun DeclarationContainerScope.runtimeApiRole() =
    "RuntimeApi".role {
        title = "公開 API"
        summary = "利用者が書く公開アノテーションと公開トークン定数（commonMain のみ）"
        description =
            """
            置けるのは、ファイル名と同じ名前の public なアノテーション（入れ子のアノテーションも可）、そのオプションに使う public な enum class、
            または public const val の String 定数だけ。public な要素にはすべて KDoc を書く。
            """.trimIndent()
        example("CopyTo.kt", "@CopyTo アノテーション")
        example("CopyFunctionNameToken.kt", "funName テンプレートのトークン定数")

        `KDoc 付きの公開アノテーションとトークン定数だけを置く`()

        layout {
            RUNTIME_MAIN / "*".ktFile()
        }
    }

private fun RoleScope.`KDoc 付きの公開アノテーションとトークン定数だけを置く`() =
    "KDoc 付きの公開アノテーションとトークン定数だけを置く".ktFileContent {
        `package`("me.tbsten.cream")

        `import`("kotlin.reflect.KClass")
        fileAnnotation("Suppress")

        allow(public, annotation, `class`, fileName) {
            allow(`val`, "*")
            allow(public, annotation, `class`, "*") {
                allow(`val`, "*")
            }.kdoc()
        }.kdoc()
        allow(public, enum, `class`, "*") {
            allow(entry, "*").kdoc()
        }.kdoc()
        allow(public, const, `val`, "*").type("String").kdoc()
    }
