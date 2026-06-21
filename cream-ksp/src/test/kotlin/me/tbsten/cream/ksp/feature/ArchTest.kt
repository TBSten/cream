package me.tbsten.cream.ksp.feature

import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import me.tbsten.cream.ksp.testing.konsist.COMPOSITION_ROOT_TYPES
import me.tbsten.cream.ksp.testing.konsist.FEATURE_PACKAGE
import me.tbsten.cream.ksp.testing.konsist.creamKspMain
import me.tbsten.cream.ksp.testing.konsist.importsFrom
import me.tbsten.cream.ksp.testing.konsist.inLayer

/**
 * Feature-layer architecture test (issue #130). Each `feature.<name>` package is a per-annotation
 * entry point ("discover → validate → call core"); it may depend on `core`, `util`, and
 * `ProcessContext`, but must NOT depend on another `feature.<name>` nor on the composition root.
 * Every feature exposes a single top-level entry point:
 * `context(ProcessContext) internal fun processXxx(): List<KSAnnotated>`.
 *
 * Module-wide and core/util rules live in [me.tbsten.cream.ksp.AllKotlinFilesTest] and
 * [me.tbsten.cream.ksp.core.ArchTest]. The authoritative dependency-direction table is in
 * `.claude/rules/ksp-architecture.md`. The entry-point signature check reads
 * `KoFunctionDeclaration.text` for the `context(...)` clause, since Konsist does not model context
 * parameters.
 */
internal class ArchTest :
    FunSpec({
        // Each `feature.<name>` package gets its own sub-context so a violation points straight at the
        // offending feature. The package list is discovered from the scope at registration time.
        val featurePackages =
            creamKspMain
                .filter { it.inLayer(FEATURE_PACKAGE) }
                .groupBy { it.packagee?.name.orEmpty() }
                .toSortedMap()

        test("scope に feature パッケージが存在する") {
            withClue("feature パッケージが 1 つも検出されていない（scope 設定の誤り）") {
                featurePackages.isNotEmpty() shouldBe true
            }
        }

        test("各ファイルは feature.<name> サブパッケージに置く（feature/ 直下・深いネストを禁止）") {
            creamKspMain
                .filter { it.inLayer(FEATURE_PACKAGE) }
                .assertTrue { file ->
                    val packageName = file.packagee?.name.orEmpty()
                    // exactly one segment below `feature`: me.tbsten.cream.ksp.feature.<name>
                    packageName.startsWith("$FEATURE_PACKAGE.") &&
                        !packageName.removePrefix("$FEATURE_PACKAGE.").contains('.')
                }
        }

        featurePackages.forEach { (packageName, files) ->
            context(packageName.substringAfterLast('.')) {
                test("他の feature を参照しない（feature 間で関数などを使い合わない）") {
                    files.assertFalse { file ->
                        file.imports.any { import ->
                            import.name.startsWith("$FEATURE_PACKAGE.") &&
                                !import.name.startsWith("$packageName.")
                        }
                    }
                }

                test("composition root（CreamSymbolProcessor / Provider）に依存しない（ProcessContext のみ可）") {
                    // feature は ProcessContext だけを上向きに依存してよく、合成ルート本体には触れない。
                    files.assertFalse { file -> file.importsFrom(*COMPOSITION_ROOT_TYPES) }
                }

                test("context(ProcessContext) な internal fun processXxx(): List<KSAnnotated> を公開する") {
                    val entryPoints =
                        files
                            .flatMap { it.functions(includeNested = false, includeLocal = false) }
                            .filter { it.name.startsWith("process") }

                    withClue("process* なトップレベル関数を公開していない") {
                        entryPoints.isNotEmpty() shouldBe true
                    }

                    entryPoints.forEach { entryPoint ->
                        withClue("entry point '${entryPoint.name}' は internal であるべき") {
                            entryPoint.hasInternalModifier shouldBe true
                        }
                        withClue(
                            "entry point '${entryPoint.name}' は List<KSAnnotated> を返すべき " +
                                "(actual: ${entryPoint.returnType?.sourceType})",
                        ) {
                            entryPoint.returnType?.sourceType shouldBe "List<KSAnnotated>"
                        }
                        // Inspect only the signature (everything before the body's `{`) so that a
                        // `context(` / `ProcessContext` occurrence inside the function body — a string
                        // literal or comment — cannot make the check pass spuriously.
                        val signature = entryPoint.text.substringBefore('{').trim()
                        withClue(
                            "entry point '${entryPoint.name}' は context(ProcessContext) を宣言すべき " +
                                "(declaration: $signature)",
                        ) {
                            signature.contains("context(") shouldBe true
                            signature.contains("ProcessContext") shouldBe true
                        }
                    }
                }
            }
        }
    })
