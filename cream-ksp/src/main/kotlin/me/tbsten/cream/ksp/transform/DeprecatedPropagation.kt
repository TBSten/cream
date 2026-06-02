package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

/**
 * Build the `@Deprecated(...)` line to propagate onto a generated copy function, or `null` when no
 * contributing source is deprecated.
 *
 * A generated copy function references its source class (as the receiver) and the source properties
 * it reads via `this.<name>` / `source.<name>`. When any of those symbols is `@Deprecated`, the
 * generated code would otherwise emit deprecation warnings (and fail under `-Werror`); a
 * `DeprecationLevel.ERROR` source even makes the generated code fail to compile outright. Copying
 * the annotation onto the generated function moves those references inside a deprecated declaration,
 * which suppresses the warnings (issue #103).
 *
 * The first deprecation found wins, in this precedence: each source class (in [sources] order),
 * then the properties of each source class. message and level are preserved; level is only rendered
 * when it differs from the default [DeprecationLevel.WARNING] to keep output minimal.
 */
internal fun deprecatedAnnotationLine(sources: List<KSClassDeclaration>): String? {
    val deprecated = sources.firstDeprecation() ?: return null
    return buildString {
        append("@Deprecated(")
        append(deprecated.message.toKotlinStringLiteral())
        if (deprecated.level != DeprecationLevel.WARNING) {
            append(", level = DeprecationLevel.")
            append(deprecated.level.name)
        }
        append(")")
    }
}

/**
 * The first `@Deprecated` among these source classes, preferring a deprecation on the class itself
 * over one on a property, so a class-level message/level wins when both are present.
 */
private fun List<KSClassDeclaration>.firstDeprecation(): Deprecated? {
    firstNotNullOfOrNull { it.deprecatedAnnotation() }?.let { return it }
    return firstNotNullOfOrNull { source ->
        source.getAllProperties().firstNotNullOfOrNull { it.deprecatedAnnotation() }
    }
}

private fun KSAnnotated.deprecatedAnnotation(): Deprecated? = getAnnotationsByType(Deprecated::class).firstOrNull()

/**
 * Render this string as a Kotlin double-quoted string literal, escaping the characters that would
 * otherwise terminate the literal or be interpreted (`\`, `"`, `$`) and common control characters.
 */
private fun String.toKotlinStringLiteral(): String =
    buildString {
        append('"')
        this@toKotlinStringLiteral.forEach { char ->
            when (char) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '$' -> append("\\$")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(char)
            }
        }
        append('"')
    }
