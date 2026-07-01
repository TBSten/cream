package me.tbsten.cream.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.ksp.testing.compile.compileWithCream
import me.tbsten.cream.ksp.testing.compile.normalizedCompilerOutput
import me.tbsten.cream.ksp.testing.snapshot.assertMatchesSnapshot

/**
 * Diagnostics for invalid KSP build arguments (`cream.*` options). The generator-driven
 * `<Feat>SnapshotTest`s only ever feed VALID option combinations (`validCreamOptions()`), so the
 * "bad option value → fail the build with a positioned message, not an opaque crash" contract has no
 * other home. `CreamOptions` parsing throws `InvalidCreamOptionException` for an unknown
 * `copyFunNamingStrategy` / `escapeDot` / `defaultVisibility`; these pin that it surfaces as a build
 * failure carrying the offending value.
 */
internal class OptionsDiagnosticTest :
    FreeSpec({
        val source =
            """
            package options.diag

            import me.tbsten.cream.CopyTo

            @CopyTo(Target::class)
            data class Source(val shared: String)

            data class Target(val shared: String, val extra: Int)
            """.trimIndent()

        "invalidNamingStrategy" {
            val result = compileWithCream(source, options = mapOf("cream.copyFunNamingStrategy" to "not-a-strategy"))

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "cream.copyFunNamingStrategy"
            }
            assertMatchesSnapshot(name = "OptionsDiagnosticTest.invalidNamingStrategy.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        "invalidEscapeDot" {
            val result = compileWithCream(source, options = mapOf("cream.escapeDot" to "not-an-escape"))

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "cream.escapeDot"
            }
            assertMatchesSnapshot(name = "OptionsDiagnosticTest.invalidEscapeDot.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        "invalidDefaultVisibility" {
            val result = compileWithCream(source, options = mapOf("cream.defaultVisibility" to "not-a-visibility"))

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "cream.defaultVisibility"
            }
            assertMatchesSnapshot(name = "OptionsDiagnosticTest.invalidDefaultVisibility.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        "invalidNonCopyableStrategy" {
            val result = compileWithCream(source, options = mapOf("cream.nonCopyableStrategy" to "not-a-strategy"))

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "cream.nonCopyableStrategy"
            }
            assertMatchesSnapshot(name = "OptionsDiagnosticTest.invalidNonCopyableStrategy.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }
    })
