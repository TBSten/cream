package me.tbsten.cream.ksp.util

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSTypeAlias
import me.tbsten.cream.ksp.InvalidCreamUsageException

/**
 * Resolves a type alias to its underlying class declaration.
 *
 * If the declaration is already a KSClassDeclaration, returns it directly.
 * If it's a KSTypeAlias, recursively resolves it to the underlying class.
 *
 * @return The underlying KSClassDeclaration, or null if resolution fails
 */
internal fun KSDeclaration.resolveToClassDeclaration(): KSClassDeclaration? =
    when (this) {
        is KSClassDeclaration -> this
        is KSTypeAlias -> {
            // Resolve the type alias to its underlying type
            val underlyingType = this.type.resolve()
            val underlyingDeclaration = underlyingType.declaration

            // Recursively resolve in case of nested type aliases
            underlyingDeclaration.resolveToClassDeclaration()
        }
        else -> null
    }

/**
 * Build the [InvalidCreamUsageException] describing why this declaration could not be resolved to a
 * class. Single source of truth for the message used by [resolveClassDeclarationOrReport].
 */
private fun KSDeclaration.unresolvableClassException(
    annotationName: String,
    context: String,
): InvalidCreamUsageException =
    InvalidCreamUsageException(
        message =
            if (context.isEmpty()) {
                "$fullName must be class, interface, or typealias."
            } else {
                "$fullName ($context) must be class, interface, or typealias."
            },
        solution =
            if (context.isEmpty()) {
                "Please apply @$annotationName to `class`, `interface`, or `typealias`"
            } else {
                "Specify class, interface, or typealias in $context."
            },
    )

/**
 * Resolves a declaration to a class declaration, emitting a clean positioned `COMPILATION_ERROR`
 * via [KSPLogger.error] (anchored at [ksNode], defaulting to this declaration) and returning `null`
 * instead of throwing when resolution fails, so the caller can skip the offending unit without
 * crashing KSP with an `INTERNAL_ERROR`.
 *
 * @param annotationName The name of the annotation being processed (e.g., "CopyTo", "CopyFrom")
 * @param context Additional context for the error message (e.g., "Specified in @CopyTo.targets of Foo")
 */
internal fun KSDeclaration.resolveClassDeclarationOrReport(
    annotationName: String,
    logger: KSPLogger,
    context: String = "",
    ksNode: KSNode = this,
): KSClassDeclaration? {
    val resolved = this.resolveToClassDeclaration()
    if (resolved == null) {
        logger.error(unresolvableClassException(annotationName, context).message.orEmpty(), ksNode)
    }
    return resolved
}
