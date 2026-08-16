package me.tbsten.cream.konsist

import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import me.tbsten.cream.konsist.support.ProjectScope

/**
 * cream-ksp/src/main の依存方向。正本は `.claude/rules/ksp-architecture.md` の依存方向テーブル。
 *
 * - 全レイヤを **kotest + import allow-list（deny by default）**で検査する。レイヤごとに
 *   「import してよい依存先」を列挙し、`me.tbsten.cream` 配下の import は必ずどれかに一致
 *   しなければ違反（列挙外 = deny）。Konsist の `Layer` + `assertArchitecture` は使わない:
 *   Konsist 0.17.x の `dependsOn` は「宣言していないレイヤへの依存」を禁止せず
 *   （deny が効くのは `dependsOnNothing()` だけ）、allow-list に見えて **allow by default**
 *   だったため（feature 間 import も core サブ間 import も素通りすることを反例で実証済み）
 * - 比較は完全一致でなく prefix（package tree）基準。ワイルドカード import
 *   （`me.tbsten.cream.ksp.*` 等）も「許可 prefix に一致しない import」として弾かれる
 * - **検査の死角**: import 文だけを見るため、import を伴わない FQN 参照
 *   （`me.tbsten.cream.ksp.ProcessContext` を式の中に直接書く形）と同一パッケージ内の依存は
 *   検出できない（Q19 の帰結: 関数本体は宣言レベルの検査対象外で、text 正規表現も使わない）。
 *   ワイルドカード import 自体は ktlint（no-wildcard-imports）も別途禁止している
 * - 配置・宣言の形・行数上限は [CreamKspManifestTest]（manifest）が受け持つ。root 型
 *   （Processor / Provider）や ProcessContext を import してよい場所の制限も、以前の個別
 *   deny-list ではなく各レイヤの allow-list（に載っていないこと）で表現される
 */
internal class CreamKspDependencyTest :
    FreeSpec({
        val mainFiles =
            ProjectScope.projectFiles.filter { it.moduleName == "cream-ksp" && it.sourceSetName == "main" }

        "scope に cream-ksp/main のファイルが存在する（空スコープで無言 PASS しない）" {
            withClue("cream-ksp/main が scope から消えている（scope 設定の誤り）") {
                mainFiles.shouldNotBeEmpty()
            }
        }

        "レイヤ別 import allow-list（deny by default）" - {
            "全ファイルがちょうど 1 つのレイヤに分類される（分類漏れ・重複で無言 PASS しない）" {
                val unassigned = mainFiles.filter { file -> LAYER_POLICIES.none { it.appliesTo(file) } }
                withClue(unassigned.joinToString("\n") { "${it.projectPath}: どのレイヤにも分類されない" }) {
                    unassigned.shouldBeEmpty()
                }
                val overlapping = mainFiles.filter { file -> LAYER_POLICIES.count { it.appliesTo(file) } > 1 }
                withClue(overlapping.joinToString("\n") { "${it.projectPath}: 複数レイヤに分類されている" }) {
                    overlapping.shouldBeEmpty()
                }
            }

            LAYER_POLICIES.forEach { policy ->
                "${policy.layerName}: me.tbsten.cream 配下の import は許可リストのみ（${policy.allowedDescription()}）" {
                    val files = mainFiles.filter(policy.appliesTo)
                    withClue("${policy.layerName} のファイルが scope にない（レイヤ定義かディレクトリ構成の誤り）") {
                        files.shouldNotBeEmpty()
                    }
                    val offenders =
                        files.flatMap { file ->
                            file.imports
                                .map { it.name }
                                .filter { it.inPackageTree(CREAM_ROOT) }
                                .filterNot { imported -> policy.allowedTargets.any { it.matches(imported) } }
                                .map { "${file.projectPath}: import $it" }
                        }
                    withClue(
                        offenders.joinToString(
                            separator = "\n",
                            prefix = "${policy.layerName} レイヤの許可リスト（${policy.allowedDescription()}）に一致しない import:\n",
                            postfix = "\n$DEPENDENCY_GUIDANCE",
                        ) { "- $it" },
                    ) {
                        offenders.shouldBeEmpty()
                    }
                }
            }
        }

        "レイヤ外の依存先（KSP API）" - {
            "ProcessContext は leaf: KSP API と options 以外を import しない" {
                val processContextFiles =
                    mainFiles.filter { it.packagee?.name == KSP_ROOT && it.nameWithExtension == "ProcessContext.kt" }
                withClue("ProcessContext.kt が見つからない（配置は CreamKspManifestTest が検査する）") {
                    processContextFiles.shouldNotBeEmpty()
                }
                val offenders =
                    processContextFiles.flatMap { file ->
                        file.importDescriptions {
                            !it.startsWith("$KSP_API_PACKAGE.") && !it.startsWith("$OPTIONS_PACKAGE.")
                        }
                    }
                withClue(offenders.joinToString("\n", postfix = "\n$DEPENDENCY_GUIDANCE")) { offenders.shouldBeEmpty() }
            }

            "util 直下（util.ksp を除く）は KSP API を import しない（KSP 依存ヘルパは util/ksp へ）" {
                val offenders =
                    mainFiles
                        .filter { it.packagee?.name == UTIL_PACKAGE }
                        .flatMap { file -> file.importDescriptions { it.startsWith("$KSP_API_PACKAGE.") } }
                withClue(offenders.joinToString("\n", postfix = "\n$DEPENDENCY_GUIDANCE")) { offenders.shouldBeEmpty() }
            }

            "core/error は KSP API を import しない（util のみに依存する葉）" {
                val offenders =
                    mainFiles
                        .filter { it.inPackageTree("$CORE_PACKAGE.error") }
                        .flatMap { file -> file.importDescriptions { it.startsWith("$KSP_API_PACKAGE.") } }
                withClue(offenders.joinToString("\n", postfix = "\n$DEPENDENCY_GUIDANCE")) { offenders.shouldBeEmpty() }
            }

            "options は KSP API を import しない（KSP 非依存の横断モデル）" {
                val offenders =
                    mainFiles
                        .filter { it.inPackageTree(OPTIONS_PACKAGE) }
                        .flatMap { file -> file.importDescriptions { it.startsWith("$KSP_API_PACKAGE.") } }
                withClue(offenders.joinToString("\n", postfix = "\n$DEPENDENCY_GUIDANCE")) { offenders.shouldBeEmpty() }
            }
        }
    })

private const val CREAM_ROOT = "me.tbsten.cream"
private const val KSP_ROOT = "$CREAM_ROOT.ksp"
private const val FEATURE_PACKAGE = "$KSP_ROOT.feature"
private const val CORE_PACKAGE = "$KSP_ROOT.core"
private const val OPTIONS_PACKAGE = "$KSP_ROOT.options"
private const val UTIL_PACKAGE = "$KSP_ROOT.util"
private const val KSP_API_PACKAGE = "com.google.devtools.ksp"

/** `common` / `error` を除く core のサブパッケージ（生成ロジック側。common に依存し、相互には依存しない）。 */
private val CORE_GENERATOR_SUBPACKAGES = listOf("copyFun", "combineFun", "sealedCopy", "parentOptional")

/** 失敗メッセージの末尾に必ず添える、読み手（主に AI agent）向けの導線。 */
private val DEPENDENCY_GUIDANCE =
    """
    依存方向の正本は .claude/rules/ksp-architecture.md の依存方向テーブルで、レイヤごとの許可リストは
    konsistTest/src/test/kotlin/me/tbsten/cream/konsist/CreamKspDependencyTest.kt に定義されている。
    依存を増やしたい場合は、自分で許可リストを書き換えるのではなく、人間に更新を依頼すること。
    """.trimIndent()

/** import の許可先 1 件。[matches] が true なら許可。 */
private class ImportTarget(
    val description: String,
    val matches: (String) -> Boolean,
)

/** [packageName] とその配下すべてを許可する（ワイルドカード import の bare 形式 `pkg` にも一致する）。 */
private fun packageTarget(packageName: String) = ImportTarget("$packageName..") { it.inPackageTree(packageName) }

/** 完全一致の型 1 つだけを許可する（ワイルドカード import は一致しない）。 */
private fun typeTarget(fullName: String) = ImportTarget(fullName) { it == fullName }

/** cream-runtime（`me.tbsten.cream` 直下の注釈と token const）。`me.tbsten.cream.ksp` 配下は含まない。 */
private val creamRuntimeTarget =
    ImportTarget("cream-runtime ($CREAM_ROOT 直下)") { it.inPackageTree(CREAM_ROOT) && !it.inPackageTree(KSP_ROOT) }

/**
 * 1 レイヤの import 規則: [appliesTo] なファイルの `me.tbsten.cream` 配下 import は
 * [allowedTargets] のどれかに一致しなければ違反。
 */
private class LayerImportPolicy(
    val layerName: String,
    val appliesTo: (KoFileDeclaration) -> Boolean,
    val allowedTargets: List<ImportTarget>,
) {
    fun allowedDescription(): String = if (allowedTargets.isEmpty()) "許可なし" else allowedTargets.joinToString(" / ") { it.description }
}

/**
 * レイヤ定義。`.claude/rules/ksp-architecture.md` の依存方向テーブルの allow-list 表現。
 * feature 間の依存・core サブ（生成系）間の依存は、許可リストに載っていないことで禁止される。
 */
private val LAYER_POLICIES: List<LayerImportPolicy> =
    listOf(
        LayerImportPolicy(
            layerName = "root (CreamSymbolProcessor / Provider)",
            appliesTo = { it.packagee?.name == KSP_ROOT && it.nameWithExtension != "ProcessContext.kt" },
            allowedTargets =
                listOf(
                    packageTarget(FEATURE_PACKAGE),
                    packageTarget(CORE_PACKAGE),
                    packageTarget(OPTIONS_PACKAGE),
                    packageTarget(UTIL_PACKAGE),
                    creamRuntimeTarget,
                ),
        ),
        LayerImportPolicy(
            layerName = "root (ProcessContext)",
            appliesTo = { it.packagee?.name == KSP_ROOT && it.nameWithExtension == "ProcessContext.kt" },
            // leaf: cream 配下では options のみ（KSP API を含む全 import の制限は別テストで検査）
            allowedTargets = listOf(packageTarget(OPTIONS_PACKAGE)),
        ),
        LayerImportPolicy(
            layerName = "feature",
            appliesTo = { it.inPackageTree(FEATURE_PACKAGE) },
            // 他 feature のパッケージは載せない = feature 間依存の禁止。
            // ProcessContext は唯一の上向き依存として型単位で許可する
            allowedTargets =
                listOf(
                    packageTarget(CORE_PACKAGE),
                    packageTarget(OPTIONS_PACKAGE),
                    packageTarget(UTIL_PACKAGE),
                    creamRuntimeTarget,
                    typeTarget("$KSP_ROOT.ProcessContext"),
                ),
        ),
        LayerImportPolicy(
            layerName = "core/common",
            appliesTo = { it.inPackageTree("$CORE_PACKAGE.common") },
            allowedTargets =
                listOf(
                    // 自パッケージ: 同名衝突を避ける alias import（copyFunctionName as …）の実測分
                    packageTarget("$CORE_PACKAGE.common"),
                    packageTarget("$CORE_PACKAGE.error"),
                    packageTarget(OPTIONS_PACKAGE),
                    packageTarget(UTIL_PACKAGE),
                    creamRuntimeTarget,
                ),
        ),
    ) +
        CORE_GENERATOR_SUBPACKAGES.map { sub ->
            LayerImportPolicy(
                layerName = "core/$sub",
                appliesTo = { it.inPackageTree("$CORE_PACKAGE.$sub") },
                // 他の生成系サブパッケージは載せない = core サブ間の依存禁止（common/error のみ許可）
                allowedTargets =
                    listOf(
                        packageTarget("$CORE_PACKAGE.common"),
                        packageTarget("$CORE_PACKAGE.error"),
                        packageTarget(OPTIONS_PACKAGE),
                        packageTarget(UTIL_PACKAGE),
                        creamRuntimeTarget,
                    ),
            )
        } +
        listOf(
            LayerImportPolicy(
                layerName = "core/error",
                appliesTo = { it.inPackageTree("$CORE_PACKAGE.error") },
                // util のみに依存する葉
                allowedTargets = listOf(packageTarget(UTIL_PACKAGE)),
            ),
            LayerImportPolicy(
                layerName = "options",
                appliesTo = { it.inPackageTree(OPTIONS_PACKAGE) },
                // パース失敗の送出のための core/error と、CopyVisibility 等の runtime 参照
                allowedTargets =
                    listOf(
                        packageTarget("$CORE_PACKAGE.error"),
                        packageTarget(UTIL_PACKAGE),
                        creamRuntimeTarget,
                    ),
            ),
            LayerImportPolicy(
                layerName = "util",
                appliesTo = { it.inPackageTree(UTIL_PACKAGE) },
                // 汎用ヘルパのみ: cream のどのパッケージも import しない（実測どおりの空リスト）
                allowedTargets = emptyList(),
            ),
        )

/** このファイルの package が [packageName] そのもの、またはその部分パッケージにあるか。 */
private fun KoFileDeclaration.inPackageTree(packageName: String): Boolean = packagee?.name.orEmpty().inPackageTree(packageName)

/**
 * この import 名（またはパッケージ名）が [packageName] そのもの、またはその配下にあるか。
 * ワイルドカード import はツールにより `pkg` / `pkg.*` の 2 形式があり得るため、どちらも配下として扱う。
 */
private fun String.inPackageTree(packageName: String): Boolean = this == packageName || startsWith("$packageName.")

/** [isDisallowed] に一致する import を「ファイル: import 名」の形で列挙する（違反メッセージ用）。 */
private fun KoFileDeclaration.importDescriptions(isDisallowed: (String) -> Boolean): List<String> =
    imports
        .filter { isDisallowed(it.name) }
        .map { "$projectPath: import ${it.name}" }
