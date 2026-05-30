package me.tbsten.cream.ksp.util

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Visibility
import me.tbsten.cream.CopyVisibility

internal val KSClassDeclaration.visibilityStr: String
    get() =
        when (getVisibility()) {
            Visibility.PUBLIC -> "public"
            Visibility.PRIVATE -> "private"
            Visibility.PROTECTED -> "protected"
            Visibility.INTERNAL -> "internal"
            Visibility.LOCAL,
            Visibility.JAVA_PACKAGE,
            -> ""
        }

/**
 * Resolve the visibility modifier to emit for a generated copy function.
 *
 * This is the single source of truth for the [CopyVisibility] -> Kotlin modifier mapping.
 * [CopyVisibility.INHERIT] falls back to [inheritFrom]'s own visibility, preserving cream's
 * behaviour from before the `visibility` argument existed.
 */
internal fun CopyVisibility.toModifierString(inheritFrom: KSClassDeclaration): String =
    when (this) {
        CopyVisibility.INHERIT -> inheritFrom.visibilityStr
        CopyVisibility.PUBLIC -> "public"
        CopyVisibility.INTERNAL -> "internal"
    }

/**
 * Read a [CopyVisibility] from a `visibility` annotation argument. KSP surfaces enum
 * arguments as a [KSType] / [KSClassDeclaration] (or, defensively, an [Enum] / [String]).
 * Falls back to [CopyVisibility.INHERIT] when the argument is absent or unrecognised, so
 * omitting `visibility` keeps the prior behaviour.
 */
internal fun KSAnnotation.copyVisibilityArgument(name: String = "visibility"): CopyVisibility {
    val value =
        arguments
            .firstOrNull { it.name?.asString() == name }
            ?.value
            ?: return CopyVisibility.INHERIT
    val entryName =
        when (value) {
            is KSClassDeclaration -> value.simpleName.asString()
            is KSType -> value.declaration.simpleName.asString()
            is Enum<*> -> value.name
            is String -> value
            else -> return CopyVisibility.INHERIT
        }
    return runCatching { CopyVisibility.valueOf(entryName) }.getOrDefault(CopyVisibility.INHERIT)
}
