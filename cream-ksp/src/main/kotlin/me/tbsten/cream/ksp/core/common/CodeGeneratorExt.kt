package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSName

/**
 * Write a generated Kotlin file, but only when [block] actually produces declarations.
 *
 * [block] is run first against an in-memory buffer and must write **only declarations** — the
 * `package` line and the `import me.tbsten.cream.*` boilerplate are owned here so every caller
 * shares one source of truth. If [block] writes nothing (every target was skipped, e.g.
 * `notCopyToObject` object-skips), no file is opened: cream emits no empty `package` + `import`
 * file (issue #113). Otherwise the real file is created and the boilerplate + buffered
 * declarations are flushed to it.
 */
internal fun CodeGenerator.createNewKotlinFile(
    dependencies: Dependencies,
    packageName: KSName,
    fileName: String,
    block: (Appendable) -> Unit,
) {
    val buffer = StringBuilder()
    block(buffer)
    val body = buffer.toString()

    if (body.isEmpty()) return

    createNewFile(
        dependencies = dependencies,
        packageName = packageName.asString(),
        fileName = fileName,
    ).bufferedWriter()
        .use {
            it.appendLine("package ${packageName.asString()}")
            it.appendLine()
            it.appendLine("import me.tbsten.cream.*")
            it.appendLine()
            it.append(body)
        }
}
