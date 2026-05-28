package me.tbsten.cream.ksp.diagnostic

import com.tschuchort.compiletesting.KotlinCompilation
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput
import kotlin.test.Test
import kotlin.test.assertNotEquals

internal class CopyToDiagnosticTest {
    @Test
    fun `@CopyTo targeting an enum class fails compilation`() {
        val result =
            compileWithCream(
                """
                package diag

                import me.tbsten.cream.CopyTo

                enum class Color { RED, BLUE }

                @CopyTo(Color::class)
                data class Source(val name: String)
                """.trimIndent(),
            )

        assertNotEquals(
            KotlinCompilation.ExitCode.OK,
            result.exitCode,
            "Compilation should fail. Output:\n${result.normalizedCompilerOutput()}",
        )
        assertMatchesSnapshot(
            "CopyToDiagnosticTest.enumTarget.output",
            result.normalizedCompilerOutput(),
            lang = "text",
        )
    }

    @Test
    fun `@CopyTo targeting a non-sealed interface fails compilation`() {
        val result =
            compileWithCream(
                """
                package diag

                import me.tbsten.cream.CopyTo

                interface Plain {
                    val id: String
                }

                @CopyTo(Plain::class)
                data class Source(val id: String)
                """.trimIndent(),
            )

        assertNotEquals(
            KotlinCompilation.ExitCode.OK,
            result.exitCode,
            "Compilation should fail. Output:\n${result.normalizedCompilerOutput()}",
        )
        assertMatchesSnapshot(
            "CopyToDiagnosticTest.nonSealedInterface.output",
            result.normalizedCompilerOutput(),
            lang = "text",
        )
    }

    @Test
    fun `@CopyTo targeting an annotation class fails compilation`() {
        val result =
            compileWithCream(
                """
                package diag

                import me.tbsten.cream.CopyTo

                annotation class Marker

                @CopyTo(Marker::class)
                data class Source(val name: String)
                """.trimIndent(),
            )

        assertNotEquals(
            KotlinCompilation.ExitCode.OK,
            result.exitCode,
            "Compilation should fail. Output:\n${result.normalizedCompilerOutput()}",
        )
        assertMatchesSnapshot(
            "CopyToDiagnosticTest.annotationTarget.output",
            result.normalizedCompilerOutput(),
            lang = "text",
        )
    }
}
