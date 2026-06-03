package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput

private val combineToObjectSource: String =
    """
    package snap.objtarget

    import me.tbsten.cream.CombineTo

    @CombineTo(Singleton::class)
    data class Source(val prop: String)

    data object Singleton
    """.trimIndent()

internal class ObjectTargetSnapshotTest :
    FunSpec({
        test("combineTo with object target generates expected source") {
            val result = compileWithCream(combineToObjectSource)

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("ObjectTargetSnapshotTest.combineToObject.default") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf combineToObjectSource
            }
        }

        test("combineTo with object target and notCopyToObject=true generates expected source") {
            val result =
                compileWithCream(
                    combineToObjectSource,
                    options = mapOf("cream.notCopyToObject" to "true"),
                )

            withClue(result.messages) {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("ObjectTargetSnapshotTest.combineToObject.notCopyToObject") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf combineToObjectSource
            }
        }

        test("combineTo keeps a vararg ctor param on the target and gives it a default") {
            val source =
                """
                package snap.objtarget.vararg

                import me.tbsten.cream.CombineTo

                @CombineTo(Target::class)
                class Primary(val id: Int, vararg val tags: String)

                class Other(val extra: String)

                class Target(val id: Int, val extra: String, vararg val tags: String)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should succeed. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("ObjectTargetSnapshotTest.combineToVararg") {
                "Generated" facetOf result.generatedSourceText()
                "Input" facetOf source
            }
        }
    })
