package me.tbsten.cream.ksp.options

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText

internal class NotCopyToObjectOptionTest :
    FunSpec({
        // @CombineTo is the path that actually consults options.notCopyToObject (Transform.kt:94),
        // so we exercise it here. @CopyToChildren has its own annotation parameter for the same
        // intent, which is covered by the existing test/ module.
        val combineToSource: String =
            """
            package opts.notobj

            import me.tbsten.cream.CombineTo

            @CombineTo(Singleton::class)
            data class Source(val prop: String)

            data object Singleton
            """.trimIndent()

        test("default behavior generates combine function to object target") {
            val result = compileWithCream(combineToSource)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue("Default behavior should include 'copyToSingleton' for the data object target. Actual:\n$generated") {
                generated shouldContain "copyToSingleton"
            }
        }

        test("cream notCopyToObject=true suppresses combine function to data object") {
            val result =
                compileWithCream(
                    combineToSource,
                    options = mapOf("cream.notCopyToObject" to "true"),
                )

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue("'copyToSingleton' should be suppressed when cream.notCopyToObject=true. Actual:\n$generated") {
                generated shouldNotContain "copyToSingleton"
            }
        }
    })
