package me.tbsten.cream.ksp.core

import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import io.kotest.core.spec.style.FunSpec
import me.tbsten.cream.ksp.testing.konsist.COMPOSITION_ROOT_TYPES
import me.tbsten.cream.ksp.testing.konsist.CORE_PACKAGE
import me.tbsten.cream.ksp.testing.konsist.CORE_SUBPACKAGES
import me.tbsten.cream.ksp.testing.konsist.CREAM_ROOT
import me.tbsten.cream.ksp.testing.konsist.FEATURE_PACKAGE
import me.tbsten.cream.ksp.testing.konsist.KSP_API_PACKAGE
import me.tbsten.cream.ksp.testing.konsist.PROCESS_CONTEXT_TYPE
import me.tbsten.cream.ksp.testing.konsist.UTIL_PACKAGE
import me.tbsten.cream.ksp.testing.konsist.creamKspMain
import me.tbsten.cream.ksp.testing.konsist.importsFrom
import me.tbsten.cream.ksp.testing.konsist.inLayer

/**
 * Core- and util-layer architecture test (issue #130) — the lower half of the `feature → core → util`
 * direction:
 *
 * - `core` — cream-specific generation logic. May depend on `util`; must NOT depend on `feature` nor on
 *   the root infra (`ProcessContext` / `CreamSymbolProcessor`) — it receives a per-layer context
 *   instead. Files live only in `common`/`copyFun`/`combineFun`/`sealedCopy`.
 * - `util` — generic helpers reusable elsewhere, split in two: `util` (top-level) holds Kotlin-only
 *   helpers and must NOT depend on the KSP API; KSP-flavoured helpers live in `util.ksp`. Neither may
 *   depend on `core` / `feature` nor reference cream-specific types.
 *
 * Feature-layer and module-wide rules live in [me.tbsten.cream.ksp.feature.ArchTest] and
 * [me.tbsten.cream.ksp.AllKotlinFilesTest]. The authoritative dependency-direction table is in
 * `.claude/rules/ksp-architecture.md`.
 */
internal class ArchTest :
    FunSpec({
        context("core レイヤ") {
            test("feature レイヤに依存しない") {
                creamKspMain
                    .filter { it.inLayer(CORE_PACKAGE) }
                    .assertFalse { file -> file.importsFrom("$FEATURE_PACKAGE.") }
            }

            test("root infra（ProcessContext / CreamSymbolProcessor / Provider）に依存しない") {
                // core receives a narrowed `context(options, logger)` instead of the whole ProcessContext,
                // and never reaches back into the composition root.
                creamKspMain
                    .filter { it.inLayer(CORE_PACKAGE) }
                    .assertFalse { file ->
                        file.importsFrom(PROCESS_CONTEXT_TYPE, *COMPOSITION_ROOT_TYPES)
                    }
            }

            test("common / copyFun / combineFun / sealedCopy サブパッケージにのみ置く（core/ 直下に .kt を置かない）") {
                creamKspMain
                    .filter { it.inLayer(CORE_PACKAGE) }
                    .assertTrue { file -> file.packagee?.name in CORE_SUBPACKAGES }
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
    })
