package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText
import kotlin.test.Test
import kotlin.test.assertEquals

internal class GenericSnapshotTest {
    @Test
    fun `copyFrom with a single type parameter generates expected source`() {
        val result =
            compileWithCream(
                """
                package snap.generic

                import me.tbsten.cream.CopyFrom

                @CopyFrom(Source::class)
                data class Target<T>(
                    val value: T,
                    val label: String,
                )

                data class Source<T>(
                    val value: T,
                    val label: String,
                )
                """.trimIndent(),
            )

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        assertMatchesSnapshot("GenericSnapshotTest.copyFromGeneric", result.generatedSourceText())
    }
}
