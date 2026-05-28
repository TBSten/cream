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
 * Compare [actual] against a checked-in golden snapshot at
 * `src/test/resources/snapshots/<name>.md`.
 *
 * The golden is a Markdown document. When [block] is empty (the default) the file
 * is a single fenced code block whose language is [lang] — the minimal form,
 * back-compatible with snapshots authored before the facet DSL existed.
 *
 * When [block] adds facets, the file becomes a multi-section document: [actual]
 * appears under an `## [mainTitle]` heading, followed by one `## <name>` section per
 * facet in declaration order. Fence lengths are computed per section so embedded
 * backtick runs (e.g. cream emits KDoc with ` ```kt ... ``` ` examples) never
 * collide with the wrapping. Comparison stays whole-file, so any drift in headings
 * or fences is also caught.
 *
 * Pass `lang = "kt"` (the default) for Kotlin source. Pass `lang = "text"` for
 * compiler output, stack traces, or anything else that is not valid Kotlin. Each
 * facet may carry its own [SnapshotFacetBuilder.facet] `lang`.
 *
 * ```kt
 * assertMatchesSnapshot("MyTest.scenario", result.generatedSourceText()) {
 *     "Input" facetOf result.inputSourceText()
 *     facet("Compiler messages", result.messages, lang = "text")
 * }
 * ```
 */
internal fun assertMatchesSnapshot(
    name: String,
    actual: String,
    lang: String = "kt",
    mainTitle: String = "Generated",
    block: SnapshotFacetBuilder.() -> Unit = {},
) {
    val builder = SnapshotFacetBuilderImpl()
    builder.block()
    val facets = builder.build()

    val file = File(snapshotRoot, "$name.md")
    val normalizedActual =
        if (facets.isEmpty()) {
            renderSingleSection(actual, lang)
        } else {
            renderMultiSection(actual, lang, mainTitle, facets)
        }

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
 * Builder for the optional facets block of [assertMatchesSnapshot]. Each facet
 * becomes a `## <name>` section in the golden file, in declaration order.
 */
internal interface SnapshotFacetBuilder {
    /**
     * Add a facet whose language is `kt`. Equivalent to `facet(this, content)`.
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

private fun renderSingleSection(
    content: String,
    lang: String,
): String = renderFencedBlock(content, lang) + "\n"

private fun renderMultiSection(
    main: String,
    mainLang: String,
    mainTitle: String,
    facets: List<Facet>,
): String =
    buildString {
        append("## ").append(mainTitle).append("\n\n")
        append(renderFencedBlock(main, mainLang)).append('\n')
        for (facet in facets) {
            append("\n## ").append(facet.name).append("\n\n")
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
