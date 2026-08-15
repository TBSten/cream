package me.tbsten.cream.konsist.support

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import java.io.File

/**
 * The repository-wide Konsist scope shared by every spec in this module.
 *
 * [Konsist.scopeFromProject] walks the whole project root (the nearest ancestor directory of the
 * test's working directory that holds `gradlew`) rather than the module the test runs in, so one
 * scope already sees every Gradle module and every KMP source set — `commonMain`, `jvmMain`,
 * `jsMain`, `commonTest`, plain `main`/`test`, … [projectFiles] narrows that raw scope down to the
 * repository's own modules; see its doc for what is dropped and why.
 *
 * The three existing `cream-ksp` architecture specs keep their own narrower
 * `Konsist.scopeFromProduction(moduleName = "cream-ksp", sourceSetName = "main")` scope and are not
 * affected by this module.
 * TODO: once repository-wide rules land here, decide whether those three specs should move into
 *  this module (they would then filter [projectFiles] down to `:cream-ksp` / `main`).
 */
internal object ProjectScope {
    /**
     * Gradle project paths (`:cream-ksp:shared` style) read straight out of `settings.gradle.kts`,
     * so the scope's definition of "our modules" has a single source of truth and cannot drift when
     * a module is added or removed.
     *
     * `includeBuild("./buildLogic")` deliberately does not match [INCLUDE_REGEX]: that included
     * build is build configuration rather than product code, and Konsist cannot skip it on its own
     * (its `ignoreBuildConfig` flag only knows the conventional `buildSrc` directory name).
     */
    val includedProjectPaths: List<String> by lazy {
        File(Konsist.projectRootPath, "settings.gradle.kts")
            .readLines()
            .mapNotNull { line -> INCLUDE_REGEX.find(line)?.groupValues?.get(1) }
            .also { check(it.isNotEmpty()) { "No include(\":…\") found in settings.gradle.kts" } }
    }

    /**
     * Every Kotlin source file of the repository's own modules, across all of their source sets.
     *
     * Konsist's raw project scope is wider than "our source", so it is narrowed to files whose
     * module is one of [includedProjectPaths]. An allow-list is used rather than a deny-list
     * because the raw scope contains anything Kotlin-shaped that happens to sit in the working
     * tree: this repository's git-ignored `.local/` holds whole scratch Gradle projects with real
     * `src/<sourceSet>/` layouts (release verification consumers, KSP probes, cloned repositories),
     * which are indistinguishable from first-party modules by path shape alone.
     *
     * Generated code needs no filter: Konsist unconditionally skips `build` (and Maven `target`)
     * directories at the project root and inside any module, plus the root `.gradle` directory.
     * That already covers KSP output such as the `test` module's
     * `build/generated/ksp/metadata/commonMain/kotlin` — a directory wired into `commonMain` as an
     * extra source dir, whose files nonetheless resolve under `build` and are therefore never
     * parsed.
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
     * Konsist names a nested module by its directory path relative to the project root
     * (`cream-ksp/shared`), not by its Gradle project path. This converts back to the
     * `:cream-ksp:shared` form used in `settings.gradle.kts`, so rules can be written against the
     * names contributors actually know.
     */
    val KoFileDeclaration.gradleProjectPath: String
        get() = ":" + moduleName.replace(File.separatorChar, ':')

    /**
     * Konsist's own production/test split (the one behind `scopeFromProduction` /`scopeFromTest`)
     * keys off the source-set name containing `test`, case-insensitively: `main` / `commonMain` /
     * `jvmMain` are production, `test` / `commonTest` / `androidUnitTest` are not. Reproduced here
     * so both halves derive from the single [projectFiles] parse instead of building a second scope.
     */
    val KoFileDeclaration.isInTestSourceSet: Boolean
        get() = sourceSetName.lowercase().contains("test")

    /** `:cream-ksp:shared` -> `cream-ksp/shared` (Konsist's [KoFileDeclaration.moduleName] form). */
    private fun String.toKonsistModuleName(): String = removePrefix(":").replace(':', File.separatorChar)

    /** Matches `include(":cream-ksp:shared")` but not `includeBuild("./buildLogic")`. */
    private val INCLUDE_REGEX = Regex("""^\s*include\(\s*"(:[^"]+)"\s*\)""")
}
