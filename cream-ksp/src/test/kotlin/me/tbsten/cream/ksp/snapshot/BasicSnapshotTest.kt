package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText
import kotlin.test.Test
import kotlin.test.assertEquals

internal class BasicSnapshotTest {
    @Test
    fun `copyTo class generates expected source`() {
        val source =
            """
            package snap.basic

            import me.tbsten.cream.CopyTo

            @CopyTo(Target::class)
            data class Source(
                val shared: String,
                val onlyOnSource: Int,
            )

            data class Target(
                val shared: String,
                val onlyOnTarget: Boolean,
            )
            """.trimIndent()
        val result = compileWithCream(source)

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        assertMatchesSnapshot("BasicSnapshotTest.copyTo", result.generatedSourceText()) {
            "Input" facetOf source
        }
    }
}
