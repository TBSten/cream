package me.tbsten.cream.ksp.diagnostic

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput

/**
 * `@CopyMapping` / `@CombineMapping` do not thread a [com.google.devtools.ksp.processing.KSPLogger]
 * into the target dispatcher yet, so a non-constructable target cannot be reported as the clean
 * `COMPILATION_ERROR` that the `@CopyTo` path produces. It must still *fail closed*: the rejection
 * throws so the invalid target reliably aborts compilation instead of being silently dropped
 * (which would generate nothing and leave the user wondering why). Threading a logger through the
 * mapping processors for a clean diagnostic is a follow-up.
 */
internal class CopyMappingTargetKindDiagnosticTest :
    FunSpec({
        test("fails compilation instead of silently skipping a sealed class target") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyMapping

                data class Source(val id: String)

                sealed class State(val id: String)

                @CopyMapping(Source::class, State::class)
                object Mapping
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.INTERNAL_ERROR
                }
                // The rejection reason still reaches the user instead of being swallowed.
                result.messages shouldContain "Unsupported target sealed class"
            }
        }
    })
