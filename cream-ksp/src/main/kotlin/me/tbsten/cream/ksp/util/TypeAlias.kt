package me.tbsten.cream.ksp.util

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSTypeAlias

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
