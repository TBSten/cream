package me.tbsten.cream.ksp.diagnostic

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput

internal class SealedCopyDiagnosticTest :
    FunSpec({
        test("object subtype under default ERROR strategy fails compilation") {
            val source =
                """
                package diag.sealedCopy

                import me.tbsten.cream.SealedCopy

                @SealedCopy
                sealed interface MyState {
                    val name: String

                    data class Loading(override val name: String) : MyState
                    data object Empty : MyState { override val name: String = "" }
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("SealedCopyDiagnosticTest.objectErrorDefault.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("non-data class without compatible copy under default ERROR strategy fails compilation") {
            val source =
                """
                package diag.sealedCopy

                import me.tbsten.cream.SealedCopy

                @SealedCopy
                sealed interface MyState {
                    val name: String

                    data class Loading(override val name: String) : MyState
                    class Frozen(override val name: String) : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("SealedCopyDiagnosticTest.missingCopy.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("missing-copy diagnostic names 'copy' even when funName is customized") {
            val source =
                """
                package diag.sealedCopy

                import me.tbsten.cream.SealedCopy

                @SealedCopy(funName = "updated")
                sealed interface MyState {
                    val name: String

                    data class Loading(override val name: String) : MyState
                    class Frozen(override val name: String) : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("SealedCopyDiagnosticTest.missingCopyCustomFunName.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("non-sealed class with @SealedCopy fails compilation") {
            val source =
                """
                package diag.sealedCopy

                import me.tbsten.cream.SealedCopy

                @SealedCopy
                class NotSealed(val name: String)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("SealedCopyDiagnosticTest.nonSealedClass.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }
    })
