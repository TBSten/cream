package me.tbsten.cream.ksp.diagnostic

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput

internal class CopyToChildrenDiagnosticTest :
    FunSpec({
        test("non-sealed class with @CopyToChildren fails compilation") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyToChildren

                @CopyToChildren
                class NotSealed(val prop: String)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("CopyToChildrenDiagnosticTest.nonSealedClass.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("data class with @CopyToChildren fails compilation") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyToChildren

                @CopyToChildren
                data class JustData(val prop: String)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("CopyToChildrenDiagnosticTest.dataClass.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("sealed interface with @CopyToChildren compiles successfully") {
            val result =
                compileWithCream(
                    """
                    package diag

                    import me.tbsten.cream.CopyToChildren

                    @CopyToChildren
                    sealed interface State {
                        val id: String

                        data class Loading(override val id: String) : State
                        data class Loaded(override val id: String, val value: Int) : State
                    }
                    """.trimIndent(),
                )

            withClue("Compilation should succeed. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
        }
    })
