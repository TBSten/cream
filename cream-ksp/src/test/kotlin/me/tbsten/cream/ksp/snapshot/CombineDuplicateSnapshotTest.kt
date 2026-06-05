package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput

/**
 * `@CombineFrom` IS `@Repeatable`. Stacking it twice with the same source set is an idempotent
 * re-declaration, not an error: cream dedupes the collected sources so exactly one copy function is
 * generated (issue #101). A regular non-duplicate stack keeps merging distinct sources as before.
 */
internal class CombineDuplicateSnapshotTest :
    FunSpec({
        test("repeated @CombineFrom with the same source is deduped to one generated function") {
            val source =
                """
                package snap.combinefrom.dup

                import me.tbsten.cream.CombineFrom

                data class A(val a: String)

                @CombineFrom(A::class)
                @CombineFrom(A::class)
                data class Target(val a: String)
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Compilation should succeed. Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                }
                // The idempotent re-declaration collapses to a single generated file.
                withClue("Generated files: ${result.generatedSources().map { it.name }}") {
                    result.generatedSources().size shouldBe 1
                }
            }
            assertMatchesSnapshot("CombineDuplicateSnapshotTest.combineFromDuplicate") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        test("repeated @CombineFrom with distinct sources still merges into one function") {
            val source =
                """
                package snap.combinefrom.distinct

                import me.tbsten.cream.CombineFrom

                data class Alpha(val id: String)
                data class Beta(val count: Int)

                @CombineFrom(Alpha::class)
                @CombineFrom(Beta::class)
                data class Target(val id: String, val count: Int)
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Compilation should succeed. Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                }
                withClue("Generated files: ${result.generatedSources().map { it.name }}") {
                    result.generatedSources().size shouldBe 1
                }
            }
            assertMatchesSnapshot("CombineDuplicateSnapshotTest.combineFromDistinct") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        test("@CombineTo with distinct targets still generates one function per target") {
            val source =
                """
                package snap.combineto.distinct

                import me.tbsten.cream.CombineTo
                import me.tbsten.cream.CopyTargetSimpleName

                data class Foo(val id: String)

                data class Bar(val id: String)

                @CombineTo(Foo::class, Bar::class, funName = "to" + CopyTargetSimpleName)
                data class Source(val id: String)
                """.trimIndent()
            val result = compileWithCream(source)

            assertSoftly {
                withClue("Compilation should succeed. Output:\n${result.normalizedCompilerOutput()}") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                }
                // One file per distinct (source, target) pair.
                withClue("Generated files: ${result.generatedSources().map { it.name }}") {
                    result.generatedSources().size shouldBe 2
                }
            }
            assertMatchesSnapshot("CombineDuplicateSnapshotTest.combineToDistinct") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }
    })
