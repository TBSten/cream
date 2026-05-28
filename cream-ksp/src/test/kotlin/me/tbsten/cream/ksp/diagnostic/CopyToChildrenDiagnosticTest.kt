package me.tbsten.cream.ksp.diagnostic

import com.tschuchort.compiletesting.KotlinCompilation
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class CopyToChildrenDiagnosticTest {
    @Test
    fun `non-sealed class with @CopyToChildren fails compilation`() {
        val source =
            """
            package diag

            import me.tbsten.cream.CopyToChildren

            @CopyToChildren
            class NotSealed(val prop: String)
            """.trimIndent()
        val result = compileWithCream(source)

        assertNotEquals(
            KotlinCompilation.ExitCode.OK,
            result.exitCode,
            "Compilation should fail. Output:\n${result.normalizedCompilerOutput()}",
        )
        assertMatchesSnapshot("CopyToChildrenDiagnosticTest.nonSealedClass.output") {
            facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
            "Input" facetOf source
        }
    }

    @Test
    fun `data class with @CopyToChildren fails compilation`() {
        val source =
            """
            package diag

            import me.tbsten.cream.CopyToChildren

            @CopyToChildren
            data class JustData(val prop: String)
            """.trimIndent()
        val result = compileWithCream(source)

        assertNotEquals(
            KotlinCompilation.ExitCode.OK,
            result.exitCode,
            "Compilation should fail. Output:\n${result.normalizedCompilerOutput()}",
        )
        assertMatchesSnapshot("CopyToChildrenDiagnosticTest.dataClass.output") {
            facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
            "Input" facetOf source
        }
    }

    @Test
    fun `sealed interface with @CopyToChildren compiles successfully`() {
        val result =
            compileWithCream(
                """
                package diag

                import me.tbsten.cream.CopyToChildren

                @CopyToChildren
                sealed interface State {
                    val id: String

                    data class Loading(override val id: String) : State
                    data class Loaded(override val id: String, val value: Int) : State
                }
                """.trimIndent(),
            )

        assertEquals(
            KotlinCompilation.ExitCode.OK,
            result.exitCode,
            "Compilation should succeed. Output:\n${result.normalizedCompilerOutput()}",
        )
    }
}
