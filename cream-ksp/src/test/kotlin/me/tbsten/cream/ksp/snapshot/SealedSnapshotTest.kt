package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText
import kotlin.test.Test
import kotlin.test.assertEquals

internal class SealedSnapshotTest {
    @Test
    fun `copyToChildren sealed interface generates expected source`() {
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

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        assertMatchesSnapshot("SealedSnapshotTest.copyToChildren") {
            "Generated" facetOf result.generatedSourceText()
            "Input" facetOf source
        }
    }
}
