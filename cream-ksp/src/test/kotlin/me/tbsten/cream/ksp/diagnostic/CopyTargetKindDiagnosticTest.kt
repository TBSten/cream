package me.tbsten.cream.ksp.diagnostic

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput

/**
 * `@CopyTo` / `@CopyFrom` targets must be *constructable*: a concrete class (or annotation
 * class / object) whose primary constructor the generated code can actually call. Kinds that
 * cannot be instantiated (a non-sealed abstract / inner class, a plain or fun interface) or whose
 * constructor is not reachable (private / protected primary constructor) must be rejected with
 * a clean cream diagnostic instead of emitting broken `Target(...)` code that fails later with a
 * confusing compiler error.
 *
 * A *sealed* class is the exception: like a sealed interface it cannot be instantiated directly,
 * but instead of being rejected it fans out to its concrete subclasses (see
 * [me.tbsten.cream.ksp.snapshot.KindMatrixSnapshotTest] /
 * [me.tbsten.cream.ksp.snapshot.SealedSnapshotTest]).
 */
internal class CopyTargetKindDiagnosticTest :
    FunSpec({
        test("rejects an abstract class target and points to a concrete class") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyTo

                abstract class Base(val id: String)

                @CopyTo(Base::class)
                data class Source(val id: String)
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
                }
                result.messages shouldContain "abstract class"
            }
            assertMatchesSnapshot("CopyTargetKindDiagnosticTest.abstractClass.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("rejects an inner class target with a clean diagnostic") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyTo

                class Outer {
                    inner class Inner(val id: String)
                }

                @CopyTo(Outer.Inner::class)
                data class Source(val id: String)
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
                }
                result.messages shouldContain "inner class"
            }
            assertMatchesSnapshot("CopyTargetKindDiagnosticTest.innerClass.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("rejects a plain interface target and points to a sealed interface") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyTo

                interface Plain {
                    val id: String
                }

                @CopyTo(Plain::class)
                data class Source(val id: String)
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
                }
                // The message-formatting bug `).It must be` (missing space) must be fixed.
                result.messages shouldContain "). It must be a sealed interface."
            }
            assertMatchesSnapshot("CopyTargetKindDiagnosticTest.plainInterface.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("rejects a fun interface target and points to a sealed interface") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyTo

                fun interface Action {
                    fun run(): Int
                }

                @CopyTo(Action::class)
                data class Source(val id: String)
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
                }
                result.messages shouldContain "). It must be a sealed interface."
            }
            assertMatchesSnapshot("CopyTargetKindDiagnosticTest.funInterface.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("rejects a target with a private primary constructor") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyTo

                class Secret private constructor(val id: String)

                @CopyTo(Secret::class)
                data class Source(val id: String)
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
                }
                result.messages shouldContain "primary constructor"
                result.messages shouldContain "private"
            }
            assertMatchesSnapshot("CopyTargetKindDiagnosticTest.privateConstructor.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("rejects a target with a protected primary constructor") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyTo

                open class Restricted protected constructor(val id: String)

                @CopyTo(Restricted::class)
                data class Source(val id: String)
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
                }
                result.messages shouldContain "primary constructor"
                result.messages shouldContain "protected"
            }
            assertMatchesSnapshot("CopyTargetKindDiagnosticTest.protectedConstructor.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }
    })
