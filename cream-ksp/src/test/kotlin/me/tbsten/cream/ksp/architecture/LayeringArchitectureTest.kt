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
 *                      and `ProcessContext`; must NOT depend on another `feature.<name>`.
 * - `core`           — cream-specific generation logic. May depend on `util`; must NOT depend on `feature` nor on the
 *                      root infra (`ProcessContext` / `CreamSymbolProcessor`) — it receives a per-layer context instead.
 * - `util`           — generic KSP/Kotlin helpers reusable elsewhere. Must NOT depend on `core` / `feature`, and must
 *                      NOT reference any cream-specific type (only generic helpers).
 *
 * The checks are import-based, which matches the project convention of always importing referenced
 * symbols (no wildcard imports, no fully-qualified inline references; enforced by ktlint).
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

            test("feature レイヤは他の feature に依存しない") {
                creamKspMain
                    .filter { it.inLayer(FEATURE_PACKAGE) }
                    .assertFalse { file ->
                        val ownPackage = file.packagee?.name ?: return@assertFalse false
                        file.imports.any { import ->
                            import.name.startsWith("$FEATURE_PACKAGE.") && !import.name.startsWith("$ownPackage.")
                        }
                    }
            }

            test("各 feature.<name> パッケージは process で始まるトップレベルのエントリポイントを公開する") {
                val featurePackages =
                    creamKspMain
                        .filter { it.inLayer(FEATURE_PACKAGE) }
                        .groupBy { it.packagee?.name }

                withClue("feature パッケージが 1 つも検出されていない（scope 設定の誤り）") {
                    featurePackages.isNotEmpty() shouldBe true
                }

                featurePackages.forEach { (packageName, files) ->
                    val exposesProcessEntry =
                        files.any { file ->
                            file
                                .functions(includeNested = false, includeLocal = false)
                                .any { it.name.startsWith("process") }
                        }
                    withClue("feature パッケージ '$packageName' は process* なトップレベル関数を公開していない") {
                        exposesProcessEntry shouldBe true
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
