package me.tbsten.cream.ksp.options

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText

internal class CopyFunNamePrefixOptionTest :
    FunSpec({
        val sampleSource: String =
            """
            package opts.prefix

            import me.tbsten.cream.CopyTo

            @CopyTo(Target::class)
            data class Source(val prop: String)

            data class Target(val prop: String)
            """.trimIndent()

        test("default prefix copyTo is used when option is not set") {
            val result = compileWithCream(sampleSource)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue("Generated source should contain 'copyToTarget'. Actual:\n$generated") {
                generated shouldContain "copyToTarget"
            }
        }

        test("prefix transitionTo is used when configured") {
            val result =
                compileWithCream(
                    sampleSource,
                    options = mapOf("cream.copyFunNamePrefix" to "transitionTo"),
                )

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue("Generated source should contain 'transitionToTarget'. Actual:\n$generated") {
                generated shouldContain "transitionToTarget"
            }
            withClue("Default prefix should not appear when custom prefix is set. Actual:\n$generated") {
                generated shouldNotContain "copyToTarget"
            }
        }

        test("prefix ending with non-letter does not capitalize target name") {
            val result =
                compileWithCream(
                    sampleSource,
                    options = mapOf("cream.copyFunNamePrefix" to "to_"),
                )

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue("Generated source should contain 'to_target' (no capitalization rewrite for underscore-ending prefix). Actual:\n$generated") {
                generated shouldContain "to_target"
            }
        }
    })
