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

internal class BasicSnapshotTest :
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

        test("copyTo class generates expected source") {
            val source =
                """
                package snap.basic

                import me.tbsten.cream.CopyTo

                @CopyTo(Target::class)
                data class Source(
                    val shared: String,
                    val onlyOnSource: Int,
                )

                data class Target(
                    val shared: String,
                    val onlyOnTarget: Boolean,
                )
                """.trimIndent()
            val result = compileWithCream(source)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("BasicSnapshotTest.copyTo") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }

        test("copyTo from a data class to an object returns the singleton") {
            runSnapshot(
                "BasicSnapshotTest.copyToObject",
                """
                package snap.basic.objecttarget

                import me.tbsten.cream.CopyTo

                @CopyTo(Target::class)
                data class Source(
                    val shared: String,
                    val onlyOnSource: Int,
                )

                object Target
                """.trimIndent(),
            )
        }

        test("copyTo keeps a vararg ctor param and gives it a default") {
            val generated =
                runSnapshot(
                    "BasicSnapshotTest.copyToVararg",
                    """
                    package snap.basic.vararg

                    import me.tbsten.cream.CopyTo

                    @CopyTo(Target::class)
                    class Source(val id: Int, vararg val tags: String)

                    class Target(val id: Int, vararg val tags: String)
                    """.trimIndent(),
                )
            generated shouldContain "vararg tags"
        }

        test("copyTo matches an Array source property to a vararg target param") {
            runSnapshot(
                "BasicSnapshotTest.copyToArraySourceToVararg",
                """
                package snap.basic.arraysource

                import me.tbsten.cream.CopyTo

                @CopyTo(Target::class)
                class Source(val id: Int, val tags: Array<String>)

                class Target(val id: Int, vararg val tags: String)
                """.trimIndent(),
            )
        }

        test("copyTo keeps a primitive vararg ctor param and gives it a default") {
            val generated =
                runSnapshot(
                    "BasicSnapshotTest.copyToPrimitiveVararg",
                    """
                    package snap.basic.primitive

                    import me.tbsten.cream.CopyTo

                    @CopyTo(Target::class)
                    class Source(val id: Int, vararg val nums: Int)

                    class Target(val id: Int, vararg val nums: Int)
                    """.trimIndent(),
                )
            generated shouldContain "vararg nums"
        }

        test("copyTo handles a vararg ctor param in the middle") {
            runSnapshot(
                "BasicSnapshotTest.copyToVarargInMiddle",
                """
                package snap.basic.middle

                import me.tbsten.cream.CopyTo

                @CopyTo(Target::class)
                class Source(val id: Int, vararg val tags: String, val name: String = "")

                class Target(val id: Int, vararg val tags: String, val name: String = "")
                """.trimIndent(),
            )
        }

        test("copyTo emits a required vararg param when no source property matches") {
            runSnapshot(
                "BasicSnapshotTest.copyToVarargWithoutMatch",
                """
                package snap.basic.nomatch

                import me.tbsten.cream.CopyTo

                @CopyTo(Target::class)
                class Source(val id: Int)

                class Target(val id: Int, vararg val tags: String)
                """.trimIndent(),
            )
        }

        test("copyTo emits a required vararg when the source array is nullable") {
            val generated =
                runSnapshot(
                    "BasicSnapshotTest.copyToNullableArraySourceVararg",
                    """
                    package snap.basic.nullablearray

                    import me.tbsten.cream.CopyTo

                    @CopyTo(Target::class)
                    class Source(val id: Int, val tags: Array<String>?)

                    class Target(val id: Int, vararg val tags: String)
                    """.trimIndent(),
                )
            generated shouldContain "vararg tags: String,"
            generated shouldNotContain "vararg tags: String = this.tags"
        }

        test("copyTo emits a required vararg when the source primitive array is nullable") {
            val generated =
                runSnapshot(
                    "BasicSnapshotTest.copyToNullablePrimitiveArraySourceVararg",
                    """
                    package snap.basic.nullableprimitivearray

                    import me.tbsten.cream.CopyTo

                    @CopyTo(Target::class)
                    class Source(val id: Int, val nums: IntArray?)

                    class Target(val id: Int, vararg val nums: Int)
                    """.trimIndent(),
                )
            generated shouldContain "vararg nums: Int,"
            generated shouldNotContain "vararg nums: Int = this.nums"
        }
    })
