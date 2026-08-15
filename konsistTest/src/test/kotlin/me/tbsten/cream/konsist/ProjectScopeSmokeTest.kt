package me.tbsten.cream.konsist

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import me.tbsten.cream.konsist.support.ProjectScope
import me.tbsten.cream.konsist.support.ProjectScope.gradleProjectPath
import me.tbsten.cream.konsist.support.ProjectScope.isInTestSourceSet
import java.io.File

/**
 * Smoke test for [ProjectScope]: it pins *what the scope sees*, not any architecture rule.
 *
 * Repository-wide guardrails (which files an agent may edit, what shape a given file's top-level
 * declarations must have, …) are only worth writing once the scope is trustworthy, so this spec
 * fixes both halves of that: every module of the build is reachable, and nothing that merely looks
 * like source code (generated output, build configuration, scratch projects) leaks in.
 *
 * TODO: real rules go in sibling specs of this module. The three `cream-ksp`-local architecture
 *  specs (`AllKotlinFilesTest`, `feature/ArchTest`, `core/ArchTest`) stay where they are for now.
 */
internal class ProjectScopeSmokeTest :
    FreeSpec(
        {
            "scope はリポジトリ全体（全モジュール / 全 source set）を見ている" - {
                "settings.gradle.kts が include する全モジュールの Kotlin ファイルが含まれる" {
                    val modulesInScope =
                        ProjectScope.projectFiles
                            .map { it.gradleProjectPath }
                            .toSet()

                    val modulesWithoutFiles = ProjectScope.includedProjectPaths.filterNot { it in modulesInScope }
                    modulesWithoutFiles shouldBe emptyList()
                }

                "JVM の main / test も KMP の commonMain / commonTest / jvmMain / jsMain も含まれる" {
                    // KSP does not support intermediate source sets, but Konsist reads the file
                    // system directly, so `commonMain` and friends are ordinary source sets to it.
                    val sourceSets =
                        ProjectScope.projectFiles
                            .map { it.sourceSetName }
                            .toSet()

                    sourceSets shouldContainAll
                        setOf("main", "test", "commonMain", "commonTest", "jvmMain", "jsMain")
                }

                "production / test の両方の source set が含まれる" {
                    ProjectScope.productionFiles.isNotEmpty() shouldBe true
                    ProjectScope.testFiles.isNotEmpty() shouldBe true
                    ProjectScope.productionFiles.none { it.isInTestSourceSet } shouldBe true
                }
            }

            "scope から外れるもの" - {
                "build 配下（KSP が生成したコード）は含まれない" {
                    // Konsist skips build directories unconditionally. This matters most for the
                    // `test` module, which wires build/generated/ksp/metadata/commonMain/kotlin into
                    // `commonMain` as an extra source dir: rules must never fire on generated code.
                    val underBuildDir =
                        ProjectScope.projectFiles
                            .map { it.projectPath }
                            .filter { it.contains("${File.separator}build${File.separator}") }

                    underBuildDir shouldBe emptyList()
                }

                "buildLogic（included build のビルド設定）は含まれない" {
                    // `includeBuild("./buildLogic")` is not an `include(":…")`, so ProjectScope's
                    // allow-list drops it. Konsist's own `ignoreBuildConfig` would not: it only
                    // recognises the conventional `buildSrc` directory name.
                    val buildLogicFiles =
                        ProjectScope.projectFiles
                            .map { it.projectPath }
                            .filter { it.startsWith("${File.separator}buildLogic${File.separator}") }

                    buildLogicFiles shouldBe emptyList()
                }

                "include されていないディレクトリのファイルは含まれない" {
                    // The raw project scope contains every Kotlin file in the working tree, including
                    // the git-ignored `.local/` scratch Gradle projects that have real
                    // `src/<sourceSet>/` layouts. Only paths under an included module survive.
                    val allowedPrefixes =
                        ProjectScope.includedProjectPaths.map { projectPath ->
                            File.separator + projectPath.removePrefix(":").replace(':', File.separatorChar) + File.separator
                        }

                    val outsideIncludedModules =
                        ProjectScope.projectFiles
                            .map { it.projectPath }
                            .filterNot { path -> allowedPrefixes.any { path.startsWith(it) } }

                    outsideIncludedModules shouldBe emptyList()
                }
            }
        },
    )
