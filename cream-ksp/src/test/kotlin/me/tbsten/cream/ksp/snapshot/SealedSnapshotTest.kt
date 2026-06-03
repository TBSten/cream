package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput

internal class SealedSnapshotTest :
    FunSpec({
        test("copyToChildren sealed interface generates expected source") {
            val source =
                """
                package snap.sealed

                import me.tbsten.cream.CopyToChildren

                @CopyToChildren
                sealed interface State {
                    val id: String

                    data class Loading(override val id: String) : State
                    data class Loaded(override val id: String, val payload: Int) : State
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("SealedSnapshotTest.copyToChildren") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        test("copyToChildren generates correctly for a child with a vararg ctor param") {
            val source =
                """
                package snap.sealed.vararg

                import me.tbsten.cream.CopyToChildren

                @CopyToChildren
                sealed interface State {
                    val id: Int

                    class Loaded(override val id: Int, vararg val tags: String) : State
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should succeed. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("SealedSnapshotTest.copyToChildrenVararg") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }
    })
