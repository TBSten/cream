package me.tbsten.cream.ksp.options

import com.tschuchort.compiletesting.KotlinCompilation
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class EscapeDotOptionTest {
    // full-name strategy makes the dotted full FQN show up in the function name,
    // which lets us observe how each EscapeDot variant rewrites it.
    private val fullNameSource: String =
        """
        package opts.escape

        import me.tbsten.cream.CopyTo

        @CopyTo(Target::class)
        data class Source(val prop: String)

        data class Target(val prop: String)
        """.trimIndent()

    private fun runWithEscape(escape: String) =
        compileWithCream(
            fullNameSource,
            options =
                mapOf(
                    "cream.copyFunNamingStrategy" to "full-name",
                    "cream.escapeDot" to escape,
                ),
        )

    @Test
    fun `lower-camel-case removes dots and capitalizes each segment`() {
        val result = runWithEscape("lower-camel-case")
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        // "opts.escape.Target" -> "optsEscapeTarget" -> first lower "optsEscapeTarget"
        // prefix capitalizes -> "copyToOptsEscapeTarget"
        assertTrue(
            result.generatedSourceText().contains("copyToOptsEscapeTarget"),
            "Expected 'copyToOptsEscapeTarget'. Actual:\n${result.generatedSourceText()}",
        )
    }

    @Test
    fun `replace-to-underscore prefixes underscore and substitutes dots`() {
        val result = runWithEscape("replace-to-underscore")
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        // "opts.escape.Target" -> "_opts_escape_Target"
        // prefix "copyTo" ends with letter; first char is '_' (non-letter) -> no further capitalization
        // -> "copyTo_opts_escape_Target"
        assertTrue(
            result.generatedSourceText().contains("copyTo_opts_escape_Target"),
            "Expected 'copyTo_opts_escape_Target'. Actual:\n${result.generatedSourceText()}",
        )
    }

    @Test
    fun `backquote wraps the target name in backticks in generated source`() {
        // backquote produces the literal substring `copyTo\`Target\`` in the generated function
        // name. Note that Kotlin currently cannot parse a function declaration whose name has
        // a non-quoted prefix followed by a backquote-wrapped suffix, so downstream compilation
        // would fail — this test only verifies the KSP code-generation behavior of the option.
        val result =
            compileWithCream(
                fullNameSource,
                options =
                    mapOf(
                        "cream.copyFunNamingStrategy" to "simple-name",
                        "cream.escapeDot" to "backquote",
                    ),
            )
        // simple-name -> "Target"
        // backquote -> "`Target`"
        // first char `\`` non-letter -> no capitalize change
        // -> "copyTo`Target`" (matches CopyFunctionNameTest unit-test expectations)
        val generated = result.generatedSourceText()
        assertTrue(
            generated.contains("copyTo`Target`"),
            "Expected 'copyTo`Target`' in generated source. Actual:\n$generated",
        )
    }
}
