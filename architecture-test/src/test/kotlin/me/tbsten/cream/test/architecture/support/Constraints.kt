@file:OptIn(ExperimentalKatachiApi::class)

package me.tbsten.cream.test.architecture.support

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

/** The package declaration follows the directory, so a file moved by hand cannot keep its old package. */
context(scope: FileConstraintScope)
internal fun packageFollowsDirectory() =
    "declares the package its directory spells".konsist {
        files.filter { it.packagee != null }.must { it.packagee?.hasMatchingPath == true }
    }

/** cream-ksp's tests are `internal class XxxTest : FreeSpec({ ... })` (`.claude/rules/ksp-test.md`). */
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

/**
 * A dependency rule from the table in `.claude/rules/ksp-architecture.md` that cream-ksp's own Konsist specs do
 * not cover: of cream-ksp's packages, only [allowedPrefixes] may be imported (and, with [forbidKspApi], nothing
 * from the KSP API).
 */
context(scope: FileConstraintScope)
internal fun importsOnly(
    vararg allowedPrefixes: String,
    forbidKspApi: Boolean,
) = "imports only ${allowedPrefixes.joinToString()} from cream-ksp${if (forbidKspApi) ", and nothing from the KSP API" else ""}"
    .konsist {
        files.mustNot { file ->
            file.imports.any { import ->
                val name = import.name
                (forbidKspApi && name.startsWith("com.google.devtools.ksp.")) ||
                    (name.startsWith("me.tbsten.cream.ksp.") && allowedPrefixes.none { name.startsWith(it) })
            }
        }
    }

/** `.claude/rules/markdown.md`: a page and its Japanese counterpart are updated together, so both always exist. */
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

/** True when this class extends kotest's `FreeSpec`, also through an import alias (`FreeSpec as KotestFreeSpec`). */
private fun KoClassDeclaration.extendsFreeSpec(): Boolean {
    val freeSpecNames =
        containingFile.imports
            .filter { it.name == FREE_SPEC }
            .map { it.alias?.name ?: FREE_SPEC.substringAfterLast('.') }
    return parents().any { it.name in freeSpecNames }
}
