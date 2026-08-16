package me.tbsten.cream.konsist

import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.provider.KoNameProvider
import com.lemonappdev.konsist.api.provider.modifier.KoVisibilityModifierProvider
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import me.tbsten.cream.konsist.dsl.DEFAULT_MAX_LINES
import me.tbsten.cream.konsist.dsl.FileVerdict
import me.tbsten.cream.konsist.dsl.SourceSetManifest
import me.tbsten.cream.konsist.dsl.evaluate
import me.tbsten.cream.konsist.dsl.normalizedProjectPath
import me.tbsten.cream.konsist.manifest.creamKspManifest
import me.tbsten.cream.konsist.support.ProjectScope

/**
 * [creamKspManifest]（cream-ksp/src/main の目録）の検証 spec。deny by default は配置と中身の
 * 両方に効く: manifest に列挙されていない .kt も、列挙されていないトップレベル宣言も違反
 * （「manifest に書いてないことは書けない」）。
 *
 * manifest driven development の第 1 弾（PR ①）。cream-ksp 側の既存 3 spec
 * （AllKotlinFilesTest / feature.ArchTest / core.ArchTest）はまだ廃止せず併存させる —
 * 両方 green であることが「manifest が既存規約を再現できている」ことの等価性検証になる。
 *
 * 検証ルールは Konsist の `assertTrue` / `assertFalse` / `assertNotEmpty` を使わず、
 * Konsist API のクエリ + kotest matcher で直接書く（Konsist の assert 終端は
 * `@Suppress("konsist.…")` を尊重して宣言を検査から除外してしまうため — OPEN-QUESTIONS Q22）。
 */
internal class CreamKspManifestTest :
    FreeSpec({
        val verdicts = creamKspManifest.evaluate(ProjectScope.projectFiles)

        verdicts.forEach { verdict ->
            val sourceSet = verdict.sourceSet
            "${sourceSet.modulePath} / ${sourceSet.sourceSetName}" - {

                "網羅性: 管轄区域の全ファイルが manifest の検査対象になっている（空リストで無言 PASS しない）" {
                    // manifest 側はパス接頭辞、こちらは Konsist の module / sourceSet メタデータという
                    // 別ルートで数えて突き合わせる。0 件や取りこぼしがあればここで落ちる。
                    val expectedModuleDir = sourceSet.modulePath.removePrefix(":").replace(':', '/')
                    val scopeFiles =
                        ProjectScope.projectFiles.filter {
                            it.moduleName == expectedModuleDir && it.sourceSetName == sourceSet.sourceSetName
                        }
                    withClue("scope 実測 ${scopeFiles.size} ファイル / manifest が検査 ${verdict.files.size} ファイル") {
                        verdict.files.shouldNotBeEmpty()
                        verdict.files shouldHaveSize scopeFiles.size
                    }
                }

                "requiredFile がすべて存在する" {
                    withClue(
                        verdict.missingRequiredFiles.joinToString(
                            separator = "\n",
                            prefix = "manifest が必須と宣言しているファイルが存在しない:\n",
                            postfix = "\n$MANIFEST_GUIDANCE",
                        ) { "- $it" },
                    ) {
                        verdict.missingRequiredFiles.shouldBeEmpty()
                    }
                }

                "package がディレクトリ構成と一致する（パス基準の manifest とパッケージ基準の Layer 検査を繋ぐ前提）" {
                    val sourceRoot = sourceSet.rootPrefix + "kotlin/"
                    val mismatches =
                        verdict.files.mapNotNull { fileVerdict ->
                            val file = fileVerdict.file
                            val relative = file.normalizedProjectPath.removePrefix(sourceRoot)
                            val expected = relative.substringBeforeLast('/', missingDelimiterValue = "").replace('/', '.')
                            val actual = file.packagee?.name.orEmpty()
                            if (actual != expected) "${file.projectPath}: package $actual ≠ ディレクトリ $expected" else null
                        }
                    withClue(mismatches.joinToString("\n")) { mismatches.shouldBeEmpty() }
                }

                "配置（deny by default）" - {
                    verdict.files.groupBy { dirKeyOf(sourceSet, it.file) }.toSortedMap().forEach { (dirKey, files) ->
                        "$dirKey: manifest に列挙されていないファイルを置かない" {
                            val violations =
                                files
                                    .filterIsInstance<FileVerdict.Unmatched>()
                                    .map { "${it.file.projectPath}（${it.deniedAt} 配下で deny by default）" }
                            withClue(
                                violations.joinToString(
                                    separator = "\n",
                                    prefix = "manifest のどのエントリにも一致しないファイル:\n",
                                    postfix = "\n$MANIFEST_GUIDANCE",
                                ) { "- $it" },
                            ) {
                                violations.shouldBeEmpty()
                            }
                        }
                    }
                }

                "行数上限（既定 $DEFAULT_MAX_LINES 行 / 例外は manifest の file エントリの maxLines）" - {
                    verdict.matched.groupBy { dirKeyOf(sourceSet, it.file) }.toSortedMap().forEach { (dirKey, files) ->
                        "$dirKey: 全ファイルが上限以内" {
                            val over =
                                files
                                    .filter {
                                        it.file.text
                                            .lines()
                                            .size > it.maxLines
                                    }.map { "${it.file.projectPath}: ${it.file.text.lines().size} 行 > 上限 ${it.maxLines} 行" }
                            withClue(
                                over.joinToString(
                                    separator = "\n",
                                    postfix = "\n責務を分割するか、分割できない理由があるなら人間に manifest の maxLines 変更を依頼すること。\n$MANIFEST_GUIDANCE",
                                ) { "- $it" },
                            ) {
                                over.shouldBeEmpty()
                            }
                        }
                    }
                }

                "中身（grant 意味論: manifest に列挙されていないトップレベル宣言は違反）" - {
                    verdict.matched
                        .filter { it.content != null }
                        .sortedBy { it.file.normalizedProjectPath }
                        .forEach { matched ->
                            val testName = matched.file.normalizedProjectPath.removePrefix(sourceSet.rootPrefix)
                            testName {
                                val violations = matched.content?.findViolations(matched.file).orEmpty()
                                withClue(
                                    violations.joinToString(
                                        separator = "\n",
                                        prefix = "${matched.file.projectPath}: manifest の grant と実態のトップレベル宣言が一致しない:\n",
                                        postfix = "\n$MANIFEST_GUIDANCE",
                                    ) { "- $it" },
                                ) {
                                    violations.shouldBeEmpty()
                                }
                            }
                        }
                }
            }
        }

        "cream-ksp/main の public なトップレベル宣言は CreamSymbolProcessorProvider だけ" {
            // explicitApi() 下の実測。エントリポイント以外の公開 API を生やさない。
            val mainFiles =
                ProjectScope.projectFiles.filter { it.moduleName == "cream-ksp" && it.sourceSetName == "main" }
            val publicDeclarationNames =
                mainFiles
                    .flatMap { file -> file.declarations(includeNested = false, includeLocal = false) }
                    .filter { it is KoVisibilityModifierProvider && it.hasPublicOrDefaultModifier }
                    .map { if (it is KoNameProvider) it.name else it.toString() }
            withClue("public / 修飾なしのトップレベル宣言: $publicDeclarationNames") {
                publicDeclarationNames shouldBe listOf("CreamSymbolProcessorProvider")
            }
        }
    })

/** 失敗メッセージの末尾に必ず添える、読み手（主に AI agent）向けの導線。 */
private val MANIFEST_GUIDANCE =
    """
    許可されるファイルとトップレベル宣言の目録（manifest）は
    konsistTest/src/test/kotlin/me/tbsten/cream/konsist/manifest/CreamKspManifest.kt に定義されている。
    manifest は人間だけが編集できる。ファイルを追加・移動・リネームしたい場合も、
    トップレベル宣言を追加・削除・改名したい場合も、
    自分で manifest を書き換えるのではなく、人間に manifest の更新を依頼すること。
    """.trimIndent()

/** テストケースを局所化するためのグルーピングキー = 管轄区域ルートからの相対ディレクトリ。 */
private fun dirKeyOf(
    sourceSet: SourceSetManifest,
    file: KoFileDeclaration,
): String =
    file.normalizedProjectPath
        .removePrefix(sourceSet.rootPrefix)
        .substringBeforeLast('/', missingDelimiterValue = "(root)")
