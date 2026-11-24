@file:OptIn(ExperimentalKatachiApi::class)

package me.tbsten.cream.test.architecture.library.ksp

import me.tbsten.cream.test.architecture.KSP_MAIN
import me.tbsten.cream.test.architecture.features
import me.tbsten.cream.test.architecture.support.`1 ファイル 300 行以内`
import me.tbsten.cream.test.architecture.support.featureLayerImports
import me.tbsten.cream.test.architecture.support.featureOf
import me.tbsten.katachi.ExperimentalKatachiApi
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.FileConstraintFailure
import me.tbsten.katachi.dsl.FileConstraintFix
import me.tbsten.katachi.dsl.FileConstraintReason
import me.tbsten.katachi.dsl.RoleScope
import me.tbsten.katachi.dsl.kotlin.ktFile
import me.tbsten.katachi.konsist.declarations.ktFileContent
import java.io.File

fun DeclarationContainerScope.featureProcessRole() =
    "FeatureProcess".role {
        title = "アノテーションごとの処理の入口"
        summary = "1 つのアノテーションを見つけて検証し、core の生成処理を呼ぶ入口（Process<Name>.kt）"
        description =
            """
            feature/<アノテーション名>/Process<Name>.kt に置く。置けるのは Features.kt の features にあるアノテーション名だけ（<Name> はアノテーション名の先頭を大文字にしたもの）。
            公開するのは internal fun process<Name>(): List<KSAnnotated> の 1 つだけで、ほかはすべて private にする。
            生成ロジックは core/ に置き、ここには発見・検証・core 呼び出しだけを書く。ほかの feature と CreamSymbolProcessor / Provider は import できない（ProcessContext は可）。
            """.trimIndent()
        example("ProcessCopyTo.kt", "@CopyTo の処理入口")

        `1 ファイル 300 行以内`()
        `入口の関数 1 つと private な補助だけを置く`()
        `入口は context(ProcessContext) を宣言する`()

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
        val feature = featureOf(fileName)

        `package`("me.tbsten.cream.ksp.feature.$feature")

        fileAnnotation("*")
        featureLayerImports(feature)

        required(internal, `fun`, "process$name")().returns("List<KSAnnotated>")
        allow(private, anythingElse)
    }

private fun RoleScope.`入口は context(ProcessContext) を宣言する`() =
    fileConstraint("入口は context(ProcessContext) を宣言する") { subject ->
        subject.files.mapNotNull { file ->
            val name = file.substringAfterLast('/').removeSuffix(".kt").removePrefix("Process")
            val text = File("${subject.projectRoot}/$file").readText()
            val declared = Regex("""context\([^)]*:\s*ProcessContext\)\s*internal fun process$name\(""").containsMatchIn(text)
            if (declared) {
                null
            } else {
                FileConstraintFailure(
                    file = file,
                    reasons = listOf(FileConstraintReason("Missing", "context(processContext: ProcessContext) internal fun process$name()")),
                    howToFix = listOf(FileConstraintFix("入口の関数に context(processContext: ProcessContext) を付ける")),
                )
            }
        }
    }
