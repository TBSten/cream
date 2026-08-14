package me.tbsten.cream.ksp.feature.parentOptional

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.ksp.testing.compile.compileWithCream
import me.tbsten.cream.ksp.testing.compile.normalizedCompilerOutput
import me.tbsten.cream.ksp.testing.snapshot.assertMatchesSnapshot

/**
 * Diagnostics for invalid `@ParentOptional` usage (issue #135). Every misuse must surface as a
 * positioned `COMPILATION_ERROR` with an actionable solution — never as silently skipped or
 * mis-generated code. Covers the property-side checks (no sealed parent / inaccessible property /
 * extension property) and the accessor-merge checks shared with `@ChildOptionals` (type mismatch —
 * incl. nullable-vs-non-null and alias-vs-expansion — / parent member collision / duplicate
 * contributions from one child / type parameters the parent does not pin / forced-public
 * signatures exposing internal symbols).
 */
internal class ParentOptionalInvalidUsageTest :
    FreeSpec({
        "noSealedParent" {
            val source =
                """
                package diag.po

                import me.tbsten.cream.ParentOptional

                class Plain(@ParentOptional val data: String)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "no sealed parent type"
            }
            assertMatchesSnapshot(name = "ParentOptionalInvalidUsageTest.noSealedParent.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        "privateProperty" {
            val source =
                """
                package diag.po

                import me.tbsten.cream.ParentOptional

                sealed interface MyState {
                    class Success(data: String) : MyState {
                        @ParentOptional
                        private val secret: String = data
                    }
                    data object Loading : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "must be public or internal"
            }
            assertMatchesSnapshot(name = "ParentOptionalInvalidUsageTest.privateProperty.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        "privateChildClass" {
            val source =
                """
                package diag.po

                import me.tbsten.cream.ParentOptional

                sealed interface MyState {
                    data object Loading : MyState
                }

                private data class Hidden(
                    @ParentOptional val secret: String,
                ) : MyState
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "cannot reference"
            }
            assertMatchesSnapshot(name = "ParentOptionalInvalidUsageTest.privateChildClass.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        "mergedTypeMismatch" {
            val source =
                """
                package diag.po

                import me.tbsten.cream.ParentOptional

                sealed interface MyState {
                    data class Success(@ParentOptional val value: String) : MyState
                    data class Failure(@ParentOptional val value: Int) : MyState
                    data object Loading : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "mismatched types"
            }
            assertMatchesSnapshot(name = "ParentOptionalInvalidUsageTest.mergedTypeMismatch.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        "parentAlreadyHasMember" {
            val source =
                """
                package diag.po

                import me.tbsten.cream.ParentOptional

                sealed interface MyState {
                    val status: String

                    data class Success(
                        override val status: String,
                        @ParentOptional(propertyName = "status") val code: Int,
                    ) : MyState

                    data object Loading : MyState {
                        override val status: String = ""
                    }
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "already visible"
            }
            assertMatchesSnapshot(name = "ParentOptionalInvalidUsageTest.parentAlreadyHasMember.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        "sameChildMergedIntoOneAccessor" {
            val source =
                """
                package diag.po

                import me.tbsten.cream.ParentOptional

                sealed interface MyState {
                    data class Success(
                        @ParentOptional(propertyName = "info") val message: String,
                        @ParentOptional(propertyName = "info") val detail: String,
                    ) : MyState
                    data object Loading : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "multiple properties of the same child"
            }
            assertMatchesSnapshot(name = "ParentOptionalInvalidUsageTest.sameChildMergedIntoOneAccessor.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        "typeParameterNotPinnedByParent" {
            val source =
                """
                package diag.po

                import me.tbsten.cream.ParentOptional

                sealed interface MyState {
                    data class Success<T>(@ParentOptional val data: T) : MyState
                    data object Loading : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "not pinned"
            }
            assertMatchesSnapshot(name = "ParentOptionalInvalidUsageTest.typeParameterNotPinnedByParent.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        "extensionProperty" {
            // A member extension property has no readable value on the child instance alone; a
            // bare `ext` in the generated getter would even resolve to the accessor itself
            // (infinite recursion), so this must be rejected up front.
            val source =
                """
                package diag.po

                import me.tbsten.cream.ParentOptional

                sealed interface MyState {
                    class Success : MyState {
                        @ParentOptional
                        val String.ext: Int get() = length
                    }
                    data object Loading : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "extension property"
            }
            assertMatchesSnapshot(name = "ParentOptionalInvalidUsageTest.extensionProperty.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        "mergedNullableVsNonNullable" {
            // `String` vs `String?` is a type mismatch like any other — the accessor type must be
            // a single denotable type, and the message spells out both spellings.
            val source =
                """
                package diag.po

                import me.tbsten.cream.ParentOptional

                sealed interface MyState {
                    data class Success(@ParentOptional val value: String) : MyState
                    data class Failure(@ParentOptional val value: String?) : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "mismatched types"
            }
            assertMatchesSnapshot(name = "ParentOptionalInvalidUsageTest.mergedNullableVsNonNullable.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        "mergedTypealiasVsExpandedType" {
            // Typealiases are preserved in the generated signature (see the propertyShape
            // `typealiasPreserved` snapshot), so an aliased and a non-aliased spelling of the
            // same type do not merge — the message shows both spellings.
            val source =
                """
                package diag.po

                import me.tbsten.cream.ParentOptional

                typealias UserId = String

                sealed interface MyState {
                    data class Success(@ParentOptional val id: UserId) : MyState
                    data class Failure(@ParentOptional val id: String) : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "mismatched types"
            }
            assertMatchesSnapshot(name = "ParentOptionalInvalidUsageTest.mergedTypealiasVsExpandedType.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        "forcedPublicOnInternalParent" {
            // `visibility = PUBLIC` on an internal sealed parent would emit `'public' member
            // exposes its 'internal' receiver type` in the generated file — rejected up front.
            val source =
                """
                package diag.po

                import me.tbsten.cream.CopyVisibility
                import me.tbsten.cream.ParentOptional

                internal sealed interface MyState {
                    data class Success(
                        @ParentOptional(visibility = CopyVisibility.PUBLIC) val data: String,
                    ) : MyState
                    data object Loading : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "forced public"
            }
            assertMatchesSnapshot(name = "ParentOptionalInvalidUsageTest.forcedPublicOnInternalParent.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        "forcedPublicExposesInternalPropertyType" {
            // The receiver is public here, but the accessor's *type* (`Detail?`) is internal —
            // a public accessor would emit `'public' property exposes its 'internal' type`.
            val source =
                """
                package diag.po

                import me.tbsten.cream.CopyVisibility
                import me.tbsten.cream.ParentOptional

                internal class Detail(val x: Int)

                sealed interface MyState {
                    data object Loading : MyState
                }

                internal data class Hidden(
                    @ParentOptional(visibility = CopyVisibility.PUBLIC) val d: Detail,
                ) : MyState
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "expose the internal declaration"
            }
            assertMatchesSnapshot(name = "ParentOptionalInvalidUsageTest.forcedPublicExposesInternalPropertyType.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        "optionDefaultVisibilityPublicOnInternalParent" {
            // The same exposure rejection when public is forced by cream.defaultVisibility=PUBLIC
            // instead of the annotation — the message names the option as the culprit.
            val source =
                """
                package diag.po

                import me.tbsten.cream.ParentOptional

                internal sealed interface MyState {
                    data class Success(@ParentOptional val data: String) : MyState
                    data object Loading : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source, options = mapOf("cream.defaultVisibility" to "PUBLIC"))

            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain "cream.defaultVisibility=PUBLIC"
            }
            assertMatchesSnapshot(name = "ParentOptionalInvalidUsageTest.optionDefaultVisibilityPublicOnInternalParent.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }
    })
