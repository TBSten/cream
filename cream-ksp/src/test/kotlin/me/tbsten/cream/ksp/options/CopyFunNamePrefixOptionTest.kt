package me.tbsten.cream.ksp.options

import com.tschuchort.compiletesting.KotlinCompilation
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class CopyFunNamePrefixOptionTest {
    private val sampleSource: String =
        """
        package opts.prefix

        import me.tbsten.cream.CopyTo

        @CopyTo(Target::class)
        data class Source(val prop: String)

        data class Target(val prop: String)
        """.trimIndent()

    @Test
    fun `default prefix copyTo is used when option is not set`() {
        val result = compileWithCream(sampleSource)

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        val generated = result.generatedSourceText()
        assertTrue(
            generated.contains("copyToTarget"),
            "Generated source should contain 'copyToTarget'. Actual:\n$generated",
        )
    }

    @Test
    fun `prefix transitionTo is used when configured`() {
        val result =
            compileWithCream(
                sampleSource,
                options = mapOf("cream.copyFunNamePrefix" to "transitionTo"),
            )

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        val generated = result.generatedSourceText()
        assertTrue(
            generated.contains("transitionToTarget"),
            "Generated source should contain 'transitionToTarget'. Actual:\n$generated",
        )
        assertFalse(
            generated.contains("copyToTarget"),
            "Default prefix should not appear when custom prefix is set. Actual:\n$generated",
        )
    }

    @Test
    fun `prefix ending with non-letter does not capitalize target name`() {
        val result =
            compileWithCream(
                sampleSource,
                options = mapOf("cream.copyFunNamePrefix" to "to_"),
            )

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        val generated = result.generatedSourceText()
        assertTrue(
            generated.contains("to_target"),
            "Generated source should contain 'to_target' (no capitalization rewrite for underscore-ending prefix). Actual:\n$generated",
        )
    }
}
