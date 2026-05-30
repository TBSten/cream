package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class VisibilitySnapshotTest {
    @Test
    fun `copyTo internal visibility generates internal function`() {
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

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        val generated = result.generatedSourceText()
        assertTrue(
            generated.contains("internal fun"),
            "expected an internal function, but got:\n$generated",
        )
        assertMatchesSnapshot("VisibilitySnapshotTest.copyToInternal") {
            "Generated" facetOf generated
            "Input" facetOf source
        }
    }

    @Test
    fun `copyTo private visibility generates private function`() {
        val source =
            """
            package snap.visibility

            import me.tbsten.cream.CopyTo
            import me.tbsten.cream.CopyVisibility

            @CopyTo(Target::class, visibility = CopyVisibility.PRIVATE)
            data class Source(
                val shared: String,
            )

            data class Target(
                val shared: String,
                val extra: Int,
            )
            """.trimIndent()
        val result = compileWithCream(source)

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        val generated = result.generatedSourceText()
        assertTrue(
            generated.contains("private fun"),
            "expected a private function, but got:\n$generated",
        )
        assertMatchesSnapshot("VisibilitySnapshotTest.copyToPrivate") {
            "Generated" facetOf generated
            "Input" facetOf source
        }
    }

    @Test
    fun `copyTo without visibility keeps public default`() {
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

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        val generated = result.generatedSourceText()
        assertTrue(
            generated.contains("public fun"),
            "expected a public function (inherited default), but got:\n$generated",
        )
        assertTrue(
            !generated.contains("internal fun") && !generated.contains("private fun"),
            "expected no internal/private modifier in default case, but got:\n$generated",
        )
    }

    @Test
    fun `copyFrom internal visibility generates internal function`() {
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

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        val generated = result.generatedSourceText()
        assertTrue(
            generated.contains("internal fun"),
            "expected an internal function, but got:\n$generated",
        )
    }

    @Test
    fun `copyToChildren internal visibility generates internal functions`() {
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

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        val generated = result.generatedSourceText()
        assertTrue(
            generated.contains("internal fun") && !generated.contains("public fun"),
            "expected only internal functions, but got:\n$generated",
        )
    }

    @Test
    fun `sealedCopy internal visibility generates internal function`() {
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

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        val generated = result.generatedSourceText()
        assertTrue(
            generated.contains("internal fun"),
            "expected an internal function, but got:\n$generated",
        )
        assertMatchesSnapshot("VisibilitySnapshotTest.sealedCopyInternal") {
            "Generated" facetOf generated
            "Input" facetOf source
        }
    }
}
