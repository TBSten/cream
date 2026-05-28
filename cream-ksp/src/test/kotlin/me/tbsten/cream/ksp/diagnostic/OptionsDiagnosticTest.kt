package me.tbsten.cream.ksp.diagnostic

import com.tschuchort.compiletesting.KotlinCompilation
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class OptionsDiagnosticTest {
    private val validSource: String =
        """
        package diag

        import me.tbsten.cream.CopyTo

        @CopyTo(Target::class)
        data class Source(val prop: String)

        data class Target(val prop: String)
        """.trimIndent()

    @Test
    fun `invalid copyFunNamingStrategy fails compilation with helpful message`() {
        val result =
            compileWithCream(
                validSource,
                options = mapOf("cream.copyFunNamingStrategy" to "not-a-strategy"),
            )

        assertNotEquals(
            KotlinCompilation.ExitCode.OK,
            result.exitCode,
            "Compilation should fail. Output:\n${result.normalizedCompilerOutput()}",
        )
        assertMatchesSnapshot(
            "OptionsDiagnosticTest.invalidCopyFunNamingStrategy.output",
            result.normalizedCompilerOutput(),
            lang = "text",
        )
    }

    @Test
    fun `invalid escapeDot fails compilation with helpful message`() {
        val result =
            compileWithCream(
                validSource,
                options = mapOf("cream.escapeDot" to "not-an-escape"),
            )

        assertNotEquals(
            KotlinCompilation.ExitCode.OK,
            result.exitCode,
            "Compilation should fail. Output:\n${result.normalizedCompilerOutput()}",
        )
        assertMatchesSnapshot(
            "OptionsDiagnosticTest.invalidEscapeDot.output",
            result.normalizedCompilerOutput(),
            lang = "text",
        )
    }

    @Test
    fun `valid copyFunNamingStrategy value compiles successfully`() {
        val result =
            compileWithCream(
                validSource,
                options = mapOf("cream.copyFunNamingStrategy" to "full-name"),
            )

        assertEquals(
            KotlinCompilation.ExitCode.OK,
            result.exitCode,
            "Compilation should succeed. Output:\n${result.normalizedCompilerOutput()}",
        )
    }
}
