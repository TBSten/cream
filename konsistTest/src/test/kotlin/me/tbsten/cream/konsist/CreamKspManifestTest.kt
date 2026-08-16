package me.tbsten.cream.konsist

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import me.tbsten.cream.konsist.dsl.AssertResult
import me.tbsten.cream.konsist.dsl.normalizedProjectPath
import me.tbsten.cream.konsist.manifest.creamKspManifest
import me.tbsten.cream.konsist.support.ProjectScope

/**
 * [creamKspManifest] の検証 spec。ファイル 1 つ = テストケース 1 つで、管轄区域の全ファイルが
 * 必ず [me.tbsten.cream.konsist.dsl.Manifest.assert] を通る（素通りの経路がない）。仕組みの大筋は dsl 側:
 *
 * @see me.tbsten.cream.konsist.dsl.manifest DSL 意味論
 * @see me.tbsten.cream.konsist.dsl.ManifestAssertionRule rule の代数
 */
internal class CreamKspManifestTest :
    FreeSpec({
        val files = creamKspManifest.scopeFiles(ProjectScope.projectFiles).sortedBy { it.normalizedProjectPath }

        "管轄区域が空でない（scope 設定の誤りで無言 PASS しない）" {
            files.shouldNotBeEmpty()
        }

        // 存在しないファイルは per-file のループに現れないため、これだけ集合レベルで検査する
        "requiredFile がすべて存在する" {
            val present = files.map { it.normalizedProjectPath }.toSet()
            val missing = creamKspManifest.requiredPaths.filterNot { it in present }
            withClue(
                missing.joinToString(
                    separator = "\n",
                    prefix = "manifest が必須と宣言しているファイルが存在しない:\n",
                    postfix = MANIFEST_GUIDANCE,
                ) { "- $it" },
            ) {
                missing.shouldBeEmpty()
            }
        }

        files.forEach { file ->
            file.normalizedProjectPath {
                when (val result = creamKspManifest.assert(file)) {
                    AssertResult.Ok -> Unit

                    is AssertResult.Failure ->
                        withClue(
                            result.because.joinToString(
                                separator = "\n",
                                prefix = "${file.projectPath}:\n",
                                postfix = MANIFEST_GUIDANCE,
                            ) { "- $it" },
                        ) {
                            result.because.shouldBeEmpty()
                        }
                }
            }
        }
    })

/** 失敗メッセージの末尾に必ず添える、読み手（主に AI agent）向けの導線。 */
private val MANIFEST_GUIDANCE =
    """
    Guidance ----
     | 許可されるファイルとトップレベル宣言の目録（manifest）は
     | konsistTest/src/test/kotlin/me/tbsten/cream/konsist/manifest/CreamKspManifest.kt に定義されている。
     | manifest は人間だけが編集できる。ファイルを追加・移動・リネームしたい場合も、
     | トップレベル宣言を追加・削除・改名したい場合も、
     | 自分で manifest を書き換えるのではなく、人間に manifest の更新を依頼すること。
     +---
    """.trimIndent()
