package me.tbsten.cream.ksp.core.combineFun

import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.ksp.core.common.KDocWriter
import me.tbsten.cream.ksp.core.common.underPackageName

private fun KSClassDeclaration.lowerCamelName(): String = underPackageName.replaceFirstChar { it.lowercase() }

/**
 * `[A] + [B] -> [Target] copy function.` — the auto description line shared by every
 * combine-function KDoc (CombineTo / CombineFrom, class- and object-target).
 */
internal fun KDocWriter.appendCombineAutoDescription(
    sources: List<KSClassDeclaration>,
    target: KSClassDeclaration,
) {
    val sourcesStr = sources.joinToString(" + ") { "[${it.underPackageName}]" }
    appendLine("$sourcesStr -> [${target.underPackageName}] copy function.")
}

/**
 * The body of a combine-function KDoc example:
 *
 * ```
 * val a = A(...)
 * val b = B(...)
 * val target = a.funName(b = B(...), <extraArgs>)
 * ```
 *
 * [extraArgs] are appended inside the call parentheses after the source params — e.g.
 * `listOf("property = value")` for the "Override property values" example. The source
 * params and extra args are comma-joined together, so no dangling comma is produced when
 * either side is empty (e.g. a single source with no extra args -> `funName()`).
 */
internal fun combineExampleBody(
    sources: List<KSClassDeclaration>,
    funName: String,
    extraArgs: List<String> = emptyList(),
): String {
    val primarySource = sources.firstOrNull() ?: return ""
    val otherSourceParams =
        sources.drop(1).map {
            "${it.lowerCamelName()} = ${it.simpleName.asString()}(...)"
        }
    val callArgs = (otherSourceParams + extraArgs).joinToString(", ")
    return buildString {
        sources.forEach { source ->
            appendLine("val ${source.lowerCamelName()} = ${source.simpleName.asString()}(...)")
        }
        append("val target = ${primarySource.lowerCamelName()}.$funName($callArgs)")
    }
}
