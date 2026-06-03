package me.tbsten.cream.ksp.diagnostic

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput

/**
 * `@CombineTo` / `@CombineFrom` combine several sources into a single target, so the target
 * must be a concrete class (or annotation class / object) that can be constructed directly.
 * Unlike `@CopyTo`, a sealed interface target cannot fan out here, so it must be rejected with a
 * clean cream diagnostic rather than crashing the processor with an INTERNAL_ERROR.
 */
internal class CombineTargetKindDiagnosticTest :
    FunSpec({
        test("rejects a sealed interface target with a clean diagnostic") {
            val source =
                """
                package diag

                import me.tbsten.cream.CombineTo

                sealed interface State {
                    val id: String
                }

                @CombineTo(State::class)
                data class Source(val id: String)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
            }
            assertMatchesSnapshot("CombineTargetKindDiagnosticTest.sealedInterface.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("rejects an enum class target without suggesting a sealed interface as alternative") {
            val source =
                """
                package diag

                import me.tbsten.cream.CombineTo

                enum class Status { ACTIVE, INACTIVE }

                @CombineTo(Status::class)
                data class Source(val name: String)
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
                }
                // The solution must not mention "sealed interface" since combine rejects interfaces.
                result.messages shouldContain "enum class"
                result.messages shouldNotContain "sealed interface"
            }
            assertMatchesSnapshot("CombineTargetKindDiagnosticTest.enumClass.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }
    })
