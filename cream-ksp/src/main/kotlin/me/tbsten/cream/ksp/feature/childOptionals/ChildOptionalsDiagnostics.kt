package me.tbsten.cream.ksp.feature.childOptionals

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import me.tbsten.cream.ChildOptionals
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.core.common.reportCreamError

private val annotationName = ChildOptionals::class.simpleName!!

/**
 * The blanket sweep found a property whose type references type parameter(s) the annotated
 * parent does not pin. A warning (not an error): the user did not single this property out, and
 * the rest of the hierarchy generates fine — but silence would make the missing accessor look
 * like a bug.
 */
internal fun KSPLogger.warnChildOptionalsUnpinnedTypeParameters(
    property: KSPropertyDeclaration,
    unpinnedNames: List<String>,
    parent: KSClassDeclaration,
) {
    val displayName = property.qualifiedName?.asString() ?: property.simpleName.asString()
    val parentName = parent.qualifiedName?.asString() ?: parent.simpleName.asString()
    warn(
        "@$annotationName skipped property $displayName: its type references type parameter(s) " +
            "${unpinnedNames.joinToString(", ")} not pinned by the sealed parent $parentName, so no accessor " +
            "can be generated for it. Pin the type parameter on $parentName (e.g. `Child<T> : Parent<T>`) " +
            "to include it.",
        property,
    )
}

/**
 * An `@ChildOptionals.Exclude` that removed nothing: the property is not swept into any generated
 * accessor (its class is outside a `@ChildOptionals` hierarchy, or it was already skipped for
 * another reason). A warning, not an error — the exclude is redundant but harmless. Mirrors the
 * `@CopyToChildren.Exclude` / `@CopyFrom.Exclude` no-effect warnings.
 */
internal fun KSPLogger.warnChildOptionalsExcludeHasNoEffect(property: KSPropertyDeclaration) {
    val displayName = property.qualifiedName?.asString() ?: property.simpleName.asString()
    warn(
        "@Exclude on '$displayName' has no effect: it is not swept into a @$annotationName-generated accessor.",
        property,
    )
}

internal fun KSPLogger.reportChildOptionalsNotADeclaration(annotated: KSAnnotated) {
    reportCreamError(
        InvalidCreamUsageException(
            message = "@$annotationName must be applied to a sealed class/interface.",
            solution = "Please apply @$annotationName to a `sealed class` or `sealed interface`.",
        ),
        annotated,
    )
}

internal fun KSPLogger.reportChildOptionalsNotSealed(annotated: KSClassDeclaration) {
    // Avoid `fullName`, which throws UnknownCreamException when qualifiedName is null
    // (e.g. local/anonymous declarations) and would mask this InvalidCreamUsageException.
    val displayName = annotated.qualifiedName?.asString() ?: annotated.simpleName.asString()
    reportCreamError(
        InvalidCreamUsageException(
            message =
                "@$annotationName must be applied to a sealed class/interface, " +
                    "but $displayName is not sealed (classKind: ${annotated.classKind}).",
            solution = "Make $displayName a `sealed class` or `sealed interface`.",
        ),
        annotated,
    )
}
