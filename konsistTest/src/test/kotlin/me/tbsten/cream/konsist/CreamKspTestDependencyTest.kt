package me.tbsten.cream.konsist

import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import me.tbsten.cream.konsist.manifest.FEATURES
import me.tbsten.cream.konsist.support.ProjectScope

/**
 * cream-ksp/src/test の依存方向。正本は `.claude/rules/ksp-test.md` のレイアウト規約で、
 * そこから根拠づけられる 2 点だけを検査する:
 *
 * 1. **feature テスト間の独立**: `feature/<name>/`（scenario 含む）は他 feature のパッケージを
 *    import しない。feature 横断のシナリオは root 直下（MultipleDiagnosticsTest 等）に置く規約
 * 2. **テスト基盤の feature 非依存**: `testing/` は feature パッケージを import しない
 *    （ksp-test.md「testing/ … テスト基盤（feature 非依存）」）
 *
 * main と違いテスト側にはレイヤ別の依存方向テーブルが（散文にも）存在しないため、
 * import allow-list までは導入しない。配置・宣言の形は [CreamKspTestManifestTest] が受け持つ。
 * test source set は main と同じパッケージ名空間を使うので、ファイル集合は Konsist の
 * module / sourceSet メタデータで test 側に限定する。
 */
internal class CreamKspTestDependencyTest :
    FreeSpec({
        val testFiles =
            ProjectScope.projectFiles.filter { it.moduleName == "cream-ksp" && it.sourceSetName == "test" }

        "scope に cream-ksp/test のファイルが存在する（空スコープで無言 PASS しない）" {
            withClue("cream-ksp/test が scope から消えている（scope 設定の誤り）") {
                testFiles.shouldNotBeEmpty()
            }
        }

        "feature テストは他 feature のパッケージを import しない（feature 単位の独立）" {
            val offenders =
                FEATURES.flatMap { feature ->
                    val otherFeatures = FEATURES - feature
                    testFiles
                        .filter { it.inPackageTree("$FEATURE_PACKAGE.$feature") }
                        .flatMap { file ->
                            file.importDescriptions { imported ->
                                otherFeatures.any { other -> imported.startsWith("$FEATURE_PACKAGE.$other.") }
                            }
                        }
                }
            withClue(
                offenders.joinToString(
                    separator = "\n",
                    prefix = "他 feature のパッケージへの import:\n",
                    postfix = "\n$TEST_DEPENDENCY_GUIDANCE",
                ) { "- $it" },
            ) {
                offenders.shouldBeEmpty()
            }
        }

        "testing/ は feature パッケージを import しない（テスト基盤は feature 非依存）" {
            val offenders =
                testFiles
                    .filter { it.inPackageTree(TESTING_PACKAGE) }
                    .flatMap { file -> file.importDescriptions { it.startsWith("$FEATURE_PACKAGE.") } }
            withClue(
                offenders.joinToString(
                    separator = "\n",
                    prefix = "テスト基盤から feature パッケージへの import:\n",
                    postfix = "\n$TEST_DEPENDENCY_GUIDANCE",
                ) { "- $it" },
            ) {
                offenders.shouldBeEmpty()
            }
        }
    })

private const val KSP_ROOT = "me.tbsten.cream.ksp"
private const val FEATURE_PACKAGE = "$KSP_ROOT.feature"
private const val TESTING_PACKAGE = "$KSP_ROOT.testing"

/** 失敗メッセージの末尾に必ず添える、読み手（主に AI agent）向けの導線。 */
private val TEST_DEPENDENCY_GUIDANCE =
    """
    テストの依存方向の正本は .claude/rules/ksp-test.md のレイアウト規約で、検査は
    konsistTest/src/test/kotlin/me/tbsten/cream/konsist/CreamKspTestDependencyTest.kt に定義されている。
    feature 横断のシナリオは root 直下（MultipleDiagnosticsTest 等）へ、共有 fixture は
    testing/ へ置く。例外を作りたい場合は自分でこの spec を書き換えるのではなく、人間に更新を依頼すること。
    """.trimIndent()

/** このファイルの package が [packageName] そのもの、またはその部分パッケージにあるか。 */
private fun KoFileDeclaration.inPackageTree(packageName: String): Boolean {
    val actual = packagee?.name ?: return false
    return actual == packageName || actual.startsWith("$packageName.")
}

/** [isDisallowed] に一致する import を「ファイル: import 名」の形で列挙する（違反メッセージ用）。 */
private fun KoFileDeclaration.importDescriptions(isDisallowed: (String) -> Boolean): List<String> =
    imports
        .filter { isDisallowed(it.name) }
        .map { "$projectPath: import ${it.name}" }
