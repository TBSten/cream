package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText

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
    })
