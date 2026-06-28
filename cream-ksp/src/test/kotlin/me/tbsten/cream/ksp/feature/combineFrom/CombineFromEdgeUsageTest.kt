package me.tbsten.cream.ksp.feature.combineFrom

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.ksp.testing.compile.compileWithCream
import me.tbsten.cream.ksp.testing.compile.generatedSourceText

/**
 * Rare/semantic `@CombineFrom` cases. `@CombineFrom` is `@Repeatable`, so stacking it produces one
 * combine function per occurrence (issue #134); the snapshot suite pins the exact generated source,
 * while these example-based cases pin the observable semantics — the stacked functions coexist and
 * the whole unit compiles.
 *
 * TODO(#127): also cover the remaining semantic cases (@Map / @Exclude across sources).
 */
internal class CombineFromEdgeUsageTest :
    FreeSpec({
        "stacked @CombineFrom sharing a primary source generates one overload per occurrence" {
            // Both occurrences share the receiver (Loading) but combine a different second source, so
            // they emit two `copyToState` overloads distinguished by their leading parameter type.
            // Compiling proves they coexist (a redeclaration would fail to compile).
            val result =
                compileWithCream(
                    """
                    package edge

                    import me.tbsten.cream.CombineFrom

                    data class Loading(val message: String)
                    data class Success(val successData: String)
                    data class Special(val specialData: Int)

                    @CombineFrom(Loading::class, Success::class)
                    @CombineFrom(Loading::class, Special::class)
                    data class State(
                        val message: String,
                        val successData: String,
                        val specialData: Int,
                    )
                    """.trimIndent(),
                )

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue("Generated:\n$generated") {
                // one overload takes Success, the other takes Special — both named copyToState
                generated shouldContain "Success"
                generated shouldContain "Special"
                Regex("""fun [^\n]*\.copyToState\(""").findAll(generated).count() shouldBe 2
            }
        }

        "stacked @CombineFrom with distinct funNames generates each separately named function" {
            val result =
                compileWithCream(
                    """
                    package edge

                    import me.tbsten.cream.CombineFrom

                    data class Left(val left: String)
                    data class Right(val right: Int)

                    @CombineFrom(Left::class, funName = "fromLeft")
                    @CombineFrom(Right::class, funName = "fromRight")
                    data class LeftRight(
                        val left: String,
                        val right: Int,
                    )
                    """.trimIndent(),
                )

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue("Generated:\n$generated") {
                generated shouldContain "fromLeft"
                generated shouldContain "fromRight"
            }
        }
    })
