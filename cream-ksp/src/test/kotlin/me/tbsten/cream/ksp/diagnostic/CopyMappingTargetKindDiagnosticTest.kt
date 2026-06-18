package me.tbsten.cream.ksp.diagnostic

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput

/**
 * `@CopyMapping` reuses the same target dispatcher ([me.tbsten.cream.ksp.core.copyFun.appendCopyFunction])
 * as `@CopyTo` / `@CopyFrom`, so a non-constructable target (abstract / enum class, plain interface, …)
 * must be rejected with the same clean, positioned `COMPILATION_ERROR` rather than an `INTERNAL_ERROR`
 * crash. The processor threads its [com.google.devtools.ksp.processing.KSPLogger] into the dispatcher,
 * so the target-kind rejection (`reportRejection` → `logger.error`) fires for the mapping path too.
 * The rejection also fails closed: nothing is generated for the invalid mapping.
 *
 * Note: a *sealed* class is no longer an invalid target — it fans out to its concrete subclasses
 * (see [me.tbsten.cream.ksp.snapshot.SealedSnapshotTest] / [me.tbsten.cream.ksp.snapshot.KindMatrixSnapshotTest]),
 * so this test uses a plain `abstract class` to exercise the non-constructable rejection path.
 */
internal class CopyMappingTargetKindDiagnosticTest :
    FunSpec({
        test("rejects an abstract class target with a clean diagnostic and generates nothing") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyMapping

                data class Source(val id: String)

                abstract class Base(val id: String)

                @CopyMapping(Source::class, Base::class)
                object Mapping
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
                }
                // The rejection reason still reaches the user instead of being swallowed.
                result.messages shouldContain "Unsupported target abstract class"
                // A rejected target must not leave a half-written generated file behind.
                result.generatedSources().shouldBeEmpty()
            }
            assertMatchesSnapshot("CopyMappingTargetKindDiagnosticTest.abstractClass.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }
    })
