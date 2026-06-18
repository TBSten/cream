package me.tbsten.cream.ksp.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Architecture test (issue #130) that makes the documented `feature → core → util` layering of
 * `cream-ksp`'s production source set executable. The boundaries are described in
 * `.claude/rules/ksp-architecture.md`; this test turns them into a guardrail so an accidental
 * back-edge fails CI instead of silently eroding the structure.
 *
 * Tests are grouped per layer (`context("... レイヤ")`):
 * - root (`me.tbsten.cream.ksp`) — composition root. The package holds ONLY `CreamSymbolProcessor`,
 *                      `CreamSymbolProcessorProvider`, and `ProcessContext`; may depend on everything.
 * - `feature.<name>` — per-annotation entry points (discover → validate → call core). May depend on `core`, `util`,
 *                      and `ProcessContext`; must NOT depend on another `feature.<name>`. Each exposes a single
 *                      top-level entry point: `context(ProcessContext) internal fun processXxx(): List<KSAnnotated>`.
 * - `core`           — cream-specific generation logic. May depend on `util`; must NOT depend on `feature` nor on the
 *                      root infra (`ProcessContext` / `CreamSymbolProcessor`) — it receives a per-layer context instead.
 * - `util`           — generic helpers reusable elsewhere, split in two: `util` (top-level) holds Kotlin-only helpers
 *                      and must NOT depend on the KSP API; KSP-flavoured helpers live in `util.ksp`. Neither may
 *                      depend on `core` / `feature` nor reference cream-specific types.
 *
 * Structural / module rules also enforced: `core` files live only in `common`/`copyFun`/`combineFun`/`sealedCopy`,
 * `feature` files only in a `feature.<name>` sub-package; every file stays within its line budget
 * (default 300, with a few justified [FILE_LINE_LIMIT_OVERRIDES]).
 *
 * The checks are import-based, matching the project convention of always importing referenced symbols rather than
 * using wildcard imports (forbidden by ktlint) or fully-qualified inline references (a convention only — not
 * lint-enforced). The entry-point signature check reads `KoFunctionDeclaration.text` for the `context(...)` clause,
 * since Konsist does not model context parameters.
 */
internal class LayeringArchitectureTest :
    FunSpec(
        {
            context("root レイヤ（composition root）") {
                test("直下には承認済みファイル（CreamSymbolProcessor / Provider / ProcessContext）以外を置かない") {
                    // The root package is the composition root only. Generation logic, helpers, exceptions
                    // (which live in :cream-ksp:shared), etc. must NOT be added here.
                    creamKspMain
                        .filter { it.packagee?.name == KSP_ROOT }
                        .assertTrue { file -> file.nameWithExtension in ROOT_ALLOWED_FILES }
                }
            }

            context("util レイヤ") {
                test("core / feature レイヤに依存しない") {
                    creamKspMain
                        .filter { it.inLayer(UTIL_PACKAGE) }
                        .assertFalse { file -> file.importsFrom("$CORE_PACKAGE.", "$FEATURE_PACKAGE.") }
                }

                test("cream 固有の型を参照しない（自分自身の util パッケージのみ許可）") {
                    // A util file may only reference cream packages that live under `util` itself. Any other
                    // `me.tbsten.cream.*` import (core / feature / ProcessContext / options / runtime annotations, …)
                    // would make it cream-specific and therefore not a generic, reusable helper.
                    creamKspMain
                        .filter { it.inLayer(UTIL_PACKAGE) }
                        .assertFalse { file ->
                            file.imports.any { import ->
                                import.name.startsWith("$CREAM_ROOT.") && !import.name.startsWith("$UTIL_PACKAGE.")
                            }
                        }
                }

                test("直下（util.ksp を除く）は KSP 型に依存しない（KSP util は util/ksp に置く）") {
                    // Helpers that touch the KSP API belong in `util.ksp`. The top-level `util` package stays
                    // Kotlin-only so it can be reused in non-KSP contexts.
                    creamKspMain
                        .filter { it.packagee?.name == UTIL_PACKAGE }
                        .assertFalse { file -> file.importsFrom("$KSP_API_PACKAGE.") }
                }
            }

            context("core レイヤ") {
                test("feature レイヤに依存しない") {
                    creamKspMain
                        .filter { it.inLayer(CORE_PACKAGE) }
                        .assertFalse { file -> file.importsFrom("$FEATURE_PACKAGE.") }
                }

                test("root infra（ProcessContext / CreamSymbolProcessor）に依存しない") {
                    // core receives a narrowed `context(options, logger)` instead of the whole ProcessContext,
                    // and never reaches back into the composition root.
                    creamKspMain
                        .filter { it.inLayer(CORE_PACKAGE) }
                        .assertFalse { file ->
                            file.importsFrom("$KSP_ROOT.ProcessContext", "$KSP_ROOT.CreamSymbolProcessor")
                        }
                }

                test("common / copyFun / combineFun / sealedCopy サブパッケージにのみ置く（core/ 直下に .kt を置かない）") {
                    creamKspMain
                        .filter { it.inLayer(CORE_PACKAGE) }
                        .assertTrue { file -> file.packagee?.name in CORE_SUBPACKAGES }
                }
            }

            context("feature レイヤ") {
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
                                withClue(
                                    "entry point '${entryPoint.name}' は context(ProcessContext) を宣言すべき " +
                                        "(declaration: ${entryPoint.text.substringBefore('{').trim()})",
                                ) {
                                    entryPoint.text.contains("context(") shouldBe true
                                    entryPoint.text.contains("ProcessContext") shouldBe true
                                }
                            }
                        }
                    }
                }
            }

            context("ファイル全般（モジュール共通）") {
                test("1 ファイル原則 $MAX_FILE_LINES 行以内（FILE_LINE_LIMIT_OVERRIDES のファイルは個別上限）") {
                    creamKspMain.assertFalse { file ->
                        val limit = FILE_LINE_LIMIT_OVERRIDES[file.nameWithExtension] ?: MAX_FILE_LINES
                        file.text.lines().size > limit
                    }
                }
            }
        },
    )

private const val CREAM_ROOT = "me.tbsten.cream"
private const val KSP_ROOT = "$CREAM_ROOT.ksp"
private const val UTIL_PACKAGE = "$KSP_ROOT.util"
private const val CORE_PACKAGE = "$KSP_ROOT.core"
private const val FEATURE_PACKAGE = "$KSP_ROOT.feature"
private const val KSP_API_PACKAGE = "com.google.devtools.ksp"
private const val MAX_FILE_LINES = 300

/**
 * Per-file overrides to [MAX_FILE_LINES] (keyed by file name). Files here may grow up to their
 * listed limit instead of the default 300. Keep this list tiny and justified — it is an escape
 * hatch, not the norm.
 *
 * - `FindMatchedProperty.kt`: プロパティ照合（`@Map` / 名前一致 / デフォルト値）の分岐が密結合で、
 *   無理に割るとロジックが追いにくくなるため、目安 300 ではなく上限 500 まで許容する。
 */
private val FILE_LINE_LIMIT_OVERRIDES =
    mapOf(
        "FindMatchedProperty.kt" to 500,
    )

/** The sub-packages `core` is allowed to contain (`core/` must not hold files directly). */
private val CORE_SUBPACKAGES =
    setOf(
        "$CORE_PACKAGE.common",
        "$CORE_PACKAGE.copyFun",
        "$CORE_PACKAGE.combineFun",
        "$CORE_PACKAGE.sealedCopy",
    )

/** The only files allowed directly in the root `me.tbsten.cream.ksp` package (the composition root). */
private val ROOT_ALLOWED_FILES =
    setOf(
        "CreamSymbolProcessor.kt",
        "CreamSymbolProcessorProvider.kt",
        "ProcessContext.kt",
    )

/**
 * cream-ksp's production (`main`) source set only — excludes the test source set and the nested
 * `:cream-ksp:shared` module. Parsed once, lazily, and reused across the assertions above.
 */
private val creamKspMain: List<KoFileDeclaration> by lazy {
    Konsist.scopeFromProduction(moduleName = "cream-ksp", sourceSetName = "main").files
}

/** True when this file's package is [layerPackage] or one of its sub-packages. */
private fun KoFileDeclaration.inLayer(layerPackage: String): Boolean {
    val packageName = packagee?.name ?: return false
    return packageName == layerPackage || packageName.startsWith("$layerPackage.")
}

/** True when this file imports any symbol whose fully-qualified name starts with one of [importPrefixes]. */
private fun KoFileDeclaration.importsFrom(vararg importPrefixes: String): Boolean = imports.any { import -> importPrefixes.any { prefix -> import.name.startsWith(prefix) } }
