package me.tbsten.cream.ksp.diagnostic

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput

/**
 * `@CombineFrom` needs at least one source class to combine. Declaring it with no sources is a
 * clean, positioned `COMPILATION_ERROR` (via `logger.error`) anchored at the annotated declaration
 * — never a KSP `INTERNAL_ERROR` — and leaves no partial generated file.
 */
internal class CombineFromDiagnosticTest :
    FunSpec({
        test("@CombineFrom with no sources fails compilation cleanly") {
            val source =
                """
                package diag

                import me.tbsten.cream.CombineFrom

                @CombineFrom()
                data class Target(val a: String)
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
                }
                result.messages shouldContain "at least one source class"
                result.generatedSources().shouldBeEmpty()
            }
            assertMatchesSnapshot("CombineFromDiagnosticTest.noSources.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }
    })
