package me.tbsten.cream.ksp.feature.childOptionals

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.ksp.testing.compile.compileWithCream
import me.tbsten.cream.ksp.testing.compile.normalizedCompilerOutput
import me.tbsten.cream.ksp.testing.snapshot.assertMatchesSnapshot

/**
 * Diagnostics for invalid `@ChildOptionals` usage (issue #135): the annotation itself is only
 * valid on a sealed class/interface, and the blanket sweep can run into the same accessor-merge
 * conflicts as explicit `@ParentOptional` — here the type-mismatch message must attribute the
 * error to `@ChildOptionals` (the annotation the user actually wrote).
 */
internal class ChildOptionalsInvalidUsageTest :
    FreeSpec({
        "childOptionalsOnNonSealed" {
            val source =
                """
                package diag.co

                import me.tbsten.cream.ChildOptionals

                @ChildOptionals
                class NotSealed(val data: String)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "is not sealed"
            }
            assertMatchesSnapshot(name = "ChildOptionalsInvalidUsageTest.childOptionalsOnNonSealed.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        "mergedLeafTypeMismatch" {
            val source =
                """
                package diag.co

                import me.tbsten.cream.ChildOptionals

                @ChildOptionals
                sealed interface MyState {
                    data class Success(val value: String) : MyState
                    data class Failure(val value: Int) : MyState
                    data object Loading : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                val output = result.normalizedCompilerOutput()
                output shouldContain "@ChildOptionals"
                output shouldContain "mismatched types"
            }
            assertMatchesSnapshot(name = "ChildOptionalsInvalidUsageTest.mergedLeafTypeMismatch.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }
    })
