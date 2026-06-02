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
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput

/**
 * Snapshot tests covering auto-copy (`= this.x`) eligibility by property kind (issue #105).
 *
 * Only STORED source properties (those with a backing field, i.e. constructor parameters and
 * plain initialised properties) must receive a `= this.x` default in the generated copy
 * function. Source members without a backing field (computed `get()`-only, delegated `by`)
 * and `lateinit var` members (which have a backing field but may be uninitialised) MUST NOT be
 * auto-copied: emitting `= this.x` for them risks `UninitializedPropertyAccessException` or
 * forces eager evaluation of a `lazy` delegate. Such a target parameter stays REQUIRED instead.
 */
internal class StoredPropertySnapshotTest :
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

        test("computed な source プロパティは auto-copy されずコンストラクタプロパティは auto-copy される") {
            val generated =
                runSnapshot(
                    "StoredPropertySnapshotTest.computedProperty",
                    """
                    package snap.stored.computed

                    import me.tbsten.cream.CopyTo

                    @CopyTo(Target::class)
                    class Source(val id: Int) {
                        val name: String
                            get() = "name-${'$'}id"
                    }

                    class Target(val id: Int, val name: String)
                    """.trimIndent(),
                )
            // Stored constructor property keeps its auto-copy default.
            generated shouldContain "id: Int = this.id"
            // Computed property has no backing field -> must NOT be auto-copied.
            generated shouldNotContain "= this.name"
        }

        test("lateinit var な source プロパティは auto-copy されずコンストラクタプロパティは auto-copy される") {
            val generated =
                runSnapshot(
                    "StoredPropertySnapshotTest.lateinitProperty",
                    """
                    package snap.stored.lateinit

                    import me.tbsten.cream.CopyTo

                    @CopyTo(Target::class)
                    class Source(val id: Int) {
                        lateinit var name: String
                    }

                    class Target(val id: Int, val name: String)
                    """.trimIndent(),
                )
            generated shouldContain "id: Int = this.id"
            // lateinit may be uninitialised -> must NOT be auto-copied.
            generated shouldNotContain "= this.name"
        }

        test("delegated な source プロパティは auto-copy されずコンストラクタプロパティは auto-copy される") {
            val generated =
                runSnapshot(
                    "StoredPropertySnapshotTest.delegatedProperty",
                    """
                    package snap.stored.delegated

                    import me.tbsten.cream.CopyTo

                    @CopyTo(Target::class)
                    class Source(val id: Int) {
                        val name: String by lazy { "name-${'$'}id" }
                    }

                    class Target(val id: Int, val name: String)
                    """.trimIndent(),
                )
            generated shouldContain "id: Int = this.id"
            // delegated property has no backing field -> must NOT be auto-copied (no eager lazy eval).
            generated shouldNotContain "= this.name"
        }

        test("初期化された非コンストラクタ stored プロパティは auto-copy される") {
            val generated =
                runSnapshot(
                    "StoredPropertySnapshotTest.initializedStoredProperty",
                    """
                    package snap.stored.initialized

                    import me.tbsten.cream.CopyTo

                    @CopyTo(Target::class)
                    class Source(val id: Int) {
                        val name: String = "fixed"
                    }

                    class Target(val id: Int, val name: String)
                    """.trimIndent(),
                )
            generated shouldContain "id: Int = this.id"
            // A plain initialised property has a backing field -> stays auto-copied.
            generated shouldContain "name: String = this.name"
        }
    })
