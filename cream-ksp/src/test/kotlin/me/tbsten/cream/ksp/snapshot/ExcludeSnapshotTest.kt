package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText

internal class ExcludeSnapshotTest :
    FunSpec({

        // --- @CopyFrom.Exclude ---

        test("CopyFrom.Exclude removes default from matched parameter") {
            val source =
                """
                package snap.exclude

                import me.tbsten.cream.CopyFrom

                sealed interface State {
                    val name: String
                    val count: Int

                    @CopyFrom(State::class)
                    data class Success(
                        val name: String,
                        @CopyFrom.Exclude val count: Int,
                    )
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("ExcludeSnapshotTest.CopyFromExclude removes default from matched parameter") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        // --- @CopyTo.Exclude ---

        test("CopyTo.Exclude removes default from source-side matched parameter") {
            val source =
                """
                package snap.exclude

                import me.tbsten.cream.CopyTo

                @CopyTo(Target::class)
                data class Source(
                    val name: String,
                    @CopyTo.Exclude val count: Int,
                )

                data class Target(val name: String, val count: Int)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("ExcludeSnapshotTest.CopyToExclude removes default from source-side matched parameter") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        // --- @CombineFrom.Exclude ---

        test("CombineFrom.Exclude removes default only for the annotated parameter with multiple sources") {
            val source =
                """
                package snap.exclude

                import me.tbsten.cream.CombineFrom

                data class LoadingState(val itemId: String)
                data class SuccessAction(val data: String)

                @CombineFrom(LoadingState::class, SuccessAction::class)
                data class SuccessState(
                    val itemId: String,
                    @CombineFrom.Exclude val data: String,
                    val extra: Int,
                )
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("ExcludeSnapshotTest.CombineFromExclude removes default only for annotated parameter") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        // --- @CombineTo.Exclude ---

        test("CombineTo.Exclude removes default via source-side property") {
            val source =
                """
                package snap.exclude

                import me.tbsten.cream.CombineTo

                @CombineTo(SuccessState::class)
                data class LoadingState(
                    val itemId: String,
                    @CombineTo.Exclude val sessionId: String,
                )

                data class SuccessState(
                    val itemId: String,
                    val sessionId: String,
                    val extra: Int,
                )
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("ExcludeSnapshotTest.CombineToExclude removes default via source-side property") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        // --- @SealedCopy.Exclude ---

        test("SealedCopy.Exclude removes default from abstract property") {
            val source =
                """
                package snap.exclude

                import me.tbsten.cream.SealedCopy

                @SealedCopy
                sealed interface MyState {
                    val name: String
                    @SealedCopy.Exclude val count: Int
                    data class Loading(override val name: String, override val count: Int) : MyState
                    data class Success(
                        override val name: String,
                        override val count: Int,
                        val data: String,
                    ) : MyState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("ExcludeSnapshotTest.SealedCopyExclude removes default from abstract property") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        // --- @CopyToChildren.Exclude ---

        test("CopyToChildren.Exclude removes default uniformly across all copyToCn functions") {
            val source =
                """
                package snap.exclude

                import me.tbsten.cream.CopyToChildren

                @CopyToChildren
                sealed interface UiState {
                    val sessionId: String
                    @CopyToChildren.Exclude val count: Int
                    data class Loading(override val sessionId: String, override val count: Int) : UiState
                    data class Success(
                        override val sessionId: String,
                        override val count: Int,
                        val data: String,
                    ) : UiState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("ExcludeSnapshotTest.CopyToChildrenExclude removes default uniformly across all copyToCn") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        // --- annotation-scoped: @SealedCopy.Exclude and @CopyToChildren.Exclude coexist ---

        test("SealedCopy.Exclude and CopyToChildren.Exclude coexist annotation-scoped") {
            val source =
                """
                package snap.exclude

                import me.tbsten.cream.SealedCopy
                import me.tbsten.cream.CopyToChildren

                @SealedCopy
                @CopyToChildren
                sealed interface DualState {
                    val name: String
                    @SealedCopy.Exclude val count: Int
                    @CopyToChildren.Exclude val score: Int
                    data class A(
                        override val name: String,
                        override val count: Int,
                        override val score: Int,
                    ) : DualState
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("ExcludeSnapshotTest.SealedCopy and CopyToChildren coexist annotation-scoped") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        // --- edge: partial exclusion ---

        test("only excluded parameter loses its default while others are preserved") {
            val source =
                """
                package snap.exclude

                import me.tbsten.cream.CopyFrom

                sealed interface S {
                    val a: String
                    val b: Int
                    val c: Boolean

                    @CopyFrom(S::class)
                    data class T(
                        val a: String,
                        @CopyFrom.Exclude val b: Int,
                        val c: Boolean,
                    )
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("ExcludeSnapshotTest.edgeCase/partial exclusion preserves other defaults") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        // --- edge: .Map × .Exclude ---

        test("Exclude combined with Map on a renamed parameter removes its default") {
            val source =
                """
                package snap.exclude

                import me.tbsten.cream.CopyFrom

                sealed interface S {
                    val count: Int

                    @CopyFrom(S::class)
                    data class T(
                        @CopyFrom.Map("count") @CopyFrom.Exclude val num: Int,
                    )
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("ExcludeSnapshotTest.edgeCase/Map and Exclude combined remove default") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        // --- edge: generics ---

        test("generic type parameter with Exclude becomes required with preserved type") {
            val source =
                """
                package snap.exclude

                import me.tbsten.cream.CopyFrom

                sealed interface Result<E> {
                    val items: List<E>
                    val label: String

                    @CopyFrom(Result::class)
                    data class Success<E>(
                        override val label: String,
                        @CopyFrom.Exclude override val items: List<E>,
                    ) : Result<E>
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("ExcludeSnapshotTest.edgeCase/generic type Exclude preserves type") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        // --- edge: excluded (required) parameter is FIRST in the constructor ---

        test("excluded first parameter keeps constructor order") {
            val source =
                """
                package snap.exclude

                import me.tbsten.cream.CopyFrom

                data class Src(val id: String, val name: String, val count: Int)

                @CopyFrom(Src::class)
                data class Dst(
                    @CopyFrom.Exclude val id: String,
                    val name: String,
                    val count: Int,
                )
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("ExcludeSnapshotTest.edgeCase/excluded first parameter keeps order") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        // --- edge: source-side @CopyTo.Map combined with @CopyTo.Exclude ---

        test("CopyTo Map and Exclude combined on a renamed source property") {
            val source =
                """
                package snap.exclude

                import me.tbsten.cream.CopyTo

                @CopyTo(Target::class)
                data class Source(
                    @CopyTo.Map("renamed") @CopyTo.Exclude val original: String,
                    val keep: Int,
                )

                data class Target(val renamed: String, val keep: Int)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("ExcludeSnapshotTest.edgeCase/CopyTo Map and Exclude combined") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        // --- edge: excluded target parameter that has its own default value ---

        test("Exclude on parameter with target default value drops the target default") {
            val source =
                """
                package snap.exclude

                import me.tbsten.cream.CopyFrom

                data class Src(val name: String, val count: Int)

                @CopyFrom(Src::class)
                data class Dst(
                    val name: String,
                    @CopyFrom.Exclude val count: Int = 10,
                )
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("ExcludeSnapshotTest.edgeCase/Exclude on parameter with target default value") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        // --- edge: nullable ---

        test("nullable type with Exclude becomes required without null default") {
            val source =
                """
                package snap.exclude

                import me.tbsten.cream.CopyFrom

                sealed interface S {
                    val label: String?

                    @CopyFrom(S::class)
                    data class T(
                        @CopyFrom.Exclude val label: String?,
                    )
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("ExcludeSnapshotTest.edgeCase/nullable type Exclude no null default") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        // --- edge: all parameters excluded ---

        test("all parameters excluded makes all required") {
            val source =
                """
                package snap.exclude

                import me.tbsten.cream.CopyFrom

                sealed interface S {
                    val a: String
                    val b: Int

                    @CopyFrom(S::class)
                    data class T(
                        @CopyFrom.Exclude val a: String,
                        @CopyFrom.Exclude val b: Int,
                    )
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("ExcludeSnapshotTest.edgeCase/all parameters excluded makes all required") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        // --- warning: unmatched Exclude ---

        test("Exclude on unmatched parameter has no effect and emits warning") {
            val source =
                """
                package snap.exclude

                import me.tbsten.cream.CopyFrom

                sealed interface S {
                    val name: String

                    @CopyFrom(S::class)
                    data class T(
                        val name: String,
                        @CopyFrom.Exclude val unmatched: Int,
                    )
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            result.messages shouldContain "has no effect"
        }

        // --- regression: @CombineTo multi-target no-op exclude warns exactly once ---

        test("CombineTo Exclude unmatched in all targets warns exactly once") {
            val source =
                """
                package snap.exclude

                import me.tbsten.cream.CombineTo

                @CombineTo(TargetA::class, TargetB::class)
                data class Src(
                    val name: String,
                    @CombineTo.Exclude val ghost: Int,
                )
                data class TargetA(val name: String)
                data class TargetB(val name: String)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            result.messages.lineSequence().count { it.contains("has no effect") } shouldBe 1
        }

        // --- regression: @CombineTo multi-target exclude matched in one target is not a no-op ---

        test("CombineTo Exclude matched in one of several targets does not warn") {
            val source =
                """
                package snap.exclude

                import me.tbsten.cream.CombineTo

                @CombineTo(WithCount::class, NoCount::class)
                data class Src(
                    val name: String,
                    @CombineTo.Exclude val count: Int,
                )
                data class WithCount(val name: String, val count: Int)
                data class NoCount(val name: String)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            result.messages.lineSequence().count { it.contains("has no effect") } shouldBe 0
            // The exclude has a real effect on the matching target's generated function.
            result.generatedSourceText() shouldContain "count: Int,\n) : snap.exclude.WithCount"
        }

        // --- regression: @CombineTo exclude on an overlapping source applies to every function ---

        test("CombineTo Exclude on overlapping source removes default in every generated function") {
            val source =
                """
                package snap.exclude

                import me.tbsten.cream.CombineTo

                @CombineTo(OverlapTarget::class)
                data class SourceA(
                    @CombineTo.Exclude val shared: String,
                    val uniqueA: Int,
                )
                @CombineTo(OverlapTarget::class)
                data class SourceB(
                    val shared: String,
                    val uniqueB: Boolean,
                )
                data class OverlapTarget(val shared: String, val uniqueA: Int, val uniqueB: Boolean)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            // `shared` must be required (no `= ...shared` default) in BOTH generated functions,
            // regardless of which source "wins" the overlap.
            result.generatedSourceText() shouldNotContain "shared: String ="
        }

        // --- regression: @CopyFrom on a sealed target warns for an unmatched subclass exclude ---

        test("CopyFrom on sealed target warns once for an unmatched Exclude parameter") {
            val source =
                """
                package snap.exclude

                import me.tbsten.cream.CopyFrom

                data class Src(val name: String)

                @CopyFrom(Src::class)
                sealed interface Dst {
                    data class Leaf(
                        val name: String,
                        @CopyFrom.Exclude val ghost: Int,
                    ) : Dst
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            result.messages.lineSequence().count { it.contains("has no effect") } shouldBe 1
        }
    })
