package me.tbsten.cream.ksp.feature.callFrom

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Visibility
import me.tbsten.cream.CallFrom
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.core.common.reportCreamError
import me.tbsten.cream.ksp.options.CreamOptions

private val annotationName = CallFrom::class.simpleName!!

/**
 * Compute the visibility modifier of the generated bridge, validating it against everything the
 * bridge's signature references: the annotated function itself, its enclosing classes (the
 * bridge's receiver type), and each source class with its enclosing classes (the bridge's first
 * parameter type).
 *
 * - Any `private` / `protected` / local declaration among them, or an `internal` one from
 *   another module, can never be referenced by the generated top-level bridge → positioned
 *   error, returns `null`.
 * - Any `internal` declaration caps the bridge at `internal`. An explicit
 *   `visibility = CopyVisibility.PUBLIC` (on the annotation or via the `cream.defaultVisibility`
 *   option) above the cap is a positioned error — kotlinc would reject the generated declaration
 *   with an exposure violation.
 * - Otherwise: an explicit request wins; `INHERIT` resolves to the cap itself, which equals the
 *   annotated function's own visibility unless a more restrictive symbol lowers it.
 */
internal fun KSPLogger.resolveCallFromBridgeVisibility(
    targetFunction: KSFunctionDeclaration,
    sourceClasses: List<KSClassDeclaration>,
    annotationVisibility: CopyVisibility,
    options: CreamOptions,
): String? {
    var internalConstraint: KSDeclaration? = null
    val functionChain = generateSequence<KSDeclaration>(targetFunction) { it.parentDeclaration }
    val sourceChains =
        sourceClasses.asSequence().flatMap { source ->
            generateSequence<KSDeclaration>(source) { it.parentDeclaration }
        }
    val referencedChains = functionChain + sourceChains

    for (declaration in referencedChains) {
        when (declaration.getVisibility()) {
            Visibility.PUBLIC,
            Visibility.JAVA_PACKAGE,
            -> Unit

            Visibility.INTERNAL -> {
                if (declaration.containingFile == null && declaration !== targetFunction) {
                    reportCreamError(
                        InvalidCreamUsageException(
                            message =
                                "@$annotationName on ${targetFunction.displayName()} references " +
                                    "${declaration.fullName}, which is internal to another module. " +
                                    "The generated bridge could not compile.",
                            solution = "Use a class that is visible from this module as the source.",
                        ),
                        targetFunction,
                    )
                    return null
                }
                if (internalConstraint == null) internalConstraint = declaration
            }

            Visibility.PRIVATE,
            Visibility.PROTECTED,
            Visibility.LOCAL,
            -> {
                // The function itself being private/protected is rejected earlier with a
                // dedicated message; reaching here means an enclosing or source declaration.
                reportCreamError(
                    InvalidCreamUsageException(
                        message =
                            "@$annotationName on ${targetFunction.displayName()} references " +
                                "${declaration.displayNameForDiagnostics()}, which is " +
                                "${declaration.getVisibility().name.lowercase()}. The generated " +
                                "top-level bridge function cannot reference it.",
                        solution =
                            "Make ${declaration.displayNameForDiagnostics()} `public` or `internal`, " +
                                "or remove @$annotationName.",
                    ),
                    targetFunction,
                )
                return null
            }
        }
    }

    val requested = annotationVisibility.takeIf { it != CopyVisibility.INHERIT }
    val requestedByOption = options.defaultVisibility.takeIf { it != CopyVisibility.INHERIT }
    val effectiveRequest = requested ?: requestedByOption

    if (effectiveRequest == CopyVisibility.PUBLIC && internalConstraint != null) {
        val origin =
            if (requested != null) {
                "visibility = CopyVisibility.PUBLIC on @$annotationName"
            } else {
                "the cream.defaultVisibility=PUBLIC option"
            }
        reportCreamError(
            InvalidCreamUsageException(
                message =
                    "$origin requires a public bridge for ${targetFunction.displayName()}, but " +
                        "${internalConstraint.displayNameForDiagnostics()} is internal. A public " +
                        "declaration must not expose an internal type.",
                solution =
                    "Make ${internalConstraint.displayNameForDiagnostics()} public, or use " +
                        "CopyVisibility.INTERNAL / CopyVisibility.INHERIT.",
            ),
            targetFunction,
        )
        return null
    }

    return when (effectiveRequest) {
        CopyVisibility.PUBLIC -> "public"
        CopyVisibility.INTERNAL -> "internal"
        CopyVisibility.INHERIT, null -> if (internalConstraint != null) "internal" else "public"
    }
}

private fun KSDeclaration.displayNameForDiagnostics(): String = qualifiedName?.asString() ?: simpleName.asString()
