package me.tbsten.cream.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.ksp.testing.compile.compileWithCream
import me.tbsten.cream.ksp.testing.compile.normalizedCompilerOutput
import me.tbsten.cream.ksp.testing.snapshot.assertMatchesSnapshot

/**
 * Cross-feature diagnostics: cream reports EVERY misuse in a single processing round rather than
 * aborting on the first. This locks the `logger.error`-not-`throw` design — two independent,
 * unrelated misuses (`@CopyToChildren` and `@SealedCopy`, each on a non-sealed class) must both
 * surface from one compile, so a regression to fail-fast (only the first error shown) is caught.
 */
internal class MultipleDiagnosticsTest :
    FunSpec({
        test("reportsEveryMisuseInOneRound") {
            val source =
                """
                package multi.diag

                import me.tbsten.cream.CopyToChildren
                import me.tbsten.cream.SealedCopy

                @CopyToChildren
                class NotSealedForChildren(val a: String)

                @SealedCopy
                class NotSealedForSelfCopy(val b: String)
                """.trimIndent()

            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "NotSealedForChildren"
                result.normalizedCompilerOutput() shouldContain "NotSealedForSelfCopy"
            }
            assertMatchesSnapshot(name = "MultipleDiagnosticsTest.reportsEveryMisuseInOneRound.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }
    })
