package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import me.tbsten.cream.ksp.util.ksp.getArgument

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
    return renderDeprecatedAnnotationLine(deprecated)
}

/**
 * Like [deprecatedAnnotationLine] but checks only the given [declarations] themselves (no
 * property scan), in order. `@CallFrom` uses this for the annotated function and its enclosing
 * class — symbols its generated bridge references in addition to the source class — falling back
 * to [deprecatedAnnotationLine] for the source-class + properties scan.
 */
internal fun deprecatedAnnotationLineOfDeclarations(declarations: List<KSAnnotated>): String? {
    val deprecated = declarations.firstNotNullOfOrNull { it.deprecatedAnnotation() } ?: return null
    return renderDeprecatedAnnotationLine(deprecated)
}

private fun renderDeprecatedAnnotationLine(deprecated: Deprecated): String =
    buildString {
        append("@Deprecated(")
        append(deprecated.message.toKotlinStringLiteral())
        if (deprecated.level != DeprecationLevel.WARNING) {
            append(", level = DeprecationLevel.")
            append(deprecated.level.name)
        }
        append(")")
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

/**
 * The declaration's own `@Deprecated` annotation, if any. Internal (not private) so the feature
 * layer can inspect the deprecation *level*: `@CallFrom` must reject `ERROR` / `HIDDEN` targets,
 * whose calls cannot compile from a generated bridge at all.
 *
 * Read from the raw [com.google.devtools.ksp.symbol.KSAnnotation] rather than the typed
 * `getAnnotationsByType` proxy: on AA-backed KSP2 (e.g. the Gradle `kspCommonMainKotlinMetadata`
 * task) the proxy throws `NoSuchElementException` when reading a field left at its default —
 * which `level` almost always is.
 */
internal fun KSAnnotated.deprecatedAnnotation(): Deprecated? {
    val annotation = annotationsOf(Deprecated::class).firstOrNull() ?: return null
    return Deprecated(
        message = annotation.getArgument<String>(Deprecated::message.name) ?: "",
        level = annotation.deprecationLevelArgument(),
    )
}

/**
 * Decode the `level` argument, tolerating the different shapes KSP surfaces an enum entry as
 * (mirrors [copyVisibilityArgument]). Absent or unrecognized values fall back to
 * [DeprecationLevel.WARNING] — the annotation's own default.
 */
private fun KSAnnotation.deprecationLevelArgument(): DeprecationLevel {
    val value = getArgument<Any>(Deprecated::level.name) ?: return DeprecationLevel.WARNING
    val entryName =
        when (value) {
            is KSClassDeclaration -> value.simpleName.asString()
            is KSType -> value.declaration.simpleName.asString()
            is Enum<*> -> value.name
            is String -> value
            else -> return DeprecationLevel.WARNING
        }
    return runCatching { DeprecationLevel.valueOf(entryName) }.getOrDefault(DeprecationLevel.WARNING)
}

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
