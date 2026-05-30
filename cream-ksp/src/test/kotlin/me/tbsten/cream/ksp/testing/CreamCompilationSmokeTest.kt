package me.tbsten.cream.ksp.testing

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

internal class CreamCompilationSmokeTest :
    FunSpec({
        test("compiles a minimal @CopyTo source and generates a copy function") {
            val result =
                compileWithCream(
                    """
                    package smoke

                    import me.tbsten.cream.CopyTo

                    @CopyTo(Target::class)
                    data class Source(val shared: String)

                    data class Target(val shared: String, val extra: Int)
                    """.trimIndent(),
                )

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }

            val generatedText = result.generatedSourceText()
            withClue("Generated source should contain copyToTarget function. Generated:\n$generatedText") {
                generatedText shouldContain "copyToTarget"
            }
        }

        test("multi-source DSL compiles when source and target live in separate files") {
            val result =
                compileWithCream {
                    "Source.kt" source
                        """
                        package smoke.multi

                        import me.tbsten.cream.CopyTo

                        @CopyTo(Target::class)
                        data class Source(val shared: String)
                        """.trimIndent()
                    "Target.kt" source
                        """
                        package smoke.multi

                        data class Target(val shared: String, val extra: Int)
                        """.trimIndent()
                }

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            withClue("Multi-source compilation should still produce copyToTarget. Generated:\n${result.generatedSourceText()}") {
                result.generatedSourceText() shouldContain "copyToTarget"
            }
        }
    })
