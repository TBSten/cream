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
 * The golden file is a Markdown document containing a single fenced code block whose
 * language is [lang]. Wrapping the captured text in a fenced block gives diffs
 * Kotlin (or other) syntax highlighting on GitHub and keeps the file
 * self-describing when opened standalone.
 *
 * Pass `lang = "kt"` (the default) for generated Kotlin source. Pass `lang = "text"`
 * for compiler output, stack traces, or anything else that is not valid Kotlin.
 *
 * Comparison is whole-file (fences included), so any drift in the wrapping itself
 * is also caught.
 */
internal fun assertMatchesSnapshot(
    name: String,
    actual: String,
    lang: String = "kt",
) {
    val file = File(snapshotRoot, "$name.md")
    val normalizedActual = wrapAsMarkdown(actual, lang)

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
 * Wrap [actual] in a fenced Markdown code block. To stay correct when the body itself
 * contains backtick runs (e.g. cream emits KDoc with embedded ` ```kt ` examples),
 * the outer fence is one backtick longer than the longest backtick run inside the body,
 * with a minimum of 3.
 */
private fun wrapAsMarkdown(
    actual: String,
    lang: String,
): String {
    val trimmed = actual.trimEnd()
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
        append('\n')
    }
}
