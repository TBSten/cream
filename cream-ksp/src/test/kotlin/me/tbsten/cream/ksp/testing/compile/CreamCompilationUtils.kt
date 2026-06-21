package me.tbsten.cream.ksp.testing.compile

import com.squareup.kotlinpoet.FileSpec
import io.kotest.core.test.TestScope
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.testing.kotlincodelikestring.cream.toKspConfigString
import me.tbsten.cream.ksp.testing.snapshot.SnapshotFacetBuilder
import me.tbsten.cream.ksp.testing.snapshot.assertMatchesSnapshot

/**
 * cream の KSP プロセッサで [inputs] を実コンパイルし、その結果を 1 つの golden に snapshot する。facet:
 * - `Input:<file>`             : 入力ソース（[FileSpec] ごと）
 * - `KSP options`              : [options] を `ksp { arg(...) }` 形式で
 * - `Output:Console`           : 正規化したコンパイラ / KSP 出力（[normalizedCompilerOutput]）
 * - `Output:Generated sources` : 生成された全ソースを連結（[generatedSourceText]）
 *
 * [additionalFacets] で facet を追加でき、[assertions] でコンパイル結果に追加検証ができる。
 * [CreamCompilationResult] を返すので呼び出し側でさらに調べられる。
 */
internal inline fun TestScope.runCompileSnapshotTest(
    inputs: List<FileSpec>,
    options: CreamOptions,
    crossinline assertions: (CreamCompilationResult) -> Unit = { },
    crossinline additionalFacets: SnapshotFacetBuilder.(CreamCompilationResult) -> Unit = { },
): CreamCompilationResult {
    val compileResult =
        compileWithCream(options = options.toKspArgs()) {
            inputs.forEach { input ->
                "${input.name}.kt" source input.toString()
            }
        }

    assertMatchesSnapshot {
        inputs.forEach { input ->
            "Input:${input.name}" facetOf input.toString()
        }
        "KSP options" facetOf options.toKspConfigString()
        "Output:ExitCode" facetOf compileResult.exitCode.name
        "Output:Console" facetOf compileResult.normalizedCompilerOutput()
        "Output:Generated sources" facetOf compileResult.generatedSourceText()
        additionalFacets(compileResult)
    }

    assertions(compileResult)

    return compileResult
}

/** 単一入力版の [runCompileSnapshotTest]。 */
internal inline fun TestScope.runCompileSnapshotTest(
    input: FileSpec,
    options: CreamOptions,
    crossinline assertions: (CreamCompilationResult) -> Unit = { },
    crossinline additionalFacets: SnapshotFacetBuilder.(CreamCompilationResult) -> Unit = { },
): CreamCompilationResult =
    runCompileSnapshotTest(
        inputs = listOf(input),
        options = options,
        assertions = assertions,
        additionalFacets = additionalFacets,
    )

/**
 * [CreamOptions] を KSP の arg（`cream.<プロパティ名>` キー）Map に変換する。[Map.toCreamOptions] の逆。
 * 値は enum なら [Enum.name]（= `under-package` 等の serialized 名）、それ以外は `toString()`。
 * [CreamOptions.properties] を回すので、option を追加しても自動で追従する。
 */
internal fun CreamOptions.toKspArgs(): Map<String, String> =
    CreamOptions.properties.associate { property ->
        val value = property.get(this)
        "cream.${property.name}" to if (value is Enum<*>) value.name else value.toString()
    }
