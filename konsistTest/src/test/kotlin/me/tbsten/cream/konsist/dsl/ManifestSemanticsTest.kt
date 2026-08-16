package me.tbsten.cream.konsist.dsl

import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
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

            "required = true にパターンはディレクトリ部分でも書けない" {
                shouldThrow<IllegalArgumentException> {
                    manifest {
                        module(":cream-ksp").sourceSet("main").ktFile("*/A.kt", required = true) { }
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

            // 題材は実ファイルだが、**パスではなく性質で選ぶ**。特定のパスに結び付けると、
            // 規約上は自由なはずのリネーム・移動・行数変更でこの spec が落ちてしまう。
            val scopeRoot = "cream-ksp/src/main/$kspDir/"
            val scopeFiles =
                ProjectScope.projectFiles
                    .filter { it.normalizedProjectPath.startsWith(scopeRoot) }
                    .sortedBy { it.normalizedProjectPath }

            fun relativeOf(file: KoFileDeclaration) = file.normalizedProjectPath.removePrefix(scopeRoot)

            fun fixtures(
                description: String,
                count: Int,
                predicate: (KoFileDeclaration) -> Boolean,
            ) = scopeFiles.filter(predicate).also {
                check(it.size >= count) {
                    "題材にできるファイルが $count 個必要だが ${it.size} 個しかない: $description — " +
                        "manifest 側の違反ではない。ManifestSemanticsTest の題材の取り方を見直すこと。"
                }
            }

            fun fixture(
                description: String,
                predicate: (KoFileDeclaration) -> Boolean,
            ) = fixtures(description, count = 1, predicate).first()

            val utilPlain =
                fixtures("util 直下・import ゼロ・$DEFAULT_MAX_LINES 行以内", count = 2) { file ->
                    val relative = relativeOf(file)
                    relative.startsWith("util/") &&
                        relative.count { it == '/' } == 1 &&
                        file.imports.isEmpty() &&
                        file.text.lines().size <= DEFAULT_MAX_LINES
                }
            val utilA = utilPlain[0]
            val utilB = utilPlain[1]
            val utilKspFile =
                fixture("util/ksp 配下・KSP API と kotlin.reflect を import") { file ->
                    relativeOf(file).startsWith("util/ksp/") &&
                        file.imports.any { it.name.startsWith("com.google.devtools.ksp") } &&
                        file.imports.any { it.name.startsWith("kotlin.reflect.") }
                }
            val outsideUtil = fixture("util 配下でないファイル") { !relativeOf(it).startsWith("util/") }
            val overLimit =
                fixture("core/common 配下・$DEFAULT_MAX_LINES 行超") {
                    relativeOf(it).startsWith("core/common/") && it.text.lines().size > DEFAULT_MAX_LINES + 1
                }

            "配置: manifest に列挙されていないファイルは Failure（deny by default）" {
                val compiled = manifestOf { dir("util") { anyFiles() } }
                val rogue = compiled.assert(outsideUtil)
                rogue.shouldBeInstanceOf<AssertResult.Failure>()
                withClue(rogue.because.joinToString()) {
                    rogue.because.any { "配置" in it }.shouldBeTrue()
                }
            }

            "配置: パターンの `*` は `/` を跨がない（直下パッケージだけを指せる）" {
                val compiled = manifestOf { dir("util") { ktFile("*/*.kt") { anyTopLevel() } } }
                // util/ksp/… は直下パッケージなのでエントリの対象（違反理由は import であって配置ではない）
                val nested = compiled.assert(utilKspFile)
                nested.shouldBeInstanceOf<AssertResult.Failure>()
                withClue(nested.because.joinToString()) {
                    nested.because.none { "配置" in it }.shouldBeTrue()
                }
                // util 直下は `*/*.kt` に一致しない = 配置違反
                val outside = compiled.assert(utilA)
                outside.shouldBeInstanceOf<AssertResult.Failure>()
                withClue(outside.because.joinToString()) {
                    outside.because.any { "配置" in it }.shouldBeTrue()
                }
            }

            "配置: 具体名エントリはパターンより常に勝つ（書き順に依存しない）" {
                // わざと実態に合わない grant を持つ具体名エントリを「パターンの後」に書く。
                // 具体名が勝つなら String.kt はその grant で Failure になる（パターンに飲まれれば Ok になってしまう）。
                val compiled =
                    manifestOf {
                        dir("util") {
                            ktFile("*.kt") { anyTopLevel() }
                            ktFile(utilA.nameWithExtension) { topLevelClass("存在しないクラス") }
                        }
                    }
                compiled.assert(utilA).shouldBeInstanceOf<AssertResult.Failure>()
                compiled.assert(utilB) shouldBe AssertResult.Ok
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
                compiled.assert(utilA) shouldBe AssertResult.Ok
                val denied = compiled.assert(utilKspFile)
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
                compiled.assert(utilKspFile) shouldBe AssertResult.Ok
            }

            "maxLines: 既定 ${DEFAULT_MAX_LINES} 行を超えると Failure、entry の maxLines で緩和できる" {
                val strict = manifestOf { dir("core") { dir("common") { ktFile("*.kt") { anyTopLevel() } } } }
                val over = strict.assert(overLimit)
                over.shouldBeInstanceOf<AssertResult.Failure>()
                withClue(over.because.joinToString()) {
                    over.because.any { "行" in it }.shouldBeTrue()
                }

                val relaxed =
                    manifestOf {
                        dir("core") {
                            dir("common") {
                                ktFile(overLimit.nameWithExtension) {
                                    // 実ファイルの行数に依存しないよう、題材より必ず大きい上限を使う
                                    maxLines(overLimit.text.lines().size + 1)
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
                relaxed.assert(overLimit) shouldBe AssertResult.Ok
            }

            "grant: 列挙されていないトップレベル宣言は Failure（only 意味論）" {
                val compiled =
                    manifestOf {
                        dir("util") {
                            // String.kt の実態は internal fun 群。あえて別名だけ grant する
                            ktFile(utilA.nameWithExtension) { topLevelClass("Nothing") }
                            ktFile("*.kt") { anyTopLevel() }
                            dir("ksp") { anyFiles() }
                        }
                    }
                compiled.assert(utilA).shouldBeInstanceOf<AssertResult.Failure>()
            }

            "required = true は requiredPaths に集約される（存在検査は spec の集合レベル）" {
                val compiled =
                    manifestOf {
                        dir("util") {
                            ktFile(utilA.nameWithExtension, required = true) { anyTopLevel() }
                        }
                    }
                compiled.requiredPaths shouldContain utilA.normalizedProjectPath
            }
        }
    })
