@file:OptIn(ExperimentalKatachiApi::class)

package me.tbsten.cream.test.architecture.library.ksp

import me.tbsten.cream.test.architecture.KSP_MAIN
import me.tbsten.cream.test.architecture.features
import me.tbsten.katachi.ExperimentalKatachiApi
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.RoleScope
import me.tbsten.katachi.dsl.kotlin.ktFile
import me.tbsten.katachi.konsist.declarations.ktFileContent

fun DeclarationContainerScope.featureProcessRole() =
    "FeatureProcess".role {
        title = "アノテーションごとの処理の入口"
        summary = "1 つのアノテーションを見つけて検証し、core の生成処理を呼ぶ入口（Process<Name>.kt）"
        description =
            """
            feature/<アノテーション名>/Process<Name>.kt に置く。置けるのは Features.kt の features にあるアノテーション名だけ（<Name> はアノテーション名の先頭を大文字にしたもの）。
            公開するのは internal fun process<Name>(): List<KSAnnotated> の 1 つだけで、ほかはすべて private にする。
            生成ロジックは core/ に置き、ここには発見・検証・core 呼び出しだけを書く。
            """.trimIndent()
        example("ProcessCopyTo.kt", "@CopyTo の処理入口")

        `入口の関数 1 つと private な補助だけを置く`()

        layout {
            KSP_MAIN {
                "feature" {
                    features.forEach { feature ->
                        val name = feature.replaceFirstChar { it.uppercase() }
                        feature / "Process$name".ktFile()
                    }
                }
            }
        }
    }

private fun RoleScope.`入口の関数 1 つと private な補助だけを置く`() =
    "入口の関数 1 つと private な補助だけを置く".ktFileContent {
        val name = fileName.removePrefix("Process")
        val feature = name.replaceFirstChar { it.lowercase() }

        `package`("me.tbsten.cream.ksp.feature.$feature")

        `import`("com.google.devtools.ksp.**")
        `import`("me.tbsten.cream.*")
        `import`("me.tbsten.cream.ksp.ProcessContext")
        `import`("me.tbsten.cream.ksp.core.**")
        `import`("me.tbsten.cream.ksp.options.**")
        `import`("me.tbsten.cream.ksp.util.**")
        `import`("me.tbsten.cream.ksp.feature.$feature.**")

        required(internal, `fun`, "process$name")().returns("List<KSAnnotated>")
        allow(private, anythingElse)
    }
