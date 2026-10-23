@file:OptIn(ExperimentalKatachiApi::class)

package me.tbsten.cream.test.architecture.support

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import me.tbsten.katachi.ExperimentalKatachiApi
import me.tbsten.katachi.dsl.FileConstraintFailure
import me.tbsten.katachi.dsl.FileConstraintScope
import me.tbsten.katachi.konsist.konsist
import java.io.File

// Constraints more than one role shares. Each one is a single, named rule so that a report line says which
// convention a file broke.

private const val FREE_SPEC = "io.kotest.core.spec.style.FreeSpec"
private const val CREAM_PACKAGE = "me.tbsten.cream"
private const val KSP_PACKAGE = "$CREAM_PACKAGE.ksp"
private const val KSP_API_PACKAGE = "com.google.devtools.ksp"
private const val FEATURE_PACKAGE = "$KSP_PACKAGE.feature"

/** The default per-file line budget of cream-ksp's production code (目安 10〜300 行、最大 500). */
private const val MAX_FILE_LINES = 300

/**
 * Per-file overrides to [MAX_FILE_LINES] (keyed by file name). Keep this list tiny and justified — it is an escape
 * hatch, not the norm.
 *
 * - `FindMatchedProperty.kt`: プロパティ照合（`@Map` / 名前一致 / デフォルト値）の分岐が密結合で、無理に割るとロジックが
 *   追いにくくなるため、目安 300 ではなく上限 500 まで許容する。
 */
private val FILE_LINE_LIMIT_OVERRIDES =
    mapOf(
        "FindMatchedProperty.kt" to 500,
    )

/** The package declaration follows the directory, so a file moved by hand cannot keep its old package. */
context(scope: FileConstraintScope)
internal fun packageFollowsDirectory() =
    "declares the package its directory spells".konsist {
        files.filter { it.packagee != null }.must { it.packagee?.hasMatchingPath == true }
    }

/** 1 ファイル原則 [MAX_FILE_LINES] 行以内（[FILE_LINE_LIMIT_OVERRIDES] のファイルは個別上限）。超えたら責務分割する。 */
context(scope: FileConstraintScope)
internal fun staysWithinLineBudget() =
    "stays within $MAX_FILE_LINES lines (${FILE_LINE_LIMIT_OVERRIDES.entries.joinToString { "${it.key}: ${it.value}" }})".konsist {
        files.must { file ->
            file.text.lines().size <= (FILE_LINE_LIMIT_OVERRIDES[file.nameWithExtension] ?: MAX_FILE_LINES)
        }
    }

/**
 * One row of the dependency table of cream-ksp (see the `creamKsp` group): of cream's own packages only
 * [allowedKspPackages] may be imported. An entry ending in `.` is a package prefix below `me.tbsten.cream.ksp`
 * (`"util."`), any other entry one declaration (`"ProcessContext"`). [runtime] allows cream-runtime
 * (`me.tbsten.cream.*`), [kspApi] allows the KSP API (`com.google.devtools.ksp.*`).
 */
context(scope: FileConstraintScope)
internal fun dependsOnlyOn(
    vararg allowedKspPackages: String,
    runtime: Boolean,
    kspApi: Boolean,
) = (
    allowedKspPackages.map { if (it.endsWith('.')) "$it*" else it } +
        listOfNotNull("cream-runtime".takeIf { runtime }, "the KSP API".takeIf { kspApi })
).joinToString(prefix = "imports from cream / KSP only: ").konsist {
    files.mustNot { file ->
        file.imports.any { import -> !import.name.isAllowedImport(allowedKspPackages, runtime, kspApi) }
    }
}

private fun String.isAllowedImport(
    allowedKspPackages: Array<out String>,
    runtime: Boolean,
    kspApi: Boolean,
): Boolean =
    when {
        startsWith("$KSP_API_PACKAGE.") -> kspApi
        startsWith("$KSP_PACKAGE.") -> {
            val relative = removePrefix("$KSP_PACKAGE.")
            allowedKspPackages.any { allowed ->
                if (allowed.endsWith('.')) relative.startsWith(allowed) else relative == allowed
            }
        }
        startsWith("$CREAM_PACKAGE.") -> runtime
        else -> true
    }

/** feature 間依存禁止: a `feature.<name>` file imports nothing from another `feature.<name>`. */
context(scope: FileConstraintScope)
internal fun doesNotImportAnotherFeature() =
    "does not import another feature".konsist {
        files.mustNot { file ->
            val ownPackage = file.packagee?.name.orEmpty()
            file.imports.any { it.name.startsWith("$FEATURE_PACKAGE.") && !it.name.startsWith("$ownPackage.") }
        }
    }

/**
 * Every feature exposes a top-level entry point `context(ctx: ProcessContext) internal fun processXxx(): List<KSAnnotated>`.
 * Each part is its own named constraint, so a report says which part a feature broke.
 */
context(scope: FileConstraintScope)
internal fun declaresFeatureEntryPoint() {
    "declares a top-level fun process*()".konsist {
        files.must { file ->
            file.functions(includeNested = false, includeLocal = false).any { it.name.startsWith("process") }
        }
    }
    "process*() is internal".konsist {
        entryPoints().must { it.hasInternalModifier }
    }
    "process*() returns List<KSAnnotated>".konsist {
        entryPoints().must { it.returnType?.sourceType == "List<KSAnnotated>" }
    }
    "process*() declares context(ctx: ProcessContext)".konsist {
        entryPoints().must { function ->
            // Konsist does not model context parameters (and leaves the clause out of `text`), so read the
            // declaration off the file: the `context(...)` clause right before `fun processXxx(`.
            Regex("""context\([^)]*\bProcessContext\b[^)]*\)\s*(internal\s+)?fun\s+${function.name}\(""")
                .containsMatchIn(function.containingFile.text)
        }
    }
}

private fun KoScope.entryPoints() = functions(includeNested = false, includeLocal = false).filter { it.name.startsWith("process") }

/** cream-ksp's tests are `internal class XxxTest : FreeSpec({ ... })`. */
context(scope: FileConstraintScope)
internal fun isInternalFreeSpec() =
    "is an internal kotest FreeSpec".konsist {
        classes().withNameEndingWith("Test").must { it.hasInternalModifier && it.extendsFreeSpec() }
    }

/** Every test in the repository is written in kotest's `FreeSpec` style (CLAUDE.md, "Testing Approach"). */
context(scope: FileConstraintScope)
internal fun isFreeSpec() =
    "is a kotest FreeSpec".konsist {
        classes().withNameEndingWith("Test").must { it.extendsFreeSpec() }
    }

/** `feature/copyTo/CopyToSnapshotTest.kt`: the file name starts with its feature directory, in PascalCase. */
context(scope: FileConstraintScope)
internal fun isNamedAfterFeatureDirectory() =
    "is named after its feature directory".konsist {
        files.must { file ->
            val feature = file.path.substringBeforeLast('/').substringAfterLast('/')
            file.name.startsWith(feature.replaceFirstChar(Char::uppercaseChar))
        }
    }

/** A user-facing page and its Japanese counterpart are updated together, so both always exist. */
context(scope: FileConstraintScope)
internal fun hasJapaneseTranslation() =
    scope.fileConstraint("has its Japanese counterpart (*.ja.md) next to it, and vice versa") { subject ->
        subject.files
            .filter { it.endsWith(".md") }
            .filterNot { path ->
                val counterpart =
                    if (path.endsWith(".ja.md")) path.removeSuffix(".ja.md") + ".md" else path.removeSuffix(".md") + ".ja.md"
                File(subject.projectRoot, counterpart).isFile
            }.map { FileConstraintFailure(it) }
    }

/** Skills are read by agents, so they are English only: no `*.ja.md`. */
context(scope: FileConstraintScope)
internal fun hasNoJapaneseTranslation() =
    scope.fileConstraint("is English only (no *.ja.md)") { subject ->
        subject.files.filter { it.endsWith(".ja.md") }.map { FileConstraintFailure(it) }
    }

/** True when this class extends kotest's `FreeSpec`, also through an import alias (`FreeSpec as KotestFreeSpec`). */
private fun KoClassDeclaration.extendsFreeSpec(): Boolean {
    val freeSpecNames =
        containingFile.imports
            .filter { it.name == FREE_SPEC }
            .map { it.alias?.name ?: FREE_SPEC.substringAfterLast('.') }
    return parents().any { it.name in freeSpecNames }
}
