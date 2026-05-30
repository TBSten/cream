package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText

/**
 * End-to-end snapshots for the `kdoc = KDoc(...)` parameter on cream annotations.
 *
 * Covers the rendering rules in [me.tbsten.cream.KDoc]:
 * - `description` is inserted between the auto-generated header line and the auto examples.
 * - Each `examples` entry is appended verbatim (after `trimIndent`) following the auto examples.
 * - Defaults (`KDoc()`) produce no user-content blocks.
 */
internal class KDocSnapshotTest :
    FunSpec({
        test("copyTo with description and examples renders user kdoc") {
            val source =
                """
                package snap.kdoc

                import me.tbsten.cream.CopyTo
                import me.tbsten.cream.KDoc

                @CopyTo(
                    Target::class,
                    kdoc = KDoc(
                        description = "This function should not be used when the source is in transient state.",
                        examples = [
                            ${"\"\"\""}
                            # Prefer this

                            ```kt
                            val target = source.copyToTarget()
                            ```
                            ${"\"\"\""},
                            ${"\"\"\""}
                            # Avoid this

                            ```kt
                            val target = source.copyToTarget() // do not!
                            ```
                            ${"\"\"\""},
                        ],
                    ),
                )
                data class Source(val shared: String)

                data class Target(val shared: String, val extra: Int)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("KDocSnapshotTest.copyToDescriptionAndExamples") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        test("copyTo with only description renders description before auto examples") {
            val source =
                """
                package snap.kdoc

                import me.tbsten.cream.CopyTo
                import me.tbsten.cream.KDoc

                @CopyTo(
                    Target::class,
                    kdoc = KDoc(description = "Deprecated. Migrate to TargetV2."),
                )
                data class Source(val shared: String)

                data class Target(val shared: String)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("KDocSnapshotTest.copyToDescriptionOnly") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        test("copyTo with only examples renders examples after auto examples") {
            val source =
                """
                package snap.kdoc

                import me.tbsten.cream.CopyTo
                import me.tbsten.cream.KDoc

                @CopyTo(
                    Target::class,
                    kdoc = KDoc(
                        examples = [
                            ${"\"\"\""}
                            # In a coroutine

                            ```kt
                            suspend fun reload(source: Source) = source.copyToTarget()
                            ```
                            ${"\"\"\""},
                        ],
                    ),
                )
                data class Source(val shared: String)

                data class Target(val shared: String)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("KDocSnapshotTest.copyToExamplesOnly") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        test("copyToChildren propagates kdoc to every generated child function") {
            val source =
                """
                package snap.kdoc

                import me.tbsten.cream.CopyToChildren
                import me.tbsten.cream.KDoc

                @CopyToChildren(
                    kdoc = KDoc(
                        description = "Shared note for every State subclass.",
                    ),
                )
                sealed interface State {
                    val id: String

                    data class Initial(override val id: String) : State
                    data class Loaded(override val id: String, val payload: Int) : State
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("KDocSnapshotTest.copyToChildrenSharedDescription") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }
    })
