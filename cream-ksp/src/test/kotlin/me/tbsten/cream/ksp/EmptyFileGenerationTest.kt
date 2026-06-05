package me.tbsten.cream.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText

/**
 * #113: when a generation unit produces zero copy functions (all targets skipped),
 * cream must write NO file at all — not a file containing only `package` + `import`.
 */
internal class EmptyFileGenerationTest :
    FunSpec({
        test("copyToChildren over all-object subclasses with notCopyToObject writes no file") {
            // Every direct subclass is an object, so notCopyToObject=true skips them all and the
            // generation unit produces zero functions.
            val source =
                """
                package empty.allobject

                import me.tbsten.cream.CopyToChildren

                @CopyToChildren(notCopyToObject = true)
                sealed interface State {
                    data object Loading : State
                    data object Empty : State
                }
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue(result.messages) {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                }
                withClue("No file should be generated when every target is skipped. Generated:\n${result.generatedSourceText()}") {
                    result.generatedSources().shouldBeEmpty()
                }
            }
        }

        test("copyToChildren that generates functions still writes its file") {
            // Regression: a unit that produces at least one function keeps emitting its file.
            val source =
                """
                package empty.generates

                import me.tbsten.cream.CopyToChildren

                @CopyToChildren(notCopyToObject = true)
                sealed interface State {
                    data class Loaded(val value: String) : State
                    data object Loading : State
                }
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue(result.messages) {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                }
                withClue("A unit with a real declaration should still produce one file. Generated:\n${result.generatedSourceText()}") {
                    result.generatedSources().size shouldBe 1
                }
            }
        }
    })
