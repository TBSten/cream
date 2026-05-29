package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * End-to-end snapshots for the `kdoc = KDoc(...)` parameter on cream annotations.
 *
 * Covers the rendering rules in [me.tbsten.cream.KDoc]:
 * - `description` is inserted between the auto-generated header line and the auto examples.
 * - Each `examples` entry is appended verbatim (after `trimIndent`) following the auto examples.
 * - Defaults (`KDoc()`) produce no user-content blocks.
 */
internal class KDocSnapshotTest {
    @Test
    fun `copyTo with description and examples renders user kdoc`() {
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

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        assertMatchesSnapshot("KDocSnapshotTest.copyToDescriptionAndExamples") {
            "Generated" facetOf result.generatedSourceText()
            "Input" facetOf source
        }
    }

    @Test
    fun `copyTo with only description renders description before auto examples`() {
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

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        assertMatchesSnapshot("KDocSnapshotTest.copyToDescriptionOnly") {
            "Generated" facetOf result.generatedSourceText()
            "Input" facetOf source
        }
    }

    @Test
    fun `copyTo with only examples renders examples after auto examples`() {
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

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        assertMatchesSnapshot("KDocSnapshotTest.copyToExamplesOnly") {
            "Generated" facetOf result.generatedSourceText()
            "Input" facetOf source
        }
    }

    @Test
    fun `copyToChildren propagates kdoc to every generated child function`() {
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

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        assertMatchesSnapshot("KDocSnapshotTest.copyToChildrenSharedDescription") {
            "Generated" facetOf result.generatedSourceText()
            "Input" facetOf source
        }
    }
}
