package me.tbsten.cream.konsist

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import me.tbsten.cream.konsist.support.ProjectScope
import me.tbsten.cream.konsist.support.ProjectScope.gradleProjectPath
import java.io.File

/**
 * Smoke test for [ProjectScope]: it pins *what the scope sees*, not any architecture rule — every
 * module of the build is reachable, and nothing that merely looks like source code (generated
 * output, build configuration, scratch projects) leaks in.
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

                "JVM モジュールの main / test も KMP モジュールの commonMain / commonTest も含まれる" {
                    // Pinned per module rather than as one flat set, so a name that stops appearing
                    // points at the module that lost it.
                    val sourceSetsByModule =
                        ProjectScope.projectFiles
                            .groupBy({ it.gradleProjectPath }, { it.sourceSetName })
                            .mapValues { (_, sourceSetNames) -> sourceSetNames.toSet() }

                    sourceSetsByModule shouldBe
                        mapOf(
                            // Konsist reads the file system directly, so KMP's intermediate source
                            // sets are visible to it even though KSP cannot process them.
                            ":cream-runtime" to setOf("commonMain"),
                            ":test" to setOf("commonMain", "commonTest"),
                            ":cream-ksp" to setOf("main", "test"),
                            ":konsistTest" to setOf("test"),
                        )
                }

                "production / test の両方の source set が含まれる" {
                    // What can break is the substring heuristic's verdict on our real source-set
                    // names (see ProjectScope.isInTestSourceSet), so that verdict is what is pinned.
                    ProjectScope.productionFiles.map { it.sourceSetName }.toSet() shouldBe
                        setOf("commonMain", "main")
                    ProjectScope.testFiles.map { it.sourceSetName }.toSet() shouldBe
                        setOf("commonTest", "test")
                }
            }

            "scope から外れるもの" - {
                "build 配下（KSP が生成したコード）は含まれない" {
                    // The `test` module wires build/generated/ksp/… into `commonMain` as an extra
                    // source dir: rules must never fire on generated code.
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
                    // Guards the allow-list against `.local/` scratch projects with real
                    // `src/<sourceSet>/` layouts (see ProjectScope.projectFiles).
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
