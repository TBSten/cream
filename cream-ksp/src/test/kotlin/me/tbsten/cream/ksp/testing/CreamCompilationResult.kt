@file:OptIn(ExperimentalCompilerApi::class)

package me.tbsten.cream.ksp.testing

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.sourcesGeneratedBySymbolProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.ByteArrayOutputStream
import java.io.File

internal data class CreamCompilationResult(
    private val raw: JvmCompilationResult,
    val compilation: KotlinCompilation,
    private val compilerOutputBuffer: ByteArrayOutputStream,
) {
    val exitCode: KotlinCompilation.ExitCode get() = raw.exitCode
    val messages: String get() = raw.messages

    /**
     * Everything the Kotlin compiler / KSP printed to `messageOutputStream` during this run.
     * Use with [me.tbsten.cream.ksp.testing.normalizedCompilerOutput] when snapshotting, since
     * the raw text contains absolute temp-dir paths.
     */
    val compilerOutput: String get() = compilerOutputBuffer.toString(Charsets.UTF_8)

    fun generatedSources(): List<File> = raw.sourcesGeneratedBySymbolProcessor.toList()

    fun loadGeneratedClass(fqName: String): Class<*> = raw.classLoader.loadClass(fqName)
}
