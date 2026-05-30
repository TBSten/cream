package me.tbsten.cream.ksp.options

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput

internal class EscapeDotOptionTest :
    FunSpec({
        // full-name strategy makes the dotted full FQN show up in the function name,
        // which lets us observe how each EscapeDot variant rewrites it.
        val fullNameSource: String =
            """
            package opts.escape

            import me.tbsten.cream.CopyTo

            @CopyTo(Target::class)
            data class Source(val prop: String)

            data class Target(val prop: String)
            """.trimIndent()

        fun runWithEscape(escape: String) =
            compileWithCream(
                fullNameSource,
                options =
                    mapOf(
                        "cream.copyFunNamingStrategy" to "full-name",
                        "cream.escapeDot" to escape,
                    ),
            )

        test("lower-camel-case removes dots and capitalizes each segment") {
            val result = runWithEscape("lower-camel-case")
            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            // "opts.escape.Target" -> "optsEscapeTarget" -> first lower "optsEscapeTarget"
            // prefix capitalizes -> "copyToOptsEscapeTarget"
            withClue("Expected 'copyToOptsEscapeTarget'. Actual:\n${result.generatedSourceText()}") {
                result.generatedSourceText() shouldContain "copyToOptsEscapeTarget"
            }
        }

        test("replace-to-underscore prefixes underscore and substitutes dots") {
            val result = runWithEscape("replace-to-underscore")
            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            // "opts.escape.Target" -> "_opts_escape_Target"
            // prefix "copyTo" ends with letter; first char is '_' (non-letter) -> no further capitalization
            // -> "copyTo_opts_escape_Target"
            withClue("Expected 'copyTo_opts_escape_Target'. Actual:\n${result.generatedSourceText()}") {
                result.generatedSourceText() shouldContain "copyTo_opts_escape_Target"
            }
        }

        test("backquote with a non-empty prefix is rejected with a clear error") {
            // escapeDot=backquote + the default "copyTo" prefix yields the literal `copyTo`Target``
            // — not a valid Kotlin function name (a non-quoted prefix followed by a backtick-wrapped
            // suffix). cream now rejects it with a clear message pointed at the naming option,
            // instead of emitting code that fails downstream at the user's compiler.
            val result =
                compileWithCream(
                    fullNameSource,
                    options =
                        mapOf(
                            "cream.copyFunNamingStrategy" to "simple-name",
                            "cream.escapeDot" to "backquote",
                        ),
                )
            withClue("Expected compilation failure. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            withClue(result.normalizedCompilerOutput()) {
                result.normalizedCompilerOutput() shouldContain "cream.escapeDot=backquote"
            }
        }
    })
