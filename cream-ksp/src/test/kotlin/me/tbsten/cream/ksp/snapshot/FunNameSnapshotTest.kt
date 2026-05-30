package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText

internal class FunNameSnapshotTest :
    FunSpec({
        test("funName plain literal overrides the generated name") {
            val source =
                """
                package snap.funname

                import me.tbsten.cream.CopyTo

                @CopyTo(Target::class, funName = "toState")
                data class Source(
                    val shared: String,
                )

                data class Target(
                    val shared: String,
                    val extra: Int,
                )
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue("expected the overridden name, but got:\n$generated") {
                generated shouldContain ".toState("
                generated shouldNotContain ".copyToTarget("
            }
            assertMatchesSnapshot("FunNameSnapshotTest.literalOverride") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }

        test("omitting funName keeps cream's derived name") {
            val source =
                """
                package snap.funname

                import me.tbsten.cream.CopyTo

                @CopyTo(Target::class)
                data class Source(
                    val shared: String,
                )

                data class Target(
                    val shared: String,
                    val extra: Int,
                )
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue("expected cream's derived name, but got:\n$generated") {
                generated shouldContain ".copyToTarget("
                generated shouldNotContain "{{cream"
            }
            assertMatchesSnapshot("FunNameSnapshotTest.defaultUnchanged") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }

        // KSP constant-folding gate: `"make" + CopyTargetSimpleName` must reach the processor
        // as a single folded string ("make{{cream:CopyTargetSimpleName}}"). If KSP does not
        // fold it, this test fails (no ".makeTarget(", or a leftover "{{cream" placeholder),
        // and implementation must stop. The "make" prefix is chosen so the default name
        // "copyToTarget" cannot accidentally satisfy the assertion.
        test("funName with Pascal simple-name token expands to the target name") {
            val source =
                """
                package snap.funname

                import me.tbsten.cream.CopyTo
                import me.tbsten.cream.CopyTargetSimpleName

                @CopyTo(Target::class, funName = "make" + CopyTargetSimpleName)
                data class Source(
                    val shared: String,
                )

                data class Target(
                    val shared: String,
                    val extra: Int,
                )
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue("KSP constant-folding gate — generated:\n$generated") {
                generated shouldContain ".makeTarget("
                generated shouldNotContain "{{cream"
            }
            assertMatchesSnapshot("FunNameSnapshotTest.tokenSimpleNamePascal") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }

        test("funName with snake simple-name token expands to the lower-cased target name") {
            val source =
                """
                package snap.funname

                import me.tbsten.cream.CopyTo
                import me.tbsten.cream.copy_target_simple_name

                @CopyTo(Target::class, funName = "make_" + copy_target_simple_name)
                data class Source(
                    val shared: String,
                )

                data class Target(
                    val shared: String,
                    val extra: Int,
                )
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue("expected the snake-cased token expansion, but got:\n$generated") {
                generated shouldContain ".make_target("
                generated shouldNotContain "{{cream"
            }
            assertMatchesSnapshot("FunNameSnapshotTest.tokenSimpleNameSnake") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }

        test("@CopyFrom honours a funName token") {
            val source =
                """
                package snap.funname

                import me.tbsten.cream.CopyFrom
                import me.tbsten.cream.CopyTargetSimpleName

                data class Source(val shared: String)

                @CopyFrom(Source::class, funName = "to" + CopyTargetSimpleName)
                data class Target(val shared: String, val extra: Int)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue(generated) {
                generated shouldContain ".toTarget("
                generated shouldNotContain "{{cream"
            }
            assertMatchesSnapshot("FunNameSnapshotTest.copyFromToken") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }

        test("@CombineTo honours a funName token") {
            val source =
                """
                package snap.funname

                import me.tbsten.cream.CombineTo
                import me.tbsten.cream.CopyTargetSimpleName

                @CombineTo(Target::class, funName = "to" + CopyTargetSimpleName)
                data class Source(val shared: String)

                data class Target(val shared: String, val extra: Int)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue(generated) {
                generated shouldContain ".toTarget("
                generated shouldNotContain "{{cream"
            }
            assertMatchesSnapshot("FunNameSnapshotTest.combineToToken") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }

        test("@CombineFrom honours a funName token") {
            val source =
                """
                package snap.funname

                import me.tbsten.cream.CombineFrom
                import me.tbsten.cream.CopyTargetSimpleName

                data class First(val a: String)
                data class Second(val b: Int)

                @CombineFrom(First::class, Second::class, funName = "build" + CopyTargetSimpleName)
                data class Combined(val a: String, val b: Int, val extra: Long)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue(generated) {
                generated shouldContain ".buildCombined("
                generated shouldNotContain "{{cream"
            }
            assertMatchesSnapshot("FunNameSnapshotTest.combineFromToken") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }

        test("@CopyMapping honours a funName token") {
            val source =
                """
                package snap.funname

                import me.tbsten.cream.CopyMapping
                import me.tbsten.cream.CopyTargetSimpleName

                data class First(val shared: String)
                data class Second(val shared: String, val extra: Int)

                @CopyMapping(First::class, Second::class, funName = "to" + CopyTargetSimpleName)
                object Mapping
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue(generated) {
                generated shouldContain ".toSecond("
                generated shouldNotContain "{{cream"
            }
            assertMatchesSnapshot("FunNameSnapshotTest.copyMappingToken") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }

        test("@CopyMapping with canReverse gives both directions distinct token names") {
            val source =
                """
                package snap.funname

                import me.tbsten.cream.CopyMapping
                import me.tbsten.cream.CopyTargetSimpleName

                data class First(val shared: String)
                data class Second(val shared: String)

                @CopyMapping(First::class, Second::class, canReverse = true, funName = "to" + CopyTargetSimpleName)
                object Mapping
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue(generated) {
                generated shouldContain ".toSecond("
                generated shouldContain ".toFirst("
            }
            assertMatchesSnapshot("FunNameSnapshotTest.edgeCase/copyMappingCanReverseToken") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }

        test("@CombineMapping honours a funName token") {
            val source =
                """
                package snap.funname

                import me.tbsten.cream.CombineMapping
                import me.tbsten.cream.CopyTargetSimpleName

                data class First(val a: String)
                data class Second(val b: Int)
                data class Combined(val a: String, val b: Int, val extra: Long)

                @CombineMapping(
                    sources = [First::class, Second::class],
                    target = Combined::class,
                    funName = "to" + CopyTargetSimpleName,
                )
                object Mapping
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue(generated) {
                generated shouldContain ".toCombined("
                generated shouldNotContain "{{cream"
            }
            assertMatchesSnapshot("FunNameSnapshotTest.combineMappingToken") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }

        test("@CopyTo with multiple targets and a token names each function distinctly") {
            val source =
                """
                package snap.funname

                import me.tbsten.cream.CopyTo
                import me.tbsten.cream.CopyTargetSimpleName

                @CopyTo(Apple::class, Banana::class, funName = "to" + CopyTargetSimpleName)
                data class Source(val shared: String)

                data class Apple(val shared: String, val a: Int)
                data class Banana(val shared: String, val b: Int)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue(generated) {
                generated shouldContain ".toApple("
                generated shouldContain ".toBanana("
            }
            assertMatchesSnapshot("FunNameSnapshotTest.edgeCase/multiTargetToken") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }

        test("@CopyTo with a sealed target and a token names each child function distinctly") {
            val source =
                """
                package snap.funname

                import me.tbsten.cream.CopyTo
                import me.tbsten.cream.CopyTargetSimpleName

                @CopyTo(State::class, funName = "to" + CopyTargetSimpleName)
                data class Source(val id: String)

                sealed interface State {
                    val id: String

                    data class Loading(override val id: String) : State
                    data class Loaded(override val id: String, val payload: Int) : State
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue(generated) {
                generated shouldContain ".toLoading("
                generated shouldContain ".toLoaded("
                generated shouldNotContain "{{cream"
            }
            assertMatchesSnapshot("FunNameSnapshotTest.edgeCase/sealedTargetToken") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }

        test("@SealedCopy resolves DefaultCopyFunctionName to copy and supports a prefix") {
            val source =
                """
                package snap.funname

                import me.tbsten.cream.SealedCopy
                import me.tbsten.cream.DefaultCopyFunctionName

                @SealedCopy(funName = "my" + DefaultCopyFunctionName)
                sealed interface State {
                    val id: String

                    data class Loading(override val id: String) : State
                    data class Loaded(override val id: String, val payload: Int) : State
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue(generated) {
                generated shouldContain ".mycopy("
                generated shouldNotContain "{{cream"
            }
            assertMatchesSnapshot("FunNameSnapshotTest.edgeCase/sealedCopyDefaultPrefix") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }

        test("@SealedCopy resolves a CopyTarget token to the sealed type name") {
            val source =
                """
                package snap.funname

                import me.tbsten.cream.SealedCopy
                import me.tbsten.cream.CopyTargetSimpleName

                @SealedCopy(funName = "to" + CopyTargetSimpleName)
                sealed interface State {
                    val id: String

                    data class Loading(override val id: String) : State
                    data class Loaded(override val id: String, val payload: Int) : State
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue(generated) {
                generated shouldContain ".toState("
                generated shouldNotContain "{{cream"
            }
            assertMatchesSnapshot("FunNameSnapshotTest.sealedCopyToken") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }
    })
