package me.tbsten.cream.ksp.feature.sealedCopy

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.ksp.testing.compile.compileWithCream
import me.tbsten.cream.ksp.testing.compile.generatedSourceText
import me.tbsten.cream.ksp.testing.compile.normalizedCompilerOutput

/**
 * Semantic edge cases for `@SealedCopy`. Currently focuses on the `cream.nonCopyableStrategy` project option
 * (issue #163): the module-wide default for a non-copyable (`object`) leaf's strategy, applied when the
 * annotation leaves `nonCopyableStrategy` at its [me.tbsten.cream.NonCopyableStrategy.INHERIT] sentinel.
 * Precedence mirrors `cream.defaultVisibility`: explicit annotation value > project option > `ERROR`.
 */
internal class SealedCopyEdgeUsageTest :
    FreeSpec({
        // A bare @SealedCopy with an `object` leaf: ERROR (the effective default) would reject it.
        val bareWithObjectLeaf =
            """
            package sealed.opt

            import me.tbsten.cream.SealedCopy

            @SealedCopy
            sealed interface State {
                val name: String

                data class Loading(override val name: String) : State
                data object Empty : State {
                    override val name: String = ""
                }
            }
            """.trimIndent()

        "cream.nonCopyableStrategy applies to a bare @SealedCopy" {
            val result = compileWithCream(bareWithObjectLeaf, options = mapOf("cream.nonCopyableStrategy" to "RETURN_AS_IS"))

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            // RETURN_AS_IS emits `-> this` for the object leaf instead of failing the build.
            result.generatedSourceText() shouldContain "Empty -> this"
        }

        "an explicit annotation nonCopyableStrategy overrides the option" {
            val source =
                """
                package sealed.opt

                import me.tbsten.cream.NonCopyableStrategy
                import me.tbsten.cream.SealedCopy

                @SealedCopy(nonCopyableStrategy = NonCopyableStrategy.ERROR)
                sealed interface State {
                    val name: String

                    data class Loading(override val name: String) : State
                    data object Empty : State {
                        override val name: String = ""
                    }
                }
                """.trimIndent()
            // Annotation ERROR must win over option RETURN_AS_IS → the object leaf is rejected (build fails).
            val result = compileWithCream(source, options = mapOf("cream.nonCopyableStrategy" to "RETURN_AS_IS"))

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "Empty"
            }
        }

        "without the option a bare @SealedCopy with an object leaf still errors (ERROR fallback)" {
            val result = compileWithCream(bareWithObjectLeaf)

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
        }
    })
