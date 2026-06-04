package me.tbsten.cream.ksp.diagnostic

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput

/**
 * `@SealedCopy` misuse must surface as a clean, positioned `COMPILATION_ERROR` (via
 * `logger.error`), never as a KSP `INTERNAL_ERROR` (a processor-crash stack trace). Each rejection
 * also fails closed: when no copy function can be generated, the transactional writer leaves no
 * partial / empty generated file behind.
 */
internal class SealedCopyDiagnosticTest :
    FunSpec({
        test("object subtype under default ERROR strategy fails compilation") {
            val source =
                """
                package diag.sealedCopy

                import me.tbsten.cream.SealedCopy

                @SealedCopy
                sealed interface MyState {
                    val name: String

                    data class Loading(override val name: String) : MyState
                    data object Empty : MyState { override val name: String = "" }
                }
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
                }
                result.generatedSources().shouldBeEmpty()
            }
            assertMatchesSnapshot("SealedCopyDiagnosticTest.objectErrorDefault.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("non-data class without compatible copy under default ERROR strategy fails compilation") {
            val source =
                """
                package diag.sealedCopy

                import me.tbsten.cream.SealedCopy

                @SealedCopy
                sealed interface MyState {
                    val name: String

                    data class Loading(override val name: String) : MyState
                    class Frozen(override val name: String) : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
                }
                result.generatedSources().shouldBeEmpty()
            }
            assertMatchesSnapshot("SealedCopyDiagnosticTest.missingCopy.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("missing-copy diagnostic names 'copy' even when funName is customized") {
            val source =
                """
                package diag.sealedCopy

                import me.tbsten.cream.SealedCopy

                @SealedCopy(funName = "updated")
                sealed interface MyState {
                    val name: String

                    data class Loading(override val name: String) : MyState
                    class Frozen(override val name: String) : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
                }
                result.generatedSources().shouldBeEmpty()
            }
            assertMatchesSnapshot("SealedCopyDiagnosticTest.missingCopyCustomFunName.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("non-sealed class with @SealedCopy fails compilation") {
            val source =
                """
                package diag.sealedCopy

                import me.tbsten.cream.SealedCopy

                @SealedCopy
                class NotSealed(val name: String)
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
                }
                result.generatedSources().shouldBeEmpty()
            }
            assertMatchesSnapshot("SealedCopyDiagnosticTest.nonSealedClass.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }
    })
