package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput

internal class CopyMappingSnapshotTest :
    FunSpec({
        test("copyMapping keeps a vararg ctor param on the target and gives it a default") {
            val source =
                """
                package snap.copymapping.vararg

                import me.tbsten.cream.CopyMapping

                class LibSource(val id: Int, val tags: Array<String>)

                class LibTarget(val id: Int, vararg val tags: String)

                @CopyMapping(LibSource::class, LibTarget::class)
                private object Mapping
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should succeed. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("CopyMappingSnapshotTest.copyMappingVararg") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }
    })
