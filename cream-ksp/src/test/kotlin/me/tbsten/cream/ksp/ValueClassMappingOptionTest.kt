package me.tbsten.cream.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import me.tbsten.cream.ksp.testing.compile.compileWithCream
import me.tbsten.cream.ksp.testing.compile.generatedSourceText

/**
 * End-to-end coverage for the project-level `cream.autoValueClassMapping` option (issue #21).
 *
 * The automatic value class mapping is ON by default (a name-matched property whose type differs
 * from the target parameter only by a single `value class` wrapper gets a wrap/unwrap default);
 * setting `cream.autoValueClassMapping=false` is the module-wide escape hatch that disables the
 * conversion entirely, leaving the parameter required. Pure Map -> options parsing is pinned by
 * [me.tbsten.cream.ksp.options.CreamOptionsParsingTest]; the per-annotation generated shapes by
 * the `valueClassMapping` snapshot families. This test deliberately does NOT add an option
 * variant to the snapshot matrix (that would multiply every family's compile count).
 */
internal class ValueClassMappingOptionTest :
    FreeSpec({
        val wrapSource =
            """
            package vcm

            import me.tbsten.cream.CopyTo

            @JvmInline
            value class DomainId(val value: String)

            @CopyTo(Target::class)
            data class Source(val id: String, val name: String)

            data class Target(val id: DomainId, val name: String)
            """.trimIndent()

        "by default a value-class name match produces a wrap default" {
            val result = compileWithCream(wrapSource)
            val generated = result.generatedSourceText()

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            withClue(generated) {
                // The param type (and thus the wrap constructor call) is rendered with the source
                // package omitted, matching the surrounding generated code.
                generated shouldContain "id: DomainId = DomainId(this.id),"
            }
        }

        "cream.autoValueClassMapping=false disables the conversion and the parameter stays required" {
            val result = compileWithCream(wrapSource, options = mapOf("cream.autoValueClassMapping" to "false"))
            val generated = result.generatedSourceText()

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            withClue(generated) {
                generated shouldNotContain "DomainId(this.id)"
                generated shouldContain "id: DomainId,"
            }
        }
    })
