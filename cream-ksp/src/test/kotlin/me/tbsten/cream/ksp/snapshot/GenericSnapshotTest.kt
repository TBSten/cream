package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText

internal class GenericSnapshotTest :
    FunSpec({
        test("copyFrom with a single type parameter generates expected source") {
            val source =
                """
                package snap.generic

                import me.tbsten.cream.CopyFrom

                @CopyFrom(Source::class)
                data class Target<T>(
                    val value: T,
                    val label: String,
                )

                data class Source<T>(
                    val value: T,
                    val label: String,
                )
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("GenericSnapshotTest.copyFromGeneric") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }
    })
