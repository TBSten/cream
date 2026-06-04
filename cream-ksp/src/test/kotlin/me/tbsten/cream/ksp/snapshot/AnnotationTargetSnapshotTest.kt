package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText

/**
 * An annotation class has a real, callable primary constructor, so it is a valid copy target.
 * `@CombineTo` already accepted annotation classes; `@CopyTo` / `@CopyFrom` now do too instead of
 * rejecting them. The generated copy function constructs the annotation just like a data class.
 */
internal class AnnotationTargetSnapshotTest :
    FunSpec({
        test("generates a copy function for an annotation class target via copyTo") {
            val source =
                """
                package snap.annotation.copyTo

                import me.tbsten.cream.CopyTo

                @CopyTo(AnnTarget::class)
                data class Source(val x: Int)

                annotation class AnnTarget(val x: Int)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("AnnotationTargetSnapshotTest.copyTo") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        test("generates a copy function for an annotation class target via copyFrom") {
            val source =
                """
                package snap.annotation.copyFrom

                import me.tbsten.cream.CopyFrom

                data class Source(val x: Int)

                @CopyFrom(Source::class)
                annotation class AnnTarget(val x: Int)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("AnnotationTargetSnapshotTest.copyFrom") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }
    })
