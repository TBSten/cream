package me.tbsten.cream.ksp.diagnostic

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput

internal class OptionsDiagnosticTest :
    FunSpec({
        val validSource: String =
            """
            package diag

            import me.tbsten.cream.CopyTo

            @CopyTo(Target::class)
            data class Source(val prop: String)

            data class Target(val prop: String)
            """.trimIndent()

        test("invalid copyFunNamingStrategy fails compilation with helpful message") {
            val result =
                compileWithCream(
                    validSource,
                    options = mapOf("cream.copyFunNamingStrategy" to "not-a-strategy"),
                )

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("OptionsDiagnosticTest.invalidCopyFunNamingStrategy.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf validSource
            }
        }

        test("invalid escapeDot fails compilation with helpful message") {
            val result =
                compileWithCream(
                    validSource,
                    options = mapOf("cream.escapeDot" to "not-an-escape"),
                )

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("OptionsDiagnosticTest.invalidEscapeDot.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf validSource
            }
        }

        test("valid copyFunNamingStrategy value compiles successfully") {
            val result =
                compileWithCream(
                    validSource,
                    options = mapOf("cream.copyFunNamingStrategy" to "full-name"),
                )

            withClue("Compilation should succeed. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
        }
    })
