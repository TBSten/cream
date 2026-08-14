package me.tbsten.cream.ksp.feature.parentOptional

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainOnlyOnce
import io.kotest.matchers.string.shouldNotContain
import me.tbsten.cream.ksp.testing.compile.compileWithCream
import me.tbsten.cream.ksp.testing.compile.generatedSourceText

/**
 * Happy-path examples for `@ParentOptional` (issue #135): a child property annotated with
 * `@ParentOptional` becomes a nullable extension property on every transitive sealed
 * supertype. The generated sources participate in the same compilation, so
 * `ExitCode.OK` also proves the generated accessor itself compiles.
 */
internal class ParentOptionalBasicUsageTest :
    FreeSpec({
        "sealedParentAccessor" {
            val source =
                """
                package basic.po

                import me.tbsten.cream.ParentOptional

                sealed interface MyState {
                    data class Success(@ParentOptional val data: String) : MyState
                    data object Loading : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val generated = result.generatedSourceText()
                generated shouldContain "val basic.po.MyState.data: String?"
                generated shouldContain "is basic.po.MyState.Success -> data"
                generated shouldContain "else -> null"
            }
        }

        "mergedAccessorAcrossChildren" {
            val source =
                """
                package basic.po.merge

                import me.tbsten.cream.ParentOptional

                sealed interface MyState {
                    data class Success(@ParentOptional val message: String) : MyState
                    data class Failure(@ParentOptional val message: String) : MyState
                    data object Loading : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val generated = result.generatedSourceText()
                generated shouldContainOnlyOnce "val basic.po.merge.MyState.message: String?"
                generated shouldContain "is basic.po.merge.MyState.Success -> message"
                generated shouldContain "is basic.po.merge.MyState.Failure -> message"
            }
        }

        "propertyNameOverride" {
            val source =
                """
                package basic.po.rename

                import me.tbsten.cream.ParentOptional

                sealed interface MyState {
                    data class Success(@ParentOptional(propertyName = "dataOrNull") val data: String) : MyState
                    data object Loading : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val generated = result.generatedSourceText()
                generated shouldContain "val basic.po.rename.MyState.dataOrNull: String?"
                generated shouldContain "is basic.po.rename.MyState.Success -> data"
            }
        }

        "intermediateSealedGetsOwnAccessor" {
            val source =
                """
                package basic.po.mid

                import me.tbsten.cream.ParentOptional

                sealed interface Root {
                    sealed interface Middle : Root {
                        data class Leaf(@ParentOptional val data: String) : Middle
                    }
                    data object Other : Root
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val generated = result.generatedSourceText()
                generated shouldContain "val basic.po.mid.Root.data: String?"
                generated shouldContain "val basic.po.mid.Root.Middle.data: String?"
            }
        }

        "nullablePropertyKeepsSingleQuestionMark" {
            val source =
                """
                package basic.po.nullable

                import me.tbsten.cream.ParentOptional

                sealed interface MyState {
                    data class Success(@ParentOptional val data: String?) : MyState
                    data object Loading : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val generated = result.generatedSourceText()
                generated shouldContain "val basic.po.nullable.MyState.data: String?"
                generated shouldNotContain "String??"
            }
        }

        "childOptionalsOwnedParentGeneratesOnlyOnce" {
            // Ownership rule: when the sealed parent itself carries @ChildOptionals, the
            // @ParentOptional feature must NOT emit a second (conflicting) accessor for it.
            val source =
                """
                package basic.po.own

                import me.tbsten.cream.ChildOptionals
                import me.tbsten.cream.ParentOptional

                @ChildOptionals
                sealed interface MyState {
                    data class Success(@ParentOptional val data: String) : MyState
                    data object Loading : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.generatedSourceText() shouldContainOnlyOnce "val basic.po.own.MyState.data: String?"
            }
        }
    })
