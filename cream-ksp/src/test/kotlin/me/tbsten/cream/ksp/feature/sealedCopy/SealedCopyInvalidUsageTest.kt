package me.tbsten.cream.ksp.feature.sealedCopy

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.ksp.testing.compile.compileWithCream
import me.tbsten.cream.ksp.testing.compile.normalizedCompilerOutput
import me.tbsten.cream.ksp.testing.snapshot.assertMatchesSnapshot

/**
 * Diagnostics for invalid `@SealedCopy` usage. The generator-driven [SealedCopySnapshotTest] only feeds
 * VALID scenarios, so misuse of `@SealedCopy.Via` (the delegate marker) has no other home. cream validates a
 * `@Via` delegate with the same strictness as the default `copy` path: every abstract property must be supplied
 * and every parameter must bind or default. A gap is a clean positioned `COMPILATION_ERROR` (never silently
 * mis-generated code — the bug behind issue #162, where a subset delegate produced an infinitely-recursing call).
 */
internal class SealedCopyInvalidUsageTest :
    FreeSpec({
        "viaMissingAbstractProperty" {
            val source =
                """
                package sealed.diag

                import me.tbsten.cream.SealedCopy

                @SealedCopy
                sealed interface State {
                    val id: String
                    val count: Int

                    class Custom(
                        override val id: String,
                        override val count: Int,
                    ) : State {
                        @SealedCopy.Via
                        fun cloneWith(id: String): Custom = Custom(id = id, count = this.count)
                    }
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "count"
            }
            assertMatchesSnapshot(name = "SealedCopyInvalidUsageTest.viaMissingAbstractProperty.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        "viaUnmappedParameterWithoutDefault" {
            val source =
                """
                package sealed.diag

                import me.tbsten.cream.SealedCopy

                @SealedCopy
                sealed interface State {
                    val id: String

                    class Custom(
                        override val id: String,
                    ) : State {
                        @SealedCopy.Via
                        fun cloneWith(id: String, extra: Int): Custom = Custom(id = id)
                    }
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "extra"
            }
            assertMatchesSnapshot(name = "SealedCopyInvalidUsageTest.viaUnmappedParameterWithoutDefault.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        "multipleViaFunctions" {
            val source =
                """
                package sealed.diag

                import me.tbsten.cream.SealedCopy

                @SealedCopy
                sealed interface State {
                    val id: String

                    class Custom(
                        override val id: String,
                    ) : State {
                        @SealedCopy.Via
                        fun cloneA(id: String): Custom = Custom(id = id)

                        @SealedCopy.Via
                        fun cloneB(id: String): Custom = Custom(id = id)
                    }
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "@SealedCopy.Via"
            }
            assertMatchesSnapshot(name = "SealedCopyInvalidUsageTest.multipleViaFunctions.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }
    })
