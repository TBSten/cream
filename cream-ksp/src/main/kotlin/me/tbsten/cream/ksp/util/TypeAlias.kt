package me.tbsten.cream.ksp.util

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
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
 * Resolves a declaration to a class declaration, throwing an exception if it fails.
 *
 * @param annotationName The name of the annotation being processed (e.g., "CopyTo", "CopyFrom")
 * @param context Additional context for the error message (e.g., "Specified in @CopyTo.targets of Foo")
 * @return The resolved KSClassDeclaration
 * @throws InvalidCreamUsageException if the declaration cannot be resolved to a class
 */
internal fun KSDeclaration.requireClassDeclaration(
    annotationName: String,
    context: String = "",
): KSClassDeclaration =
    this.resolveToClassDeclaration()
        ?: throw InvalidCreamUsageException(
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
