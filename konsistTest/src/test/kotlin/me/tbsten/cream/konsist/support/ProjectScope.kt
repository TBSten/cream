package me.tbsten.cream.konsist.support

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import java.io.File

/**
 * The repository-wide Konsist scope shared by every spec in this module.
 *
 * [Konsist.scopeFromProject] walks the whole repository, so one scope sees every Gradle module and
 * every source set — KMP `commonMain` / `commonTest` as well as plain JVM `main` / `test`.
 * [projectFiles] narrows that raw scope down to the repository's own modules.
 *
 * TODO: once repository-wide rules land here, decide whether the three `cream-ksp`-local
 *  architecture specs should move into this module (they would then filter [projectFiles] down to
 *  `:cream-ksp` / `main`).
 */
internal object ProjectScope {
    /**
     * Gradle project paths (`:cream-ksp`, or `:a:b` for a nested module) read straight out of
     * `settings.gradle.kts`, so the scope's definition of "our modules" has a single source of
     * truth. The parse takes every quoted path on an `include(…)` line — `include(":a", ":b")` is
     * equally valid Gradle, and dropping `:b` would silently shrink the scope instead of failing.
     */
    val includedProjectPaths: List<String> by lazy {
        File(Konsist.projectRootPath, "settings.gradle.kts")
            .readLines()
            .filter { line -> INCLUDE_CALL_REGEX.containsMatchIn(line) }
            .flatMap { line -> PROJECT_PATH_REGEX.findAll(line).map { it.groupValues[1] } }
            .also { check(it.isNotEmpty()) { "No include(\":…\") found in settings.gradle.kts" } }
    }

    /**
     * Every Kotlin source file of the repository's own modules, across all of their source sets.
     *
     * An allow-list over [includedProjectPaths] rather than a deny-list: the raw scope contains
     * anything Kotlin-shaped in the working tree, and the git-ignored `.local/` holds whole scratch
     * Gradle projects with real `src/<sourceSet>/` layouts that path shape alone cannot tell apart
     * from first-party modules. Generated code needs no filter — Konsist unconditionally skips
     * `build` directories.
     */
    val projectFiles: List<KoFileDeclaration> by lazy {
        val moduleNames = includedProjectPaths.map { it.toKonsistModuleName() }.toSet()
        Konsist
            .scopeFromProject()
            .files
            .filter { it.moduleName in moduleNames }
    }

    /** [projectFiles] restricted to production source sets (see [isInTestSourceSet]). */
    val productionFiles: List<KoFileDeclaration>
        get() = projectFiles.filterNot { it.isInTestSourceSet }

    /** [projectFiles] restricted to test source sets (see [isInTestSourceSet]). */
    val testFiles: List<KoFileDeclaration>
        get() = projectFiles.filter { it.isInTestSourceSet }

    /**
     * Konsist's [KoFileDeclaration.moduleName] is a directory path (`a/b`), not a Gradle project
     * path; this converts back to the `:`-separated form used in `settings.gradle.kts`.
     */
    val KoFileDeclaration.gradleProjectPath: String
        get() = ":" + moduleName.replace(File.separatorChar, ':')

    /**
     * Konsist's own production/test split (behind `scopeFromProduction` / `scopeFromTest`): the
     * source-set name contains `test`, case-insensitively. Reproduced here so both halves derive
     * from the single [projectFiles] parse instead of building a second scope.
     */
    val KoFileDeclaration.isInTestSourceSet: Boolean
        get() = sourceSetName.lowercase().contains("test")

    /** `:cream-ksp` -> `cream-ksp`, `:a:b` -> `a/b` (Konsist's [KoFileDeclaration.moduleName] form). */
    private fun String.toKonsistModuleName(): String = removePrefix(":").replace(':', File.separatorChar)

    /** Opens an `include(…)` call — `includeBuild(…)` cannot match, as `(` must follow `include`. */
    private val INCLUDE_CALL_REGEX = Regex("""^\s*include\s*\(""")

    /** One `":cream-ksp"` argument of such a call; repeats for every path on the line. */
    private val PROJECT_PATH_REGEX = Regex("\"(:[^\"]+)\"")
}
