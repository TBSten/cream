package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.ksp.util.underPackageName

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
 * val target = a.funName(b = B(...)<extraArgs>)
 * ```
 *
 * [extraArgs] is appended inside the call parentheses after the source params — e.g.
 * `", property = value"` for the "Override property values" example.
 */
internal fun combineExampleBody(
    sources: List<KSClassDeclaration>,
    funName: String,
    extraArgs: String = "",
): String {
    val primarySource = sources.first()
    val otherSourceParams =
        sources.drop(1).joinToString(", ") {
            "${it.lowerCamelName()} = ${it.simpleName.asString()}(...)"
        }
    return buildString {
        sources.forEach { source ->
            appendLine("val ${source.lowerCamelName()} = ${source.simpleName.asString()}(...)")
        }
        append("val target = ${primarySource.lowerCamelName()}.$funName($otherSourceParams$extraArgs)")
    }
}
