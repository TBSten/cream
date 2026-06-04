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
 * `@CombineTo` is NOT `@Repeatable`; it lists its targets in a single `vararg targets`. Listing
 * the same target twice (`@CombineTo(Foo::class, Foo::class)`) is an unambiguous user mistake: cream
 * would otherwise write the same generated file twice and crash with a `FileAlreadyExistsException`
 * (a KSP `INTERNAL_ERROR`). It is rejected up front with a clean positioned `COMPILATION_ERROR`
 * instead, and no partial file is emitted (issue #101).
 */
internal class CombineToDiagnosticTest :
    FunSpec({
        test("@CombineTo listing the same target twice fails compilation cleanly without crashing") {
            val source =
                """
                package diag

                import me.tbsten.cream.CombineTo

                data class Foo(val id: String, val extra: Int)

                @CombineTo(Foo::class, Foo::class)
                data class Source(val id: String)
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Output:\n${result.normalizedCompilerOutput()}") {
                    // A clean COMPILATION_ERROR, not the INTERNAL_ERROR a FileAlreadyExistsException crash produces.
                    result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
                }
                // The diagnostic names the duplicated target so the user can find the mistake.
                result.messages shouldContain "Foo"
                result.messages shouldContain "duplicate"
                // No partial / empty generated file is left behind.
                withClue("No file should be generated for a rejected @CombineTo.") {
                    result.generatedSources() shouldBe emptyList()
                }
            }
            assertMatchesSnapshot("CombineToDiagnosticTest.duplicateTarget.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }
    })
