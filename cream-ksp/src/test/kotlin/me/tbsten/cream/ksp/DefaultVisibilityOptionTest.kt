package me.tbsten.cream.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import me.tbsten.cream.ksp.testing.compile.compileWithCream
import me.tbsten.cream.ksp.testing.compile.generatedSourceText

/**
 * End-to-end coverage for the project-level `cream.defaultVisibility` option (issue #137).
 *
 * Precedence under test:
 *  1. an explicit annotation `visibility` always wins, and
 *  2. otherwise `cream.defaultVisibility` is applied when the annotation leaves it at `INHERIT`,
 *  3. otherwise (both `INHERIT`) the generated function inherits the target declaration's visibility.
 *
 * Pure Map -> options parsing is pinned by
 * [me.tbsten.cream.ksp.options.CreamOptionsParsingTest]; the invalid-value build failure by
 * [OptionsDiagnosticTest]. Each scenario compiles a single annotated source so its one generated
 * function's modifier can be asserted unambiguously.
 */
internal class DefaultVisibilityOptionTest :
    FreeSpec({
        // Bare @CopyTo (no `visibility`) so the project default decides the modifier.
        val inheritingSource =
            """
            package viz

            import me.tbsten.cream.CopyTo

            @CopyTo(Target::class)
            data class Source(val shared: String)

            data class Target(val shared: String, val extra: Int)
            """.trimIndent()

        "cream.defaultVisibility=internal makes an unspecified @CopyTo generate an internal function" {
            val result =
                compileWithCream(inheritingSource, options = mapOf("cream.defaultVisibility" to "internal"))
            val generated = result.generatedSourceText()

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            withClue(generated) {
                generated shouldContain "internal fun"
                generated shouldNotContain "public fun"
            }
        }

        "an explicit annotation visibility wins over cream.defaultVisibility" {
            val explicitSource =
                """
                package viz

                import me.tbsten.cream.CopyTo
                import me.tbsten.cream.CopyVisibility

                @CopyTo(Target::class, visibility = CopyVisibility.PUBLIC)
                data class Source(val shared: String)

                data class Target(val shared: String, val extra: Int)
                """.trimIndent()

            val result =
                compileWithCream(explicitSource, options = mapOf("cream.defaultVisibility" to "internal"))
            val generated = result.generatedSourceText()

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            // The explicit PUBLIC overrides the internal project default.
            withClue(generated) {
                generated shouldContain "public fun"
                generated shouldNotContain "internal fun"
            }
        }

        "without the option the generated function still inherits the target's visibility" {
            val result = compileWithCream(inheritingSource)
            val generated = result.generatedSourceText()

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            // Target is public, so the unspecified copy stays public (pre-#137 behaviour).
            withClue(generated) {
                generated shouldContain "public fun"
                generated shouldNotContain "internal fun"
            }
        }

        "cream.defaultVisibility also applies to @SealedCopy self-copy functions" {
            val sealedSource =
                """
                package viz

                import me.tbsten.cream.SealedCopy

                @SealedCopy
                sealed interface UiState {
                    val message: String

                    data class Loading(override val message: String) : UiState
                    data class Success(override val message: String, val data: Int) : UiState
                }
                """.trimIndent()

            val result =
                compileWithCream(sealedSource, options = mapOf("cream.defaultVisibility" to "INTERNAL"))
            val generated = result.generatedSourceText()

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            withClue(generated) {
                generated shouldContain "internal fun"
                generated shouldNotContain "public fun"
            }
        }
    })
