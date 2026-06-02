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
 * Snapshot tests covering backquote-escaping of generated identifiers (issue #94).
 *
 * When a property / constructor-parameter name is a Kotlin keyword (`in`, `is`, …) or
 * contains characters that are only legal inside backquotes (e.g. a space), the generated
 * copy function must wrap that identifier in backquotes at every interpolation site —
 * parameter declaration, `this.<name>` default, and the `name = name` named argument —
 * otherwise the generated code does not compile.
 *
 * Note: the generated function NAME is intentionally not escaped by cream (left to the
 * compiler, see CopyFunctionNameExt). These tests pin the escaping of property/parameter
 * identifiers only, and assert the generated code actually compiles (exitCode == OK proves
 * the emitted source is valid Kotlin).
 */
internal class IdentifierEscapingSnapshotTest :
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

        test("keyword 名のプロパティを持つ copyTo が backquote で escape して compile できるコードを生成する") {
            val generated =
                runSnapshot(
                    "IdentifierEscapingSnapshotTest.copyToKeyword",
                    """
                    package snap.escape.copyto

                    import me.tbsten.cream.CopyTo

                    @CopyTo(KwTarget::class)
                    data class KwSource(val `in`: Int, val `is`: Boolean)

                    data class KwTarget(val `in`: Int, val `is`: Boolean)
                    """.trimIndent(),
                )
            generated shouldContain "`in`: Int = this.`in`"
            generated shouldContain "`is`: Boolean = this.`is`"
            generated shouldContain "`in` = `in`"
            generated shouldContain "`is` = `is`"
        }

        test("空白入り名のプロパティを持つ copyTo が backquote で escape して compile できるコードを生成する") {
            val generated =
                runSnapshot(
                    "IdentifierEscapingSnapshotTest.copyToSpaceName",
                    """
                    package snap.escape.space

                    import me.tbsten.cream.CopyTo

                    @CopyTo(SpTarget::class)
                    data class SpSource(val `my prop`: Int)

                    data class SpTarget(val `my prop`: Int)
                    """.trimIndent(),
                )
            generated shouldContain "`my prop`: Int = this.`my prop`"
            generated shouldContain "`my prop` = `my prop`"
        }

        test("keyword 名のプロパティを持つ copyFrom が backquote で escape して compile できるコードを生成する") {
            val generated =
                runSnapshot(
                    "IdentifierEscapingSnapshotTest.copyFromKeyword",
                    """
                    package snap.escape.copyfrom

                    import me.tbsten.cream.CopyFrom

                    data class KwSource(val `object`: String)

                    @CopyFrom(KwSource::class)
                    data class KwTarget(val `object`: String)
                    """.trimIndent(),
                )
            generated shouldContain "`object`: String = this.`object`"
            generated shouldContain "`object` = `object`"
        }

        test("keyword 名のプロパティを持つ combineTo が backquote で escape して compile できるコードを生成する") {
            val generated =
                runSnapshot(
                    "IdentifierEscapingSnapshotTest.combineToKeyword",
                    """
                    package snap.escape.combineto

                    import me.tbsten.cream.CombineTo

                    @CombineTo(KwTarget::class)
                    data class Primary(val `in`: Int)

                    data class Other(val `return`: String)

                    data class KwTarget(val `in`: Int, val `return`: String)
                    """.trimIndent(),
                )
            // primary source contributes `this.` reference, other source contributes a param + ref
            generated shouldContain "`in`: Int = this.`in`"
            generated shouldContain "`in` = `in`"
            generated shouldContain "`return` = `return`"
        }

        test("非 ASCII の正当な identifier は backquote で escape されない") {
            val generated =
                runSnapshot(
                    "IdentifierEscapingSnapshotTest.nonAsciiIdentifier",
                    """
                    package snap.escape.nonascii

                    import me.tbsten.cream.CopyTo

                    @CopyTo(注文::class)
                    data class 注文Entity(val 税込金額: Int)

                    data class 注文(val 税込金額: Int)
                    """.trimIndent(),
                )
            generated shouldContain "税込金額: Int = this.税込金額"
            generated shouldNotContain "`税込金額`"
        }
    })
