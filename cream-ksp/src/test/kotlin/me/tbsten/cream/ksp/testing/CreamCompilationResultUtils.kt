@file:OptIn(ExperimentalCompilerApi::class)

package me.tbsten.cream.ksp.testing

import com.tschuchort.compiletesting.kspSourcesDir
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.File

internal fun CreamCompilationResult.generatedSourceText(): String =
    generatedSources()
        .sortedBy { it.name }
        .joinToString(separator = "\n\n// ----- next file -----\n\n") { file ->
            "// file: ${file.name}\n" + file.readText().trimEnd()
        }

internal fun CreamCompilationResult.kspSourcesDir(): File = compilation.kspSourcesDir

/**
 * The captured compiler / KSP output with machine-specific bits replaced by stable
 * placeholders, so the result can be checked into snapshot golden files. Currently this
 * redacts:
 *
 * - The JVM temp directory (`java.io.tmpdir`) → `<TMPDIR>`
 * - The per-run kctfork working directory name (`Kotlin-CompilationNNN`) → `Kotlin-Compilation<N>`
 * - Consecutive stack-trace frames (`\tat ...`) and the `\t... NN more` continuation marker
 *   that follows a `Caused by:` block → a single `\t<stack trace omitted>` line. Both the
 *   frame contents and the `NN` depth count vary with the JVM / Gradle / JUnit / KSP version
 *   and with cream's own line moves; snapshot tests are concerned with the error message
 *   body, not the exact frames. If a snapshot ever needs to assert on a specific frame,
 *   prefer a targeted contains-style check alongside the snapshot instead of disabling
 *   this collapse.
 */
internal fun CreamCompilationResult.normalizedCompilerOutput(): String {
    val tmpDir = System.getProperty("java.io.tmpdir").trimEnd('/', '\\')
    return compilerOutput
        .replace(tmpDir, "<TMPDIR>")
        .replace(Regex("Kotlin-Compilation\\d+"), "Kotlin-Compilation<N>")
        .replace(Regex("(?:\\n\\tat [^\\n]+|\\n\\t\\.\\.\\. \\d+ more)+"), "\n\t<stack trace omitted>")
        .trimEnd() + if (compilerOutput.isEmpty()) "" else "\n"
}
