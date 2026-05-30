package me.tbsten.cream.ksp.testing

import java.io.File
import kotlin.test.fail

/**
 * Run with `-Dcream.snapshot.update=true` to (re-)generate snapshot files.
 */
private val updateSnapshots: Boolean
    get() = System.getProperty("cream.snapshot.update")?.equals("true", ignoreCase = true) == true

private val snapshotRoot: File by lazy {
    val dir = File("src/test/resources/snapshots")
    if (!dir.exists()) dir.mkdirs()
    dir
}

/**
 * Map a dotted snapshot [name] to its golden file path relative to [snapshotRoot].
 *
 * The first `.` is the test-class/test-case boundary and becomes a directory
 * separator, giving the `<TestName>/<testCase>.md` layout:
 *
 * - `"BasicSnapshotTest.copyTo"` → `BasicSnapshotTest/copyTo.md`
 * - `"CopyToDiagnosticTest.enumTarget.output"` → `CopyToDiagnosticTest/enumTarget.output.md`
 *
 * Only the first `.` is rewritten, so dotted suffixes/variants in the test case
 * (`.output`, `.default`, …) stay part of the file name. An `edgeCase` segment is
 * expressed with a `/` in [name] and survives verbatim, supporting both
 * `"<TestName>.edgeCase/<case>"` → `<TestName>/edgeCase/<case>.md` and
 * `"edgeCase/<TestName>.<case>"` → `edgeCase/<TestName>/<case>.md`.
 */
private fun snapshotRelativePath(name: String): String = "${name.replaceFirst(".", "/")}.md"

/**
 * Compare a Markdown golden snapshot against the facets declared in [block].
 *
 * The golden file lives under `src/test/resources/snapshots/` in a per-test-class
 * directory derived from [name]: the first `.` separates the test class from the
 * test case and becomes a directory boundary. So `"MyTest.scenario"` resolves to
 * `snapshots/MyTest/scenario.md` (see [snapshotRelativePath]). Edge-case scenarios
 * embed an `edgeCase` path segment in [name], e.g. `"MyTest.edgeCase/scenario"` →
 * `snapshots/MyTest/edgeCase/scenario.md`.
 *
 * Every captured value is a facet — there is no special "main" content. Each facet
 * becomes a `## <facet name>` section in declaration order, followed by a single
 * fenced code block. Fence lengths are computed per section so embedded backtick
 * runs (e.g. cream emits KDoc with ` ```kt ... ``` ` examples) never collide with
 * the wrapping. Comparison is whole-file (headings + fences included), so any drift
 * in the wrapping itself is also caught.
 *
 * ```kt
 * assertMatchesSnapshot("MyTest.scenario") {
 *     "Output" facetOf result.generatedSourceText()
 *     "Input" facetOf source
 *     facet("Compiler messages", result.messages, lang = "text")
 * }
 * ```
 *
 * At least one facet is required. The infix [SnapshotFacetBuilder.facetOf] form
 * defaults `lang` to `"kt"`; use [SnapshotFacetBuilder.facet] to pick a different
 * fence language.
 */
internal fun assertMatchesSnapshot(
    name: String,
    block: SnapshotFacetBuilder.() -> Unit,
) {
    val builder = SnapshotFacetBuilderImpl()
    builder.block()
    val facets = builder.build()
    require(facets.isNotEmpty()) {
        "assertMatchesSnapshot(\"$name\") requires at least one facet inside its block."
    }

    val file = File(snapshotRoot, snapshotRelativePath(name))
    val normalizedActual = renderFacets(facets)

    if (!file.exists()) {
        if (updateSnapshots) {
            file.parentFile.mkdirs()
            file.writeText(normalizedActual, Charsets.UTF_8)
            return
        }
        fail(
            """
            Snapshot file not found: ${file.path}
            Run with -Dcream.snapshot.update=true to create it.
            Actual content:
            $normalizedActual
            """.trimIndent(),
        )
    }

    val expected = file.readText(Charsets.UTF_8)
    if (expected != normalizedActual) {
        if (updateSnapshots) {
            file.writeText(normalizedActual, Charsets.UTF_8)
            return
        }
        fail(
            """
            Snapshot mismatch for ${file.path}
            Run with -Dcream.snapshot.update=true to update.
            --- Expected ---
            $expected
            --- Actual ---
            $normalizedActual
            """.trimIndent(),
        )
    }
}

/**
 * Builder for the facets block of [assertMatchesSnapshot]. Each facet becomes a
 * `## <name>` section in the golden file, in declaration order.
 */
internal interface SnapshotFacetBuilder {
    /**
     * Add a facet whose fence language is `kt`. Equivalent to `facet(this, content)`.
     * Use the long form [facet] when a non-Kotlin language is needed.
     */
    infix fun String.facetOf(content: String)

    /** Add a facet with an explicit fence language (e.g. `"text"`, `"properties"`). */
    fun facet(
        name: String,
        content: String,
        lang: String = "kt",
    )
}

private class SnapshotFacetBuilderImpl : SnapshotFacetBuilder {
    private val facets = mutableListOf<Facet>()

    override infix fun String.facetOf(content: String) {
        facets += Facet(this, content, "kt")
    }

    override fun facet(
        name: String,
        content: String,
        lang: String,
    ) {
        facets += Facet(name, content, lang)
    }

    fun build(): List<Facet> = facets.toList()
}

private data class Facet(
    val name: String,
    val content: String,
    val lang: String,
)

private fun renderFacets(facets: List<Facet>): String =
    buildString {
        for ((index, facet) in facets.withIndex()) {
            if (index > 0) append('\n')
            append("## ").append(facet.name).append("\n\n")
            append(renderFencedBlock(facet.content, facet.lang)).append('\n')
        }
    }

/**
 * Wrap [content] in a fenced Markdown code block. To stay correct when the body
 * itself contains backtick runs (e.g. cream emits KDoc with embedded
 * ` ```kt ` examples), the outer fence is one backtick longer than the longest
 * backtick run inside the body, with a minimum of 3.
 */
private fun renderFencedBlock(
    content: String,
    lang: String,
): String {
    val trimmed = content.trimEnd()
    val longestInternalRun =
        Regex("`+").findAll(trimmed).maxOfOrNull { it.value.length } ?: 0
    val fence = "`".repeat(maxOf(3, longestInternalRun + 1))
    return buildString {
        append(fence)
        append(lang)
        append('\n')
        append(trimmed)
        append('\n')
        append(fence)
    }
}
