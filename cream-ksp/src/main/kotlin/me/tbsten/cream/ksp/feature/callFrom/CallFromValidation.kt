package me.tbsten.cream.ksp.feature.callFrom

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Visibility
import me.tbsten.cream.CallFrom
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.core.common.deprecatedAnnotation
import me.tbsten.cream.ksp.core.common.reportCreamError
import me.tbsten.cream.ksp.util.safeCast

private val annotationName = CallFrom::class.simpleName!!

/**
 * Validate that [targetFunction] is a function kind `@CallFrom` can bridge. Unsupported kinds
 * (private / protected / local / abstract / expect / `ERROR`- or `HIDDEN`-deprecated / member
 * extension (double receiver) / reified / member of a generic class) are reported as clean
 * positioned `COMPILATION_ERROR`s and `false` is returned so the caller skips the function
 * without emitting a partial file.
 */
internal fun KSPLogger.validateCallFromTargetFunction(targetFunction: KSFunctionDeclaration): Boolean {
    val displayName = targetFunction.displayName()

    if (targetFunction.isDeclaredInLocalScope()) {
        reportCreamError(
            InvalidCreamUsageException(
                message = "@$annotationName cannot be applied to a local function: $displayName.",
                solution = "Move $displayName to the top level or into a class.",
            ),
            targetFunction,
        )
        return false
    }

    if (Modifier.EXPECT in targetFunction.modifiers) {
        reportCreamError(
            InvalidCreamUsageException(
                message =
                    "@$annotationName cannot be applied to an expect function: $displayName. " +
                        "KSP processes each platform compilation separately, so the generated " +
                        "bridge cannot be matched with the actual declarations.",
                solution =
                    "Apply @$annotationName to a regular (non-expect) function in common code, " +
                        "or to the actual platform functions.",
            ),
            targetFunction,
        )
        return false
    }

    when (targetFunction.getVisibility()) {
        Visibility.PRIVATE,
        Visibility.PROTECTED,
        -> {
            reportCreamError(
                InvalidCreamUsageException(
                    message =
                        "@$annotationName cannot be applied to a private/protected function: $displayName. " +
                            "The generated bridge function is top-level and cannot call it.",
                    solution = "Make $displayName `public` or `internal`.",
                ),
                targetFunction,
            )
            return false
        }

        Visibility.PUBLIC,
        Visibility.INTERNAL,
        Visibility.LOCAL,
        Visibility.JAVA_PACKAGE,
        -> Unit
    }

    if (targetFunction.isAbstract) {
        reportCreamError(
            InvalidCreamUsageException(
                message = "@$annotationName cannot be applied to an abstract function: $displayName.",
                solution = "Apply @$annotationName to a concrete function.",
            ),
            targetFunction,
        )
        return false
    }

    val deprecation = targetFunction.deprecatedAnnotation()
    if (deprecation != null && deprecation.level != DeprecationLevel.WARNING) {
        reportCreamError(
            InvalidCreamUsageException(
                message =
                    "@$annotationName cannot be applied to a function deprecated with " +
                        "level ${deprecation.level.name}: $displayName. A call to it does not " +
                        "compile, so the generated bridge could not delegate to it.",
                solution =
                    "Lower the deprecation of $displayName to DeprecationLevel.WARNING " +
                        "(cream propagates it onto the bridge), or remove @$annotationName.",
            ),
            targetFunction,
        )
        return false
    }

    if (targetFunction.extensionReceiver != null && targetFunction.parentDeclaration != null) {
        reportCreamError(
            InvalidCreamUsageException(
                message =
                    "@$annotationName cannot be applied to a member extension function: $displayName. " +
                        "The bridge would need both the dispatch receiver and the extension " +
                        "receiver, which a generated top-level function cannot declare.",
                solution = "Move $displayName to the top level, or make it a plain member function.",
            ),
            targetFunction,
        )
        return false
    }

    if (targetFunction.typeParameters.any { it.isReified }) {
        reportCreamError(
            InvalidCreamUsageException(
                message =
                    "@$annotationName cannot be applied to a function with a reified type parameter: " +
                        "$displayName. The generated bridge function is not inline, so it cannot " +
                        "forward a reified type parameter.",
                solution = "Apply @$annotationName to a function without `reified` type parameters.",
            ),
            targetFunction,
        )
        return false
    }

    if (targetFunction.enclosingGenericClassOrNull() != null) {
        reportCreamError(
            InvalidCreamUsageException(
                message =
                    "@$annotationName cannot be applied to a member function of a generic class: " +
                        "$displayName (not supported yet).",
                solution = "Apply @$annotationName to a member function of a non-generic class.",
            ),
            targetFunction,
        )
        return false
    }

    return true
}

/**
 * Whether [this] is declared in a local scope, where no stable fully-qualified name exists for
 * the generated bridge to reference: a local function itself, a member function of a local
 * class (the enclosing chain passes through a function body), or a member of an anonymous
 * object (`qualifiedName == null` somewhere on the chain). A plain direct-parent check misses
 * the latter two, which would crash later on the null `qualifiedName`.
 */
private fun KSFunctionDeclaration.isDeclaredInLocalScope(): Boolean {
    if (qualifiedName == null) return true
    var enclosing: KSDeclaration? = parentDeclaration
    while (enclosing != null) {
        if (enclosing !is KSClassDeclaration || enclosing.qualifiedName == null) return true
        enclosing = enclosing.parentDeclaration
    }
    return false
}

/**
 * Find the class whose type parameters the generated bridge's receiver type would have to spell
 * out, or `null` if there is none: the direct enclosing class when it is generic itself, or —
 * because an `inner` class type must carry its outer class's type arguments
 * (`Outer<T>.Inner`) — the first generic class reached by walking outwards while the chain
 * consists of `inner` classes. A nested (non-`inner`) class does not capture outer type
 * parameters, so the walk stops there.
 */
private fun KSFunctionDeclaration.enclosingGenericClassOrNull(): KSClassDeclaration? {
    var enclosing = parentDeclaration.safeCast<KSClassDeclaration>() ?: return null
    while (true) {
        if (enclosing.typeParameters.isNotEmpty()) return enclosing
        if (Modifier.INNER !in enclosing.modifiers) return null
        enclosing = enclosing.parentDeclaration.safeCast<KSClassDeclaration>() ?: return null
    }
}

/**
 * Human-readable name for diagnostics. Avoid `fullName`, which throws when qualifiedName is
 * null (e.g. local functions) and would mask the InvalidCreamUsageException being reported.
 */
internal fun KSFunctionDeclaration.displayName(): String = qualifiedName?.asString() ?: simpleName.asString()
