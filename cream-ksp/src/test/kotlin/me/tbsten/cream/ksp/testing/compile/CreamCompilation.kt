@file:OptIn(ExperimentalCompilerApi::class)

package me.tbsten.cream.ksp.testing.compile

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspProcessorOptions
import com.tschuchort.compiletesting.symbolProcessorProviders
import com.tschuchort.compiletesting.useKsp2
import me.tbsten.cream.CopyTo
import me.tbsten.cream.ksp.CreamSymbolProcessorProvider
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.ByteArrayOutputStream
import java.io.File
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
    val captured = ByteArrayOutputStream()
    val tee = TeeOutputStream(System.out, captured)
    val compilation =
        KotlinCompilation().apply {
            // For performance, limit the classpath to creamCompilationClasspath (issue #155).
            inheritClassPath = false
            classpaths = creamCompilationClasspath
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

private val creamCompilationClasspath: List<File> =
    listOf(
        CopyTo::class.java, // cream-runtime
        Unit::class.java, // kotlin-stdlib
    ).map { it.classpathRoot() }.distinct()

private fun Class<*>.classpathRoot(): File {
    val location =
        checkNotNull(protectionDomain?.codeSource?.location) {
            "Cannot locate the classpath root for $name (codeSource is null)."
        }
    return File(location.toURI())
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
