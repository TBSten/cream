package me.tbsten.cream.konsist.dsl

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import me.tbsten.cream.konsist.support.ProjectScope

/**
 * DSL・rule の意味論の pin。題材は cream-ksp の実ファイル（scope 経由）。
 *
 * @see manifest DSL 意味論の大筋
 * @see ManifestAssertionRule rule の代数の大筋
 */
internal class ManifestSemanticsTest :
    FreeSpec({
        "AssertResult の合成" - {
            val failureA = AssertResult.Failure(listOf("a"))
            val failureB = AssertResult.Failure(listOf("b"))

            "全 9 組み合わせ（NoEffect が単位元 / Failure は結合 / Ok は Failure に負ける）" {
                (AssertResult.NoEffect + AssertResult.NoEffect) shouldBe AssertResult.NoEffect
                (AssertResult.NoEffect + AssertResult.Ok) shouldBe AssertResult.Ok
                (AssertResult.NoEffect + failureA) shouldBe failureA
                (AssertResult.Ok + AssertResult.NoEffect) shouldBe AssertResult.Ok
                (AssertResult.Ok + AssertResult.Ok) shouldBe AssertResult.Ok
                (AssertResult.Ok + failureA) shouldBe failureA
                (failureA + AssertResult.NoEffect) shouldBe failureA
                (failureA + AssertResult.Ok) shouldBe failureA
                (failureA + failureB) shouldBe AssertResult.Failure(listOf("a", "b"))
            }

            "finalize は未確定（NoEffect）だけを onNoEffect で確定させる" {
                val placement = AssertResult.Failure(listOf("配置違反"))
                AssertResult.NoEffect.finalize { placement } shouldBe placement
                AssertResult.Ok.finalize { placement } shouldBe AssertResult.Ok
                failureA.finalize { placement } shouldBe failureA
            }
        }

        "構築時ガード（不完全な manifest はテスト実行前に落ちる）" - {
            "required = true にパターンは書けない" {
                shouldThrow<IllegalArgumentException> {
                    manifest {
                        module(":cream-ksp").sourceSet("main").ktFile("*.kt", required = true) { }
                    }
                }
            }

            "パターンはファイル名部分にだけ書ける" {
                shouldThrow<IllegalArgumentException> {
                    manifest {
                        module(":cream-ksp").sourceSet("main").ktFile("*/A.kt") { }
                    }
                }
            }

            "同じパスの ktFile エントリの重複は例外" {
                shouldThrow<IllegalArgumentException> {
                    manifest {
                        module(":cream-ksp").sourceSet("main").apply {
                            ktFile("A.kt") { }
                            ktFile("A.kt") { }
                        }
                    }
                }
            }

            "ktFile エントリと dir エントリの重なりは例外" {
                shouldThrow<IllegalArgumentException> {
                    manifest {
                        module(":cream-ksp").sourceSet("main").apply {
                            dir("some") { anyFiles() }
                            ktFile("some/A.kt") { }
                        }
                    }
                }
            }

            "maxLines はエントリより前に宣言する（後置は黙って効かない事故のもと）" {
                shouldThrow<IllegalStateException> {
                    manifest {
                        module(":cream-ksp").sourceSet("main").apply {
                            anyFiles()
                            maxLines(100)
                        }
                    }
                }
            }

            "imports は 1 つの ktFile に 1 回だけ" {
                shouldThrow<IllegalStateException> {
                    manifest {
                        module(":cream-ksp").sourceSet("main").ktFile("A.kt") {
                            imports { packageTree("kotlin") }
                            imports { packageTree("kotlin") }
                        }
                    }
                }
            }

            "topLevels に全可視性の set は書けない（anyTopLevel を明示する）" {
                shouldThrow<IllegalArgumentException> {
                    manifest {
                        module(":cream-ksp").sourceSet("main").ktFile("A.kt") {
                            topLevels(*TopLevelVisibility.entries.toTypedArray())
                        }
                    }
                }
            }

            "パターン grant は絞り込み軸を最低 1 つ要求する" {
                shouldThrow<IllegalArgumentException> {
                    manifest {
                        module(":cream-ksp").sourceSet("main").ktFile("A.kt") {
                            topLevelFunctions()
                        }
                    }
                }
            }
        }

        "rule の挙動（題材: cream-ksp の実ファイル）" - {
            val kspDir = "kotlin/me/tbsten/cream/ksp"

            fun manifestOf(block: ManifestDirBuilder.() -> Unit) = manifest { module(":cream-ksp").sourceSet("main").dir(kspDir, block) }

            fun fileOf(relative: String) =
                ProjectScope.projectFiles.first {
                    it.normalizedProjectPath == "cream-ksp/src/main/$kspDir/$relative"
                }

            "配置: manifest に列挙されていないファイルは Failure（deny by default）" {
                val compiled = manifestOf { dir("util") { anyFiles() } }
                val rogue = compiled.assert(fileOf("options/CreamOptions.kt"))
                rogue.shouldBeInstanceOf<AssertResult.Failure>()
                withClue(rogue.because.joinToString()) {
                    rogue.because.any { "配置" in it }.shouldBeTrue()
                }
            }

            "配置: 具体名エントリはパターンより常に勝つ（書き順に依存しない）" {
                // わざと実態に合わない grant を持つ具体名エントリを「パターンの後」に書く。
                // 具体名が勝つなら String.kt はその grant で Failure になる（パターンに飲まれれば Ok になってしまう）。
                val compiled =
                    manifestOf {
                        dir("util") {
                            ktFile("*.kt") { anyTopLevel() }
                            ktFile("String.kt") { topLevelClass("存在しないクラス") }
                        }
                    }
                compiled.assert(fileOf("util/String.kt")).shouldBeInstanceOf<AssertResult.Failure>()
                compiled.assert(fileOf("util/With.kt")) shouldBe AssertResult.Ok
            }

            "import: ktFile に imports が無ければ import ゼロのみ許可" {
                val compiled =
                    manifestOf {
                        dir("util") {
                            dir("ksp") {
                                // KSAnnotationArgument.kt は KSP API 等を import している → 全 deny に当たる
                                ktFile("*.kt") { anyTopLevel() }
                            }
                            ktFile("*.kt") { anyTopLevel() }
                        }
                    }
                // util 直下は import ゼロの実態なので通る
                compiled.assert(fileOf("util/String.kt")) shouldBe AssertResult.Ok
                val denied = compiled.assert(fileOf("util/ksp/KSAnnotationArgument.kt"))
                denied.shouldBeInstanceOf<AssertResult.Failure>()
                withClue(denied.because.joinToString()) {
                    denied.because.any { "import" in it }.shouldBeTrue()
                }
            }

            "import: 許可は ktFile の imports の列挙だけで決まる（列挙の union）" {
                val compiled =
                    manifestOf {
                        dir("util") {
                            dir("ksp") {
                                ktFile("*.kt") {
                                    imports {
                                        packageTree("com.google.devtools.ksp")
                                        packageEquals("kotlin.reflect")
                                    }
                                    anyTopLevel()
                                }
                            }
                        }
                    }
                // KSAnnotationArgument.kt = KSP API + kotlin.reflect の両方を import
                // → 列挙した許可の union に収まって初めて通る
                compiled.assert(fileOf("util/ksp/KSAnnotationArgument.kt")) shouldBe AssertResult.Ok
            }

            "maxLines: 既定 ${DEFAULT_MAX_LINES} 行を超えると Failure、entry の maxLines で緩和できる" {
                val strict = manifestOf { dir("core") { dir("common") { ktFile("*.kt") { anyTopLevel() } } } }
                val over = strict.assert(fileOf("core/common/FindMatchedProperty.kt"))
                over.shouldBeInstanceOf<AssertResult.Failure>()
                withClue(over.because.joinToString()) {
                    over.because.any { "行" in it }.shouldBeTrue()
                }

                val relaxed =
                    manifestOf {
                        dir("core") {
                            dir("common") {
                                ktFile("FindMatchedProperty.kt") {
                                    maxLines(500)
                                    // fixture の関心は maxLines だけなので import は広めに許可する
                                    imports {
                                        packageTree("me.tbsten.cream")
                                        packageTree("com.google.devtools.ksp")
                                        packageTree("kotlin")
                                    }
                                    anyTopLevel()
                                }
                            }
                        }
                    }
                relaxed.assert(fileOf("core/common/FindMatchedProperty.kt")) shouldBe AssertResult.Ok
            }

            "grant: 列挙されていないトップレベル宣言は Failure（only 意味論）" {
                val compiled =
                    manifestOf {
                        dir("util") {
                            // String.kt の実態は internal fun 群。あえて別名だけ grant する
                            ktFile("String.kt") { topLevelClass("Nothing") }
                            ktFile("*.kt") { anyTopLevel() }
                            dir("ksp") { anyFiles() }
                        }
                    }
                compiled.assert(fileOf("util/String.kt")).shouldBeInstanceOf<AssertResult.Failure>()
            }

            "required = true は requiredPaths に集約される（存在検査は spec の集合レベル）" {
                val compiled =
                    manifestOf {
                        dir("util") {
                            ktFile("String.kt", required = true) { anyTopLevel() }
                        }
                    }
                compiled.requiredPaths shouldContain "cream-ksp/src/main/$kspDir/util/String.kt"
            }
        }
    })
