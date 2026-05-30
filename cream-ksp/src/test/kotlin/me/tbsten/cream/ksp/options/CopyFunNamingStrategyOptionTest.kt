package me.tbsten.cream.ksp.options

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText

internal class CopyFunNamingStrategyOptionTest :
    FunSpec({
        val sampleSource: String =
            """
            package opts.strategy

            import me.tbsten.cream.CopyTo

            @CopyTo(Target::class)
            data class Source(val prop: String)

            data class Target(val prop: String)
            """.trimIndent()

        fun runWithStrategy(strategy: String) =
            compileWithCream(
                sampleSource,
                options = mapOf("cream.copyFunNamingStrategy" to strategy),
            )

        test("under-package strategy uses target name relative to package") {
            val result = runWithStrategy("under-package")
            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            // under-package: "opts.strategy.Target" minus "opts.strategy." = "Target"
            // escape lower-camel-case: "Target" -> "target"
            // prefix "copyTo" capitalizes -> "copyToTarget"
            withClue("Expected 'copyToTarget'. Actual:\n${result.generatedSourceText()}") {
                result.generatedSourceText() shouldContain "copyToTarget"
            }
        }

        test("simple-name strategy uses target simpleName") {
            val result = runWithStrategy("simple-name")
            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            withClue("Expected 'copyToTarget'. Actual:\n${result.generatedSourceText()}") {
                result.generatedSourceText() shouldContain "copyToTarget"
            }
        }

        test("full-name strategy includes the package in the function name") {
            val result = runWithStrategy("full-name")
            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            // full-name: "opts.strategy.Target"
            // escape lower-camel-case: -> "optsStrategyTarget"
            // prefix capitalizes -> "copyToOptsStrategyTarget"
            withClue("Expected 'copyToOptsStrategyTarget'. Actual:\n${result.generatedSourceText()}") {
                result.generatedSourceText() shouldContain "copyToOptsStrategyTarget"
            }
        }

        test("diff strategy uses difference between source and target") {
            val result = runWithStrategy("diff")
            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            // common prefix "opts.strategy." then S vs T differ -> diff = "Target"
            // escape lower-camel-case "Target" -> "target"
            // prefix capitalize -> "copyToTarget"
            withClue("Expected 'copyToTarget'. Actual:\n${result.generatedSourceText()}") {
                result.generatedSourceText() shouldContain "copyToTarget"
            }
        }

        test("inner-name strategy drops the leading segment of underPackageName") {
            val result =
                compileWithCream(
                    """
                    package opts.strategy

                    import me.tbsten.cream.CopyTo

                    data class Outer(val prop: String) {
                        @CopyTo(Inner::class)
                        data class Source(val prop: String)

                        data class Inner(val prop: String)
                    }
                    """.trimIndent(),
                    options = mapOf("cream.copyFunNamingStrategy" to "inner-name"),
                )
            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            // inner-name for Outer.Inner: underPackageName "Outer.Inner".split(".") -> ["Outer","Inner"], size>1
            // subList(1, 2) -> ["Inner"], joinToString(".") -> "Inner"
            // escape lower-camel-case "Inner" -> "inner"
            // prefix capitalize -> "copyToInner"
            withClue("Expected 'copyToInner'. Actual:\n${result.generatedSourceText()}") {
                result.generatedSourceText() shouldContain "copyToInner"
            }
        }
    })
