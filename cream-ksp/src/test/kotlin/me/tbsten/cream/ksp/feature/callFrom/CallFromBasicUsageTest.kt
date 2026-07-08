package me.tbsten.cream.ksp.feature.callFrom

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.ksp.testing.compile.compileWithCream
import me.tbsten.cream.ksp.testing.compile.generatedSourceText

internal class CallFromBasicUsageTest :
    FreeSpec({
        "top-level 関数に args ホルダーを第 1 引数に取る同名オーバーロードが生成される" {
            val source =
                """
                package basic.cf

                import me.tbsten.cream.CallFrom

                data class ProcessDataArgs(val data1: String, val data2: Int)

                @CallFrom(ProcessDataArgs::class)
                fun processData(data1: String, data2: Int): String = "${'$'}data1-${'$'}data2"
                """.trimIndent()
            val result = compileWithCream(source)
            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.generatedSourceText() shouldContain "processDataArgs: ProcessDataArgs"
                result.generatedSourceText() shouldContain "data1: String = processDataArgs.data1"
                result.generatedSourceText() shouldContain "data2: Int = processDataArgs.data2"
            }
        }

        "生成された body はプロパティでなくパラメータを渡す (override が効く形)" {
            val source =
                """
                package basic.cf.body

                import me.tbsten.cream.CallFrom

                data class Args(val value: String)

                @CallFrom(Args::class)
                fun handle(value: String) { }
                """.trimIndent()
            val result = compileWithCream(source)
            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.generatedSourceText() shouldContain "value = value"
            }
        }

        "member 関数は enclosing クラスの拡張関数として生成される" {
            val source =
                """
                package basic.cf.member

                import me.tbsten.cream.CallFrom

                data class ProcessArgs(val value: String)

                class DataProcessor {
                    @CallFrom(ProcessArgs::class)
                    fun process(value: String): String = value
                }
                """.trimIndent()
            val result = compileWithCream(source)
            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.generatedSourceText() shouldContain "fun basic.cf.member.DataProcessor.process("
                result.generatedSourceText() shouldContain "value: String = processArgs.value"
            }
        }

        "suspend 修飾子と戻り値の型が転写される" {
            val source =
                """
                package basic.cf.suspendfun

                import me.tbsten.cream.CallFrom

                data class LoadArgs(val id: String)

                @CallFrom(LoadArgs::class)
                suspend fun load(id: String): Int = id.length
                """.trimIndent()
            val result = compileWithCream(source)
            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.generatedSourceText() shouldContain "suspend fun load("
                result.generatedSourceText() shouldContain "): Int"
            }
        }

        "sources が複数なら source ごとに 1 オーバーロード生成される" {
            val source =
                """
                package basic.cf.multi

                import me.tbsten.cream.CallFrom

                data class ArgsA(val value: String)
                data class ArgsB(val value: String)

                @CallFrom(ArgsA::class, ArgsB::class)
                fun consume(value: String) { }
                """.trimIndent()
            val result = compileWithCream(source)
            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.generatedSourceText() shouldContain "argsA: ArgsA"
                result.generatedSourceText() shouldContain "argsB: ArgsB"
            }
        }
    })
