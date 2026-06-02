package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput

internal class CombineFromSnapshotTest :
    FunSpec({
        test("combineFrom keeps a vararg ctor param on the target and gives it a default") {
            val source =
                """
                package snap.combinefrom.vararg

                import me.tbsten.cream.CombineFrom

                class Primary(val id: Int, vararg val tags: String)

                class Other(val extra: String)

                @CombineFrom(Primary::class, Other::class)
                class Target(val id: Int, val extra: String, vararg val tags: String)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should succeed. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("CombineFromSnapshotTest.combineFromVararg") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }
    })
