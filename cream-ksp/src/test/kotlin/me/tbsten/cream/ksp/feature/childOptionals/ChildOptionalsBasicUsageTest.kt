package me.tbsten.cream.ksp.feature.childOptionals

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import me.tbsten.cream.ksp.testing.compile.compileWithCream
import me.tbsten.cream.ksp.testing.compile.generatedSourceText

/**
 * Happy-path examples for `@ChildOptionals` (issue #135): annotating the sealed parent
 * generates a nullable extension property on the parent for every eligible property declared
 * by its transitive concrete leaves. The generated sources participate in the same
 * compilation, so `ExitCode.OK` also proves the generated accessors compile.
 */
internal class ChildOptionalsBasicUsageTest :
    FreeSpec({
        "generatesAccessorsForLeafProperties" {
            val source =
                """
                package basic.co

                import me.tbsten.cream.ChildOptionals

                @ChildOptionals
                sealed interface MyState {
                    data class Success(val data: String) : MyState
                    data class Failure(val error: Int) : MyState
                    data object Loading : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val generated = result.generatedSourceText()
                generated shouldContain "val basic.co.MyState.data: String?"
                generated shouldContain "is basic.co.MyState.Success -> data"
                generated shouldContain "val basic.co.MyState.error: Int?"
                generated shouldContain "is basic.co.MyState.Failure -> error"
            }
        }

        "skipsPropertiesVisibleOnParent" {
            val source =
                """
                package basic.co.parentprop

                import me.tbsten.cream.ChildOptionals

                @ChildOptionals
                sealed interface MyState {
                    val shared: String

                    data class Success(override val shared: String, val data: String) : MyState
                    data object Loading : MyState {
                        override val shared: String = ""
                    }
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val generated = result.generatedSourceText()
                generated shouldContain "val basic.co.parentprop.MyState.data: String?"
                generated shouldNotContain "val basic.co.parentprop.MyState.shared"
            }
        }

        "skipsPrivatePropertiesSilently" {
            val source =
                """
                package basic.co.priv

                import me.tbsten.cream.ChildOptionals

                @ChildOptionals
                sealed interface MyState {
                    data class Success(val data: String) : MyState {
                        private val secret: String = data
                    }
                    data object Loading : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val generated = result.generatedSourceText()
                generated shouldContain "val basic.co.priv.MyState.data: String?"
                generated shouldNotContain "secret"
            }
        }

        "respectsParentOptionalOverridesInMerge" {
            val source =
                """
                package basic.co.interop

                import me.tbsten.cream.ChildOptionals
                import me.tbsten.cream.ParentOptional

                @ChildOptionals
                sealed interface MyState {
                    data class Success(@ParentOptional(propertyName = "dataOrNull") val data: String) : MyState
                    data object Loading : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val generated = result.generatedSourceText()
                generated shouldContain "val basic.co.interop.MyState.dataOrNull: String?"
                generated shouldContain "is basic.co.interop.MyState.Success -> data"
            }
        }

        "overrideWithRenameGeneratesRenamedAccessor" {
            val source =
                """
                package basic.co.rename

                import me.tbsten.cream.ChildOptionals
                import me.tbsten.cream.ParentOptional

                @ChildOptionals
                sealed interface MyState {
                    val data: String

                    data class Success(
                        @ParentOptional(propertyName = "dataOrNull") override val data: String,
                    ) : MyState
                    data object Loading : MyState {
                        override val data: String = ""
                    }
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val generated = result.generatedSourceText()
                // The parent-visible original name gets no accessor, but the explicit rename does.
                generated shouldContain "val basic.co.rename.MyState.dataOrNull: String?"
                generated shouldContain "is basic.co.rename.MyState.Success -> data"
                generated shouldNotContain "val basic.co.rename.MyState.data:"
            }
        }

        "sweepsIntermediateSealedOwnProperties" {
            val source =
                """
                package basic.co.mid

                import me.tbsten.cream.ChildOptionals
                import me.tbsten.cream.ParentOptional

                @ChildOptionals
                sealed interface Root {
                    sealed class Middle(
                        @ParentOptional val session: String,
                    ) : Root

                    class Leaf(session: String, val extra: Int) : Middle(session)
                    data object Other : Root
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val generated = result.generatedSourceText()
                // The intermediate sealed type's own @ParentOptional property is swept up;
                // one `is Middle` branch covers every leaf below it.
                generated shouldContain "val basic.co.mid.Root.session: String?"
                generated shouldContain "is basic.co.mid.Root.Middle -> session"
                // Leaf-declared properties keep being swept as usual.
                generated shouldContain "val basic.co.mid.Root.extra: Int?"
            }
        }

        "recursesThroughIntermediateSealed" {
            val source =
                """
                package basic.co.nested

                import me.tbsten.cream.ChildOptionals

                @ChildOptionals
                sealed interface Root {
                    sealed interface Middle : Root {
                        data class Leaf(val data: String) : Middle
                    }
                    data object Other : Root
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val generated = result.generatedSourceText()
                generated shouldContain "val basic.co.nested.Root.data: String?"
                generated shouldContain "is basic.co.nested.Root.Middle.Leaf -> data"
            }
        }
    })
