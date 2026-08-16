package me.tbsten.cream.konsist.dsl

import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.konsist.support.ProjectScope

/**
 * manifest DSL の意味論を、実在する :konsistTest モジュール自身のファイルを題材に固定する。
 * deny by default が配置（ツリー）と中身（grant）の**両方**に効くことが中核不変条件:
 * 「manifest に書いてないことは書けない」。
 *
 * cream-ksp の manifest 本体（`manifest/CreamKspManifest.kt`）とは独立に、DSL 実装のバグや
 * 意味論の退行をここで検出する。ここで宣言する probe manifest は検証専用で、
 * :konsistTest の実際の配置ルールを定めるものではない（違反もわざと作っている）。
 */
internal class ManifestSemanticsTest :
    FreeSpec({
        // 配置（ツリー）意味論の probe では中身は検査対象にしないため、全緩和を渡す
        val anything = fileContentManifest { anyTopLevel() }
        val probe =
            manifest {
                module(":konsistTest").sourceSet("test").dir("kotlin/me/tbsten/cream/konsist") {
                    requiredFile("CreamKspManifestTest.kt") { assertContent(anything) }
                    // 実在しない requiredFile → missing として検出されるはず
                    requiredFile("support/Ghost.kt") { assertContent(anything) }
                    file("CreamKspDependencyTest.kt") {
                        maxLines(10)
                        assertContent(anything)
                    }
                    dir("dsl") {
                        maxLines(42)
                        file("ManifestDsl.kt") { assertContent(anything) }
                        file("ManifestEvaluation.kt") {
                            maxLines(7)
                            assertContent(anything)
                        }
                        // FileContentManifest.kt / ManifestSemanticsTest.kt は意図的に列挙しない
                        // → 閉じた dir の deny by default
                    }
                    // 配置フリー（中身も自由）は明示的な anyFiles() でのみ許可されるはず
                    dir("manifest") { anyFiles() }
                    // 空 block の dir → 配下に .kt を 1 つも置けないはず（ProjectScope.kt が違反になる）
                    dir("support") { }
                    // 同じパスの再宣言。宣言順で最初のエントリ（maxLines = 10）が勝つはず
                    file("CreamKspDependencyTest.kt") {
                        maxLines(999)
                        assertContent(anything)
                    }
                    // ProjectScopeSmokeTest.kt はどのエントリにも一致しない → root で違反
                }
            }
        val verdict = probe.evaluate(ProjectScope.projectFiles).single()

        fun matched(fileName: String): FileVerdict.Matched = verdict.matched.first { it.file.nameWithExtension == fileName }

        fun fileOf(relativePath: String): KoFileDeclaration = ProjectScope.projectFiles.first { it.normalizedProjectPath == BASE_PREFIX + relativePath }

        "配置（ツリー）の意味論" - {
            "管轄区域は module + sourceSet のパス接頭辞で決まり、全ファイルが分類される" {
                val scopeCount =
                    ProjectScope.projectFiles.count { it.moduleName == "konsistTest" && it.sourceSetName == "test" }
                withClue("scope 実測 $scopeCount ファイル / manifest が分類 ${verdict.files.size} ファイル") {
                    verdict.files shouldHaveSize scopeCount
                }
                verdict.files.forEach { fileVerdict ->
                    withClue("${fileVerdict.file.projectPath} は管轄区域（$BASE_PREFIX…）にあるはず") {
                        fileVerdict.file.normalizedProjectPath.startsWith("konsistTest/src/test/") shouldBe true
                    }
                }
            }

            "file / requiredFile は完全一致で許可し、宣言順で最初のエントリが勝つ" {
                withClue("後から再宣言した maxLines = 999 ではなく、最初のエントリの 10 が適用されるはず") {
                    matched("CreamKspDependencyTest.kt").maxLines shouldBe 10
                }
            }

            "file エントリ経由の一致は中身の目録（content）を持ち帰る" {
                matched("ManifestDsl.kt").content shouldBe anything
            }

            "requiredFile が存在しなければ missing として報告される" {
                verdict.missingRequiredFiles shouldBe listOf(BASE_PREFIX + "support/Ghost.kt")
            }

            "エントリを持つ dir の配下は deny by default（後続の兄弟エントリに fallback しない）" {
                val violation = verdict.violations.first { it.file.nameWithExtension == "ManifestSemanticsTest.kt" }
                violation.deniedAt shouldBe BASE_PREFIX + "dsl/"
            }

            "空 block の dir は配下に .kt を 1 つも置けない（暗黙の配置フリーはない）" {
                val violation = verdict.violations.first { it.file.nameWithExtension == "ProjectScope.kt" }
                violation.deniedAt shouldBe BASE_PREFIX + "support/"
            }

            "anyFiles() だけが配置フリーになり、中身も検査しない（content = null）" {
                val viaAnyFiles = matched("CreamKspManifest.kt")
                viaAnyFiles.entryPath shouldBe BASE_PREFIX + "manifest/"
                viaAnyFiles.maxLines shouldBe DEFAULT_MAX_LINES
                viaAnyFiles.content.shouldBeNull()
            }

            "dir の maxLines は配下の既定になり、file エントリの maxLines が優先される" {
                withClue("dir(\"dsl\") { maxLines(42) } を継承するはず") {
                    matched("ManifestDsl.kt").maxLines shouldBe 42
                }
                withClue("file エントリの maxLines = 7 が dir の 42 より優先されるはず") {
                    matched("ManifestEvaluation.kt").maxLines shouldBe 7
                }
            }

            "どのエントリにも一致しないファイルは違反（deny by default）" {
                val violationNames = verdict.violations.map { it.file.nameWithExtension }
                violationNames shouldContain "ProjectScopeSmokeTest.kt"
                val rootViolation = verdict.violations.first { it.file.nameWithExtension == "ProjectScopeSmokeTest.kt" }
                rootViolation.deniedAt shouldBe BASE_PREFIX
            }
        }

        "中身（grant）の意味論" - {
            "grant に完全一致するファイルは違反なし" {
                val content = fileContentManifest { topLevelObject("ProjectScope") }
                content.findViolations(fileOf("support/ProjectScope.kt")).shouldBeEmpty()
            }

            "どの grant にも一致しない宣言は違反（空の fileContentManifest = 宣言ゼロのみ許可）" {
                val violations = fileContentManifest { }.findViolations(fileOf("support/ProjectScope.kt"))
                violations shouldHaveSize 1
                violations.first() shouldContain "internal object ProjectScope"
            }

            "可視性が違う宣言は grant に一致しない（grant 外と必須未充足の両方が報告される）" {
                val content = fileContentManifest { topLevelObject("ProjectScope", visibility = TopLevelVisibility.Private) }
                val violations = content.findViolations(fileOf("support/ProjectScope.kt"))
                violations shouldHaveSize 2
                withClue(violations.joinToString("\n")) {
                    violations.count { "どの grant にも一致しない" in it } shouldBe 1
                    violations.count { "1 個のはずが 0 個" in it } shouldBe 1
                }
            }

            "必須のパターン grant は一致 0 件で違反になる" {
                val content =
                    fileContentManifest {
                        topLevelFunctions(nameStartsWith = "process", visibility = TopLevelVisibility.Internal, required = true)
                        anyTopLevel()
                    }
                val violations = content.findViolations(fileOf("support/ProjectScope.kt"))
                violations shouldHaveSize 1
                violations.first() shouldContain "必須の grant"
            }

            "count は個数まで固定する（実測と一致しなければ違反）" {
                val content =
                    fileContentManifest {
                        topLevelFunction("manifest", count = 2)
                        anyTopLevel()
                    }
                val violations = content.findViolations(fileOf("dsl/ManifestDsl.kt"))
                violations shouldHaveSize 1
                violations.first() shouldContain "2 個のはずが 1 個"
            }

            "privateTopLevels() は private 宣言だけを任意個許可する明示的な緩和" {
                val file = fileOf("CreamKspManifestTest.kt")
                withClue("private 以外（internal class）は grant 外として違反になるはず") {
                    fileContentManifest { privateTopLevels() }.findViolations(file) shouldHaveSize 1
                }
                withClue("internal class に個別 grant を足せば違反ゼロになるはず") {
                    fileContentManifest {
                        topLevelClass("CreamKspManifestTest")
                        privateTopLevels()
                    }.findViolations(file).shouldBeEmpty()
                }
            }

            "ファイルアノテーション（@file:）はトップレベル宣言として数えない" {
                // cream-ksp/test の実在ファイル（@file:OptIn 付き・トップレベルは data class 1 つ）を題材にする
                val file =
                    ProjectScope.projectFiles.first {
                        it.normalizedProjectPath == "cream-ksp/src/test/kotlin/me/tbsten/cream/ksp/testing/compile/CreamCompilationResult.kt"
                    }
                fileContentManifest { topLevelClass("CreamCompilationResult") }.findViolations(file).shouldBeEmpty()
            }

            "anyTopLevel() はすべての宣言を許可する明示的な全緩和" {
                fileContentManifest { anyTopLevel() }
                    .findViolations(fileOf("dsl/FileContentManifest.kt"))
                    .shouldBeEmpty()
            }

            "enum class は class とは別の宣言種（topLevelClass に一致せず、topLevelEnumClass に一致する）" {
                // dsl/FileContentManifest.kt の internal enum class TopLevelVisibility が題材
                val file = fileOf("dsl/FileContentManifest.kt")
                withClue("topLevelClass は enum class に一致しないはず（grant 外 + 個数不一致の両方が出る）") {
                    val violations =
                        fileContentManifest {
                            topLevelClass("TopLevelVisibility")
                            anyTopLevel()
                        }.findViolations(file)
                    violations shouldHaveSize 1
                    violations.first() shouldContain "1 個のはずが 0 個"
                }
                withClue("topLevelEnumClass は enum class に一致するはず") {
                    fileContentManifest {
                        topLevelEnumClass("TopLevelVisibility")
                        anyTopLevel()
                    }.findViolations(file).shouldBeEmpty()
                }
            }
        }

        "不完全な manifest は構築時に落ちる" - {
            "file の block で assertContent を呼ばないと即例外（テスト実行前に落とす）" {
                val exception =
                    shouldThrow<IllegalStateException> {
                        manifest {
                            module(":ghost").sourceSet("test").dir("kotlin") {
                                file("Incomplete.kt") { }
                            }
                        }
                    }
                exception.message shouldContain "content manifest 未指定"
                exception.message shouldContain "Incomplete.kt"
            }

            "assertContent を 2 回呼ぶのも即例外" {
                val exception =
                    shouldThrow<IllegalStateException> {
                        manifest {
                            module(":ghost").sourceSet("test").dir("kotlin") {
                                file("Doubled.kt") {
                                    assertContent(anything)
                                    assertContent(anything)
                                }
                            }
                        }
                    }
                exception.message shouldContain "1 回だけ"
            }

            "パターン grant の全軸省略（実質 anyTopLevel）は即例外" {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        fileContentManifest { topLevelFunctions() }
                    }
                exception.message shouldContain "anyTopLevel()"
            }
        }
    })

private const val BASE_PREFIX = "konsistTest/src/test/kotlin/me/tbsten/cream/konsist/"
