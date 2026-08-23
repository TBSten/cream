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
 * The automatic value class mapping is OPT-IN: without the option a property whose type differs
 * from the name-matched target parameter only by a `value class` wrapper leaves that parameter
 * required, exactly as it was before the feature existed. Setting `cream.autoValueClassMapping=true`
 * turns the wrap/unwrap default on for the whole module. Pure Map -> options parsing is pinned by
 * [me.tbsten.cream.ksp.options.CreamOptionsParsingTest]; the per-annotation generated shapes by
 * the `valueClassMapping` snapshot families, which ask for the option explicitly. This test
 * deliberately does NOT add an option variant to the snapshot matrix (that would multiply every
 * family's compile count).
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

        "by default the conversion is off and the parameter stays required" {
            val result = compileWithCream(wrapSource)
            val generated = result.generatedSourceText()

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            withClue(generated) {
                generated shouldNotContain "DomainId(this.id)"
                generated shouldContain "id: DomainId,"
            }
        }

        "cream.autoValueClassMapping=true enables the conversion and a value-class name match produces a wrap default" {
            val result = compileWithCream(wrapSource, options = mapOf("cream.autoValueClassMapping" to "true"))
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
    })
