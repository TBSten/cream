@file:OptIn(ExperimentalCompilerApi::class)

package me.tbsten.cream.ksp.testing

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspProcessorOptions
import com.tschuchort.compiletesting.symbolProcessorProviders
import com.tschuchort.compiletesting.useKsp2
import me.tbsten.cream.ksp.CreamSymbolProcessorProvider
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.ByteArrayOutputStream
import java.io.OutputStream

/**
 * DSL receiver for collecting source files. Use [source] from a [String] receiver
 * (file name) to add a Kotlin source. See the multi-source overload of
 * [compileWithCream].
 */
internal class CreamSourcesBuilder {
    private val sources = mutableListOf<SourceFile>()

    infix fun String.source(
        @Language("kotlin") code: String,
    ) {
        sources += SourceFile.kotlin(this, code)
    }

    internal fun build(): List<SourceFile> = sources.toList()
}

/**
 * Multi-source overload. Use when a test needs more than one input file:
 *
 * ```kt
 * compileWithCream(options = mapOf(...)) {
 *     "Source.kt" source """
 *         @CopyTo(Target::class)
 *         data class Source(...)
 *     """.trimIndent()
 *     "Target.kt" source """
 *         data class Target(...)
 *     """.trimIndent()
 * }
 * ```
 */
internal fun compileWithCream(
    options: Map<String, String> = emptyMap(),
    block: CreamSourcesBuilder.() -> Unit,
): CreamCompilationResult {
    val sources = CreamSourcesBuilder().apply(block).build()
    return runCompilation(sources, options)
}

/**
 * Single-source convenience overload. Equivalent to:
 *
 * ```kt
 * compileWithCream(
 *     """
 *         @CopyTo(Target::class)
 *         data class Source(...)
 *     """,
 *     options,
 * )
 * ```
 */
internal fun compileWithCream(
    @Language("kotlin") source: String,
    options: Map<String, String> = emptyMap(),
    sourceFileName: String = "Test.kt",
): CreamCompilationResult =
    compileWithCream(options = options) {
        sourceFileName source source
    }

private fun runCompilation(
    sources: List<SourceFile>,
    options: Map<String, String>,
): CreamCompilationResult {
    // Multiplex compiler output: keep printing to System.out for local debugging,
    // and also capture into a buffer so tests can snapshot it via
    // CreamCompilationResult.compilerOutput.
    val captured = ByteArrayOutputStream()
    val tee = TeeOutputStream(System.out, captured)
    val compilation =
        KotlinCompilation().apply {
            inheritClassPath = true
            useKsp2()
            symbolProcessorProviders += CreamSymbolProcessorProvider()
            if (options.isNotEmpty()) {
                kspProcessorOptions = options.toMutableMap()
            }
            this.sources = sources
            messageOutputStream = tee
        }
    return CreamCompilationResult(
        raw = compilation.compile(),
        compilation = compilation,
        compilerOutputBuffer = captured,
    )
}

private class TeeOutputStream(
    private val a: OutputStream,
    private val b: OutputStream,
) : OutputStream() {
    override fun write(byte: Int) {
        a.write(byte)
        b.write(byte)
    }

    override fun write(
        buffer: ByteArray,
        offset: Int,
        length: Int,
    ) {
        a.write(buffer, offset, length)
        b.write(buffer, offset, length)
    }

    override fun flush() {
        a.flush()
        b.flush()
    }
}
