package me.tbsten.cream.ksp.options

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText

// These tests are the false-green guard for funName tokens: they run end-to-end with
// non-default cream.* naming options and prove that CopyTarget* tokens render the target
// name with a fixed strategy (independent of the options), while DefaultCopyFunctionName
// and the omitted-funName baseline DO follow the options. Target = opts.funname.Wrapper.Success
// (simpleName "Success", under-package "Wrapper.Success").
internal class FunNameTemplateOptionsTest :
    FunSpec({
        test("a CopyTarget token ignores copyFunNamingStrategy / escapeDot") {
            val source =
                """
                package opts.funname

                import me.tbsten.cream.CopyTo
                import me.tbsten.cream.CopyTargetSimpleName

                @CopyTo(Wrapper.Success::class, funName = "to" + CopyTargetSimpleName)
                data class Source(val shared: String)

                class Wrapper {
                    data class Success(val shared: String, val extra: Int)
                }
                """.trimIndent()
            val result =
                compileWithCream(
                    source,
                    options =
                        mapOf(
                            "cream.copyFunNamingStrategy" to "full-name",
                            "cream.escapeDot" to "replace-to-underscore",
                        ),
                )

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue("token must render simple name in Pascal regardless of options:\n$generated") {
                generated shouldContain ".toSuccess("
                // if the token had wrongly followed full-name + replace-to-underscore:
                generated shouldNotContain "opts_funname"
            }
        }

        test("DefaultCopyFunctionName follows copyFunNamePrefix / strategy") {
            val source =
                """
                package opts.funname

                import me.tbsten.cream.CopyTo
                import me.tbsten.cream.DefaultCopyFunctionName

                @CopyTo(Wrapper.Success::class, funName = DefaultCopyFunctionName + "OrNull")
                data class Source(val shared: String)

                class Wrapper {
                    data class Success(val shared: String, val extra: Int)
                }
                """.trimIndent()
            val result =
                compileWithCream(
                    source,
                    options =
                        mapOf(
                            "cream.copyFunNamePrefix" to "build",
                            "cream.copyFunNamingStrategy" to "simple-name",
                        ),
                )

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue("default token must use prefix 'build' + simple-name, then suffix:\n$generated") {
                generated shouldContain ".buildSuccessOrNull("
            }
        }

        test("a plain-literal funName ignores every naming option") {
            val source =
                """
                package opts.funname

                import me.tbsten.cream.CopyTo

                @CopyTo(Wrapper.Success::class, funName = "myCustomName")
                data class Source(val shared: String)

                class Wrapper {
                    data class Success(val shared: String, val extra: Int)
                }
                """.trimIndent()
            val result =
                compileWithCream(
                    source,
                    options =
                        mapOf(
                            "cream.copyFunNamePrefix" to "build",
                            "cream.copyFunNamingStrategy" to "full-name",
                            "cream.escapeDot" to "replace-to-underscore",
                        ),
                )

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue("literal name must be emitted as-is:\n$generated") {
                generated shouldContain ".myCustomName("
            }
        }

        test("omitting funName keeps following the naming options (baseline)") {
            val source =
                """
                package opts.funname

                import me.tbsten.cream.CopyTo

                @CopyTo(Wrapper.Success::class)
                data class Source(val shared: String)

                class Wrapper {
                    data class Success(val shared: String, val extra: Int)
                }
                """.trimIndent()
            val result =
                compileWithCream(
                    source,
                    options =
                        mapOf(
                            "cream.copyFunNamePrefix" to "build",
                            "cream.copyFunNamingStrategy" to "simple-name",
                        ),
                )

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            withClue("derived name must follow the options when funName is omitted:\n$generated") {
                generated shouldContain ".buildSuccess("
                generated shouldNotContain ".copyToWrapperSuccess("
            }
        }
    })
