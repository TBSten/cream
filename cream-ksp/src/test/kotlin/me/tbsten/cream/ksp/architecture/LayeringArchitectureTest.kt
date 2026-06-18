package me.tbsten.cream.ksp.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.verify.assertFalse
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Architecture test (issue #130) that makes the documented `feature → core → util` layering of
 * `cream-ksp`'s production source set executable. The boundaries are described in
 * `.claude/rules/ksp-architecture.md`; this test turns them into a guardrail so an accidental
 * back-edge fails CI instead of silently eroding the structure.
 *
 * Layers (by package), with their allowed dependency direction:
 * - root (`me.tbsten.cream.ksp.*`) — `CreamSymbolProcessor` / `Provider` / `ProcessContext`. May depend on everything.
 * - `feature.<name>` — per-annotation entry points (discover → validate → call core). May depend on `core`, `util`,
 *                      and `ProcessContext`; must NOT depend on another `feature.<name>`. Each exposes a single
 *                      top-level entry point: `context(ProcessContext) internal fun processXxx(): List<KSAnnotated>`.
 * - `core`           — cream-specific generation logic. May depend on `util`; must NOT depend on `feature` nor on the
 *                      root infra (`ProcessContext` / `CreamSymbolProcessor`) — it receives a per-layer context instead.
 * - `util`           — generic helpers reusable elsewhere, split in two: `util` (top-level) holds Kotlin-only helpers
 *                      and must NOT depend on the KSP API; KSP-flavoured helpers live in `util.ksp`. Neither may
 *                      depend on `core` / `feature` nor reference cream-specific types.
 *
 * The checks are import-based, matching the project convention of always importing referenced symbols (no wildcard
 * imports, no fully-qualified inline references; enforced by ktlint). The entry-point signature check reads
 * `KoFunctionDeclaration.text` for the `context(...)` clause, since Konsist does not model context parameters.
 */
internal class LayeringArchitectureTest :
    FunSpec(
        {
            test("util レイヤは core / feature レイヤに依存しない") {
                creamKspMain
                    .filter { it.inLayer(UTIL_PACKAGE) }
                    .assertFalse { file -> file.importsFrom("$CORE_PACKAGE.", "$FEATURE_PACKAGE.") }
            }

            test("util レイヤは cream 固有の型を参照しない（自分自身の util パッケージのみ許可）") {
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

            test("util 直下（util.ksp を除く）は KSP 型に依存しない（KSP util は util/ksp に置く）") {
                // Helpers that touch the KSP API belong in `util.ksp`. The top-level `util` package stays
                // Kotlin-only so it can be reused in non-KSP contexts.
                creamKspMain
                    .filter { it.packagee?.name == UTIL_PACKAGE }
                    .assertFalse { file -> file.importsFrom("$KSP_API_PACKAGE.") }
            }

            test("core レイヤは feature レイヤに依存しない") {
                creamKspMain
                    .filter { it.inLayer(CORE_PACKAGE) }
                    .assertFalse { file -> file.importsFrom("$FEATURE_PACKAGE.") }
            }

            test("core レイヤは root infra（ProcessContext / CreamSymbolProcessor）に依存しない") {
                // core receives a narrowed `context(options, logger)` instead of the whole ProcessContext,
                // and never reaches back into the composition root.
                creamKspMain
                    .filter { it.inLayer(CORE_PACKAGE) }
                    .assertFalse { file ->
                        file.importsFrom("$KSP_ROOT.ProcessContext", "$KSP_ROOT.CreamSymbolProcessor")
                    }
            }

            test("feature レイヤは他の feature を参照しない（feature 間で関数などを使い合わない）") {
                creamKspMain
                    .filter { it.inLayer(FEATURE_PACKAGE) }
                    .assertFalse { file ->
                        val ownPackage = file.packagee?.name ?: return@assertFalse false
                        file.imports.any { import ->
                            import.name.startsWith("$FEATURE_PACKAGE.") && !import.name.startsWith("$ownPackage.")
                        }
                    }
            }

            test("各 feature.<name> は context(ProcessContext) な internal fun processXxx(): List<KSAnnotated> を公開する") {
                val byPackage =
                    creamKspMain
                        .filter { it.inLayer(FEATURE_PACKAGE) }
                        .groupBy { it.packagee?.name }

                withClue("feature パッケージが 1 つも検出されていない（scope 設定の誤り）") {
                    byPackage.isNotEmpty() shouldBe true
                }

                byPackage.forEach { (packageName, files) ->
                    val entryPoints =
                        files
                            .flatMap { it.functions(includeNested = false, includeLocal = false) }
                            .filter { it.name.startsWith("process") }

                    withClue("feature '$packageName' は process* なトップレベル関数を公開していない") {
                        entryPoints.isNotEmpty() shouldBe true
                    }

                    entryPoints.forEach { entryPoint ->
                        withClue("feature entry point '${entryPoint.name}' は internal であるべき") {
                            entryPoint.hasInternalModifier shouldBe true
                        }
                        withClue(
                            "feature entry point '${entryPoint.name}' は List<KSAnnotated> を返すべき " +
                                "(actual: ${entryPoint.returnType?.sourceType})",
                        ) {
                            entryPoint.returnType?.sourceType shouldBe "List<KSAnnotated>"
                        }
                        withClue(
                            "feature entry point '${entryPoint.name}' は context(ProcessContext) を宣言すべき " +
                                "(declaration: ${entryPoint.text.substringBefore('{').trim()})",
                        ) {
                            entryPoint.text.contains("context(") shouldBe true
                            entryPoint.text.contains("ProcessContext") shouldBe true
                        }
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
