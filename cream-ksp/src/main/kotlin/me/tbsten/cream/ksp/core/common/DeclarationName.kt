package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSDeclaration
import me.tbsten.cream.ksp.UnknownCreamException
import kotlin.reflect.KClass

internal val KSDeclaration.fullName: String
    get() =
        qualifiedName?.asString()
            ?: throw UnknownCreamException("qualifiedName is null")

/**
 * The qualified name without the package prefix (e.g. `Outer.Args` for `com.example.Outer.Args`).
 * Uses `removePrefix` — not `replace` — so a package-name substring occurring later in the
 * qualified name is never touched, and a root-package declaration (empty package name, where
 * `replace(".", "")` would have destroyed every separator) is returned as-is.
 */
internal val KSDeclaration.underPackageName: String
    get() {
        val packagePrefix = packageName.asString()
        return if (packagePrefix.isEmpty()) fullName else fullName.removePrefix("$packagePrefix.")
    }

internal val KClass<*>.fullName: String
    get() =
        qualifiedName
            ?: throw UnknownCreamException("qualifiedName is null")
