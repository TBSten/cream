package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput

/**
 * Snapshot tests covering `@Deprecated` propagation onto generated copy functions (issue #103).
 *
 * When the source class or one of the source properties referenced by the generated function is
 * `@Deprecated`, the generated copy function references deprecated symbols and would otherwise emit
 * deprecation warnings (failing under `-Werror`). cream propagates `@Deprecated` onto the generated
 * function so those references live inside a deprecated declaration and stop warning.
 *
 * The propagated annotation reproduces the original message and level (a non-default level such as
 * `DeprecationLevel.ERROR` must be preserved). `exitCode == OK` confirms the generated source still
 * compiles with the propagated annotation.
 */
internal class DeprecatedSnapshotTest :
    FunSpec({
        fun runSnapshot(
            snapshotName: String,
            source: String,
        ): String {
            val result = compileWithCream(source)

            withClue("Compilation should succeed. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            val generated = result.generatedSourceText()
            assertMatchesSnapshot(snapshotName) {
                "Generated" facetOf generated
                "Input" facetOf source
            }
            return generated
        }

        test("propagates @Deprecated onto the copy function generated from a deprecated source class") {
            val generated =
                runSnapshot(
                    "DeprecatedSnapshotTest.deprecatedSourceClass",
                    """
                    package snap.deprecated.sourceclass

                    import me.tbsten.cream.CopyTo

                    @Deprecated("source is deprecated")
                    @CopyTo(DpT::class)
                    data class DpS(val old: Int)

                    data class DpT(val old: Int)
                    """.trimIndent(),
                )
            generated shouldContain "@Deprecated(\"source is deprecated\")"
        }

        test("propagates @Deprecated onto the copy function generated from a deprecated source property") {
            val generated =
                runSnapshot(
                    "DeprecatedSnapshotTest.deprecatedSourceProperty",
                    """
                    package snap.deprecated.sourceproperty

                    import me.tbsten.cream.CopyTo

                    @CopyTo(DpT::class)
                    data class DpS(@Deprecated("prop is deprecated") val old: Int)

                    data class DpT(val old: Int)
                    """.trimIndent(),
                )
            generated shouldContain "@Deprecated(\"prop is deprecated\")"
        }

        // A DeprecationLevel.ERROR source cannot yield compiling generated code: the generated
        // function references the source (as receiver / via this.x), and an ERROR-level deprecation
        // is a hard error at every use site even inside another @Deprecated declaration. So this
        // case pins only that the propagated annotation PRESERVES the level, without requiring the
        // generated code to compile.
        test("preserves DeprecationLevel.ERROR when propagating @Deprecated from the source") {
            val source =
                """
                package snap.deprecated.levelerror

                import me.tbsten.cream.CopyTo

                @Deprecated("gone", level = DeprecationLevel.ERROR)
                @CopyTo(DpT::class)
                data class DpS(val old: Int)

                data class DpT(val old: Int)
                """.trimIndent()
            val generated = compileWithCream(source).generatedSourceText()
            assertSoftly {
                generated shouldContain "@Deprecated(\"gone\", level = DeprecationLevel.ERROR)"
                assertMatchesSnapshot("DeprecatedSnapshotTest.deprecatedLevelError") {
                    "Generated" facetOf generated
                    "Input" facetOf source
                }
            }
        }

        test("does not propagate @Deprecated from a non-deprecated source") {
            val generated =
                runSnapshot(
                    "DeprecatedSnapshotTest.notDeprecated",
                    """
                    package snap.deprecated.none

                    import me.tbsten.cream.CopyTo

                    @CopyTo(DpT::class)
                    data class DpS(val old: Int)

                    data class DpT(val old: Int)
                    """.trimIndent(),
                )
            generated shouldNotContain "@Deprecated"
        }

        test("propagates @Deprecated onto a @CombineTo copy function generated from a deprecated primary source") {
            val generated =
                runSnapshot(
                    "DeprecatedSnapshotTest.deprecatedCombineToSource",
                    """
                    package snap.deprecated.combineto

                    import me.tbsten.cream.CombineTo

                    @Deprecated("primary is deprecated")
                    @CombineTo(Target::class)
                    data class Primary(val id: Int)

                    data class Other(val extra: String)

                    data class Target(val id: Int, val extra: String)
                    """.trimIndent(),
                )
            generated shouldContain "@Deprecated(\"primary is deprecated\")"
        }

        // Precedence is evaluated per source in declaration order: for each source, its class-level
        // @Deprecated is checked before that same source's property-level @Deprecated. With multiple
        // @CombineTo sources, the receiver source comes first, so an earlier source's deprecated
        // PROPERTY must win over a later source's deprecated CLASS. The earlier (`First`) receiver
        // has only a deprecated property; the later (`Second`) source is class-deprecated. The
        // function on the `First` receiver must therefore carry the property's message, not the
        // later class's message — otherwise a later class-level deprecation would shadow an earlier
        // property-level one.
        test("prefers an earlier source's deprecated property over a later source's deprecated class") {
            val generated =
                runSnapshot(
                    "DeprecatedSnapshotTest.combineToPropertyBeforeLaterClass",
                    """
                    package snap.deprecated.combinetoorder

                    import me.tbsten.cream.CombineTo

                    @CombineTo(Target::class)
                    data class First(@Deprecated("first property gone") val id: Int)

                    @Deprecated("second class gone")
                    @CombineTo(Target::class)
                    data class Second(val extra: String)

                    data class Target(val id: Int, val extra: String)
                    """.trimIndent(),
                )
            // Buggy precedence (all classes first) would attach "second class gone" to the `First`
            // receiver and never emit "first property gone"; the per-source order makes it appear.
            generated shouldContain "@Deprecated(\"first property gone\")"
        }
    })
