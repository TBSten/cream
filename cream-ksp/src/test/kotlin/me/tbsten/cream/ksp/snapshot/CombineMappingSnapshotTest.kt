package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput

internal class CombineMappingSnapshotTest :
    FunSpec({
        test("combineMapping keeps a vararg ctor param on the target and gives it a default") {
            val source =
                """
                package snap.combinemapping.vararg

                import me.tbsten.cream.CombineMapping

                class LibPrimary(val id: Int, val tags: Array<String>)

                class LibOther(val extra: String)

                class LibTarget(val id: Int, val extra: String, vararg val tags: String)

                @CombineMapping(
                    sources = [LibPrimary::class, LibOther::class],
                    target = LibTarget::class,
                )
                private object Mapping
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should succeed. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("CombineMappingSnapshotTest.combineMappingVararg") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }
    })
