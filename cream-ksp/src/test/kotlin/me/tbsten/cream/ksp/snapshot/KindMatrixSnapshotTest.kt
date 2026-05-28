package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Snapshot tests covering cross-kind source/target combinations that are not exercised by
 * [BasicSnapshotTest] / [GenericSnapshotTest] / [SealedSnapshotTest] / [ObjectTargetSnapshotTest].
 */
internal class KindMatrixSnapshotTest {
    @Test
    fun `copyTo from data class to data object generates expected source`() {
        val source =
            """
            package snap.kind.classToObject

            import me.tbsten.cream.CopyTo

            @CopyTo(Loaded::class)
            data class Source(val id: String)

            data object Loaded
            """.trimIndent()
        val result = compileWithCream(source)

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        assertMatchesSnapshot("KindMatrixSnapshotTest.copyToDataObject") {
            "Generated" facetOf result.generatedSourceText()
            "Input" facetOf source
        }
    }

    @Test
    fun `copyTo from data class to sealed interface expands across subclasses`() {
        val source =
            """
            package snap.kind.classToSealed

            import me.tbsten.cream.CopyTo

            @CopyTo(State::class)
            data class Source(val id: String)

            sealed interface State {
                val id: String

                data class Loading(override val id: String) : State
                data class Loaded(override val id: String, val payload: Int) : State
            }
            """.trimIndent()
        val result = compileWithCream(source)

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        assertMatchesSnapshot("KindMatrixSnapshotTest.copyToSealedInterface") {
            "Generated" facetOf result.generatedSourceText()
            "Input" facetOf source
        }
    }

    @Test
    fun `copyFrom basic data class to data class generates expected source`() {
        val source =
            """
            package snap.kind.copyFromBasic

            import me.tbsten.cream.CopyFrom

            data class Source(
                val shared: String,
                val onlyOnSource: Int,
            )

            @CopyFrom(Source::class)
            data class Target(
                val shared: String,
                val onlyOnTarget: Boolean,
            )
            """.trimIndent()
        val result = compileWithCream(source)

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        assertMatchesSnapshot("KindMatrixSnapshotTest.copyFromBasic") {
            "Generated" facetOf result.generatedSourceText()
            "Input" facetOf source
        }
    }

    @Test
    fun `copyToChildren with mixed data class and data object subclasses generates expected source`() {
        val source =
            """
            package snap.kind.sealedMixed

            import me.tbsten.cream.CopyToChildren

            @CopyToChildren
            sealed interface State {
                val id: String

                data object Initial : State {
                    override val id: String get() = "initial"
                }

                data class Loaded(override val id: String, val payload: Int) : State
            }
            """.trimIndent()
        val result = compileWithCream(source)

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        assertMatchesSnapshot("KindMatrixSnapshotTest.copyToChildrenMixed") {
            "Generated" facetOf result.generatedSourceText()
            "Input" facetOf source
        }
    }
}
