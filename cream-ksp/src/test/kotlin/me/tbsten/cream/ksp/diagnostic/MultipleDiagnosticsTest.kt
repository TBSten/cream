package me.tbsten.cream.ksp.diagnostic

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput

/**
 * cream runs ~8 processors sequentially in one round. Because every user-misuse error is reported
 * per-site via `logger.error` (not a raw `throw`), the processors run to completion and KSP
 * collects *all* diagnostics in a single compilation — a `throw` would abort the round and hide the
 * remaining errors. This locks that guarantee in: two independent misuses, handled by two different
 * processors, are both reported as clean `COMPILATION_ERROR`s, and nothing is generated.
 */
internal class MultipleDiagnosticsTest :
    FunSpec({
        test("independent misuses across processors are all reported, none swallowed") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyToChildren
                import me.tbsten.cream.SealedCopy

                @CopyToChildren
                class NotSealedChildren(val a: String)

                @SealedCopy
                class NotSealedCopy(val b: String)
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
                }
                // Both diagnostics are present — neither processor's error aborted the other.
                result.messages shouldContain "NotSealedChildren"
                result.messages shouldContain "NotSealedCopy"
                result.generatedSources().shouldBeEmpty()
            }
        }
    })
