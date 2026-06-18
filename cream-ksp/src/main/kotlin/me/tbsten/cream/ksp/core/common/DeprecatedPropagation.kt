package me.tbsten.cream.ksp.core.common

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
 * The first deprecation found wins, scanning [sources] in order and, within each source, checking
 * its class-level `@Deprecated` before that same source's properties before moving to the next
 * source. So a class-level deprecation wins over its own properties, yet an earlier source's
 * deprecated property still wins over a later source's deprecated class. message and level are
 * preserved; level is only rendered when it differs from the default [DeprecationLevel.WARNING] to
 * keep output minimal.
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
 * The first `@Deprecated` among these source classes, evaluated per source in declaration order:
 * each source is fully checked (its class-level annotation, then its own properties) before moving
 * on to the next source. So a class-level deprecation wins over that same source's properties, but
 * an earlier source's property deprecation still wins over a later source's class deprecation.
 */
private fun List<KSClassDeclaration>.firstDeprecation(): Deprecated? =
    firstNotNullOfOrNull { source ->
        source.deprecatedAnnotation()
            ?: source.getAllProperties().firstNotNullOfOrNull { it.deprecatedAnnotation() }
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
