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

internal class CopyToDiagnosticTest :
    FunSpec({
        test("@CopyTo targeting an enum class fails compilation") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyTo

                enum class Color { RED, BLUE }

                @CopyTo(Color::class)
                data class Source(val name: String)
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
                }
                result.generatedSources().shouldBeEmpty()
            }
            assertMatchesSnapshot("CopyToDiagnosticTest.enumTarget.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("@CopyTo targeting a non-sealed interface fails compilation") {
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
                withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
                }
                result.generatedSources().shouldBeEmpty()
            }
            assertMatchesSnapshot("CopyToDiagnosticTest.nonSealedInterface.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("@CopyTo targeting an annotation class fails compilation") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyTo

                annotation class Marker

                @CopyTo(Marker::class)
                data class Source(val name: String)
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
                }
                result.generatedSources().shouldBeEmpty()
            }
            assertMatchesSnapshot("CopyToDiagnosticTest.annotationTarget.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }
    })
