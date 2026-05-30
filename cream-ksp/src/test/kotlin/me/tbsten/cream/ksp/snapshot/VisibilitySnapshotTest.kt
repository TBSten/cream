package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText

internal class VisibilitySnapshotTest :
    FunSpec({
        test("copyTo internal visibility generates internal function") {
            val source =
                """
                package snap.visibility

                import me.tbsten.cream.CopyTo
                import me.tbsten.cream.CopyVisibility

                @CopyTo(Target::class, visibility = CopyVisibility.INTERNAL)
                data class Source(
                    val shared: String,
                )

                data class Target(
                    val shared: String,
                    val extra: Int,
                )
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue("expected an internal function, but got:\n$generated") {
                generated shouldContain "internal fun"
            }
            assertMatchesSnapshot("VisibilitySnapshotTest.copyToInternal") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }

        test("copyTo without visibility keeps public default") {
            val source =
                """
                package snap.visibility

                import me.tbsten.cream.CopyTo

                @CopyTo(Target::class)
                data class Source(
                    val shared: String,
                )

                data class Target(
                    val shared: String,
                    val extra: Int,
                )
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue("expected a public function (inherited default), but got:\n$generated") {
                generated shouldContain "public fun"
            }
            withClue("expected no internal/private modifier in default case, but got:\n$generated") {
                (!generated.contains("internal fun") && !generated.contains("private fun")) shouldBe true
            }
        }

        test("copyFrom internal visibility generates internal function") {
            val source =
                """
                package snap.visibility

                import me.tbsten.cream.CopyFrom
                import me.tbsten.cream.CopyVisibility

                data class Source(
                    val shared: String,
                )

                @CopyFrom(Source::class, visibility = CopyVisibility.INTERNAL)
                data class Target(
                    val shared: String,
                    val extra: Int,
                )
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue("expected an internal function, but got:\n$generated") {
                generated shouldContain "internal fun"
            }
            assertMatchesSnapshot("VisibilitySnapshotTest.copyFromInternal") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }

        test("copyToChildren internal visibility generates internal functions") {
            val source =
                """
                package snap.visibility

                import me.tbsten.cream.CopyToChildren
                import me.tbsten.cream.CopyVisibility

                @CopyToChildren(visibility = CopyVisibility.INTERNAL)
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
            val generated = result.generatedSourceText()
            withClue("expected only internal functions, but got:\n$generated") {
                (generated.contains("internal fun") && !generated.contains("public fun")) shouldBe true
            }
            assertMatchesSnapshot("VisibilitySnapshotTest.copyToChildrenInternal") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }

        test("combineTo internal visibility generates internal function") {
            val source =
                """
                package snap.visibility

                import me.tbsten.cream.CombineTo
                import me.tbsten.cream.CopyVisibility

                @CombineTo(Target::class, visibility = CopyVisibility.INTERNAL)
                data class Source(
                    val shared: String,
                )

                data class Target(
                    val shared: String,
                    val extra: Int,
                )
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue("expected an internal function, but got:\n$generated") {
                generated shouldContain "internal fun"
            }
            assertMatchesSnapshot("VisibilitySnapshotTest.combineToInternal") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }

        test("combineFrom internal visibility generates internal function") {
            val source =
                """
                package snap.visibility

                import me.tbsten.cream.CombineFrom
                import me.tbsten.cream.CopyVisibility

                data class Source(
                    val shared: String,
                )

                @CombineFrom(Source::class, visibility = CopyVisibility.INTERNAL)
                data class Target(
                    val shared: String,
                    val extra: Int,
                )
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue("expected an internal function, but got:\n$generated") {
                generated shouldContain "internal fun"
            }
            assertMatchesSnapshot("VisibilitySnapshotTest.combineFromInternal") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }

        test("sealedCopy internal visibility generates internal function") {
            val source =
                """
                package snap.visibility

                import me.tbsten.cream.SealedCopy
                import me.tbsten.cream.CopyVisibility

                @SealedCopy(visibility = CopyVisibility.INTERNAL)
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
            val generated = result.generatedSourceText()
            withClue("expected an internal function, but got:\n$generated") {
                generated shouldContain "internal fun"
            }
            assertMatchesSnapshot("VisibilitySnapshotTest.sealedCopyInternal") {
                "Generated" facetOf generated
                "Input" facetOf source
            }
        }
    })
