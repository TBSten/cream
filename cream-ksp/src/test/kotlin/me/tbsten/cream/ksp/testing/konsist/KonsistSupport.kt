package me.tbsten.cream.ksp.testing.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration

/**
 * Shared Konsist scope and layer helpers for the architecture tests (issue #130). The documented
 * `feature → core → util` boundaries of `cream-ksp`'s production source set are split across three
 * specs so a violation points straight at the layer that broke:
 *
 * - [me.tbsten.cream.ksp.AllKotlinFilesTest] — module-wide rules (root package allow-list, per-file
 *   line budget).
 * - [me.tbsten.cream.ksp.feature.ArchTest] — feature-layer rules.
 * - [me.tbsten.cream.ksp.core.ArchTest] — core- and util-layer rules.
 *
 * The authoritative dependency-direction table lives in `.claude/rules/ksp-architecture.md`; this
 * file only encodes the package names and reusable predicates those specs share.
 */

internal const val CREAM_ROOT = "me.tbsten.cream"
internal const val KSP_ROOT = "$CREAM_ROOT.ksp"
internal const val UTIL_PACKAGE = "$KSP_ROOT.util"
internal const val CORE_PACKAGE = "$KSP_ROOT.core"
internal const val FEATURE_PACKAGE = "$KSP_ROOT.feature"
internal const val KSP_API_PACKAGE = "com.google.devtools.ksp"
internal const val PROCESS_CONTEXT_TYPE = "$KSP_ROOT.ProcessContext"
internal const val MAX_FILE_LINES = 300

/**
 * Composition-root infra types defined directly in the root `me.tbsten.cream.ksp` package. Neither
 * `core` nor `feature` may import these. (`feature` may still import [PROCESS_CONTEXT_TYPE].) Listed
 * explicitly so the intent matches `ksp-architecture.md`, rather than relying on `CreamSymbolProcessor`
 * being a prefix of `CreamSymbolProcessorProvider`.
 */
internal val COMPOSITION_ROOT_TYPES =
    arrayOf(
        "$KSP_ROOT.CreamSymbolProcessor",
        "$KSP_ROOT.CreamSymbolProcessorProvider",
    )

/**
 * Per-file overrides to [MAX_FILE_LINES] (keyed by file name). Files here may grow up to their
 * listed limit instead of the default 300. Keep this list tiny and justified — it is an escape
 * hatch, not the norm.
 *
 * - `FindMatchedProperty.kt`: プロパティ照合（`@Map` / 名前一致 / デフォルト値）の分岐が密結合で、
 *   無理に割るとロジックが追いにくくなるため、目安 300 ではなく上限 500 まで許容する。
 */
internal val FILE_LINE_LIMIT_OVERRIDES =
    mapOf(
        "FindMatchedProperty.kt" to 500,
    )

/** The sub-packages `core` is allowed to contain (`core/` must not hold files directly). */
internal val CORE_SUBPACKAGES =
    setOf(
        "$CORE_PACKAGE.common",
        "$CORE_PACKAGE.copyFun",
        "$CORE_PACKAGE.combineFun",
        "$CORE_PACKAGE.sealedCopy",
        "$CORE_PACKAGE.callFrom",
    )

/** The only files allowed directly in the root `me.tbsten.cream.ksp` package (the composition root). */
internal val ROOT_ALLOWED_FILES =
    setOf(
        "CreamSymbolProcessor.kt",
        "CreamSymbolProcessorProvider.kt",
        "ProcessContext.kt",
    )

/**
 * cream-ksp's production (`main`) source set only — excludes the test source set and the nested
 * `:cream-ksp:shared` module. Parsed once, lazily, and reused across the architecture specs.
 */
internal val creamKspMain: List<KoFileDeclaration> by lazy {
    Konsist.scopeFromProduction(moduleName = "cream-ksp", sourceSetName = "main").files
}

/** True when this file's package is [layerPackage] or one of its sub-packages. */
internal fun KoFileDeclaration.inLayer(layerPackage: String): Boolean {
    val packageName = packagee?.name ?: return false
    return packageName == layerPackage || packageName.startsWith("$layerPackage.")
}

/** True when this file imports any symbol whose fully-qualified name starts with one of [importPrefixes]. */
internal fun KoFileDeclaration.importsFrom(vararg importPrefixes: String): Boolean =
    imports.any { import ->
        importPrefixes.any { prefix -> import.name.startsWith(prefix) }
    }
