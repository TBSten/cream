package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
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

        test("copyToChildren through an intermediate sealed class generates only the annotated class receiver") {
            val source =
                """
                package snap.sealed.viaSealedClass

                import me.tbsten.cream.CopyToChildren

                @CopyToChildren
                sealed interface S1 {
                    val id: String

                    sealed class S2 : S1 {
                        data object S3 : S2() {
                            override val id: String get() = "s3"
                        }
                    }
                }
                """.trimIndent()
            val result = compileWithCream(source)
            val generated = result.generatedSourceText()

            assertSoftly {
                withClue("Compilation should succeed. Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                }
                // The annotated class S1 is the receiver, reaching the terminal leaf S3.
                generated shouldContain "S1.copyToS1S2S3()"
                // The intermediate sealed node S2 must NOT be a receiver.
                generated shouldNotContain "S1.S2.copyToS1S2S3()"
            }
            assertMatchesSnapshot("SealedSnapshotTest.copyToChildrenViaSealedClass") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }

        test("copyToChildren through a deep interface-class-class chain generates only the annotated class receiver") {
            val source =
                """
                package snap.sealed.deepChain

                import me.tbsten.cream.CopyToChildren

                @CopyToChildren
                sealed interface Root {
                    val id: String

                    sealed class Mid : Root {
                        sealed class Inner : Mid() {
                            data class Leaf(override val id: String, val payload: Int) : Inner()
                        }
                    }
                }
                """.trimIndent()
            val result = compileWithCream(source)
            val generated = result.generatedSourceText()

            assertSoftly {
                withClue("Compilation should succeed. Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                }
                // Only the annotated class Root is the receiver, reaching the terminal leaf Leaf.
                generated shouldContain "snap.sealed.deepChain.Root.copyToRootMidInnerLeaf("
                // Neither intermediate sealed node (Mid, Inner) may be a receiver.
                generated shouldNotContain "Root.Mid.Inner.copyToRootMidInnerLeaf("
                generated shouldNotContain "Root.Mid.copyToRootMidInnerLeaf("
            }
            assertMatchesSnapshot("SealedSnapshotTest.copyToChildrenDeepChain") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }

        test("copyToChildren on a sealed type with no concrete subclasses generates no copy function") {
            val source =
                """
                package snap.sealed.empty

                import me.tbsten.cream.CopyToChildren

                @CopyToChildren
                sealed interface Empty {
                    val id: String
                }
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Compilation should succeed. Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                }
                // No concrete subclasses to fan out to, so no copy function is generated (only the
                // file header, if any, is emitted).
                result.generatedSourceText() shouldNotContain "fun "
            }
        }
    })
