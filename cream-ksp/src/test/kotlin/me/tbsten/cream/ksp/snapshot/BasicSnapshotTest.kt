package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText

internal class BasicSnapshotTest :
    FunSpec({
        test("copyTo class generates expected source") {
            val source =
                """
                package snap.basic

                import me.tbsten.cream.CopyTo

                @CopyTo(Target::class)
                data class Source(
                    val shared: String,
                    val onlyOnSource: Int,
                )

                data class Target(
                    val shared: String,
                    val onlyOnTarget: Boolean,
                )
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("BasicSnapshotTest.copyTo") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }
    })
