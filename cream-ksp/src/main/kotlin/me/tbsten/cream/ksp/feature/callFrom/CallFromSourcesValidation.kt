package me.tbsten.cream.ksp.feature.callFrom

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import me.tbsten.cream.CallFrom
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.core.callFrom.callFromSourceParamName
import me.tbsten.cream.ksp.core.common.deprecatedAnnotation
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.core.common.reportCreamError

private val annotationName = CallFrom::class.simpleName!!

/**
 * Reject a bridge whose generated code would reference a class deprecated with `ERROR` /
 * `HIDDEN`: unlike `WARNING` (which cream suppresses by propagating `@Deprecated` onto the
 * bridge), fatal deprecation levels are NOT silenced by an enclosing deprecated declaration in
 * K2, so the generated file itself would fail to compile. Checked for the annotated function's
 * enclosing classes (the bridge receiver) and every source class with its enclosing classes
 * (the bridge's first parameter type).
 */
internal fun KSPLogger.validateNoFatallyDeprecatedReferences(
    targetFunction: KSFunctionDeclaration,
    sourceClasses: List<KSClassDeclaration>,
): Boolean {
    val referencedDeclarations =
        generateSequence(targetFunction.parentDeclaration) { it.parentDeclaration } +
            sourceClasses.asSequence().flatMap { source ->
                generateSequence<KSDeclaration>(source) { it.parentDeclaration }
            }
    val fatallyDeprecated =
        referencedDeclarations.firstOrNull { declaration ->
            val deprecation = declaration.deprecatedAnnotation()
            deprecation != null && deprecation.level != DeprecationLevel.WARNING
        } ?: return true
    reportCreamError(
        InvalidCreamUsageException(
            message =
                "@$annotationName on ${targetFunction.displayName()} references " +
                    "${fatallyDeprecated.fullName}, which is deprecated with level " +
                    "${fatallyDeprecated.deprecatedAnnotation()?.level?.name}. The generated " +
                    "bridge could not reference it.",
            solution =
                "Lower the deprecation of ${fatallyDeprecated.fullName} to " +
                    "DeprecationLevel.WARNING, or remove @$annotationName.",
        ),
        targetFunction,
    )
    return false
}

/**
 * Reject an empty `sources` list: `@CallFrom()` would silently generate nothing, which is never
 * what the user meant.
 */
internal fun KSPLogger.validateSourcesNotEmpty(
    targetFunction: KSFunctionDeclaration,
    sourceClasses: List<KSClassDeclaration>,
): Boolean {
    if (sourceClasses.isNotEmpty()) return true
    reportCreamError(
        InvalidCreamUsageException(
            message = "@$annotationName on ${targetFunction.displayName()} has no source classes, so there is nothing to generate.",
            solution = "Specify at least one argument-holder class, e.g. @$annotationName(MyArgs::class).",
        ),
        targetFunction,
    )
    return false
}

/**
 * Reject `sources` listing the same class more than once: every source generates an overload
 * with the same name and signature shape, so duplicates would emit conflicting overloads.
 */
internal fun KSPLogger.validateNoDuplicatedSources(
    targetFunction: KSFunctionDeclaration,
    sourceClasses: List<KSClassDeclaration>,
): Boolean {
    val duplicatedSourceNames =
        sourceClasses
            .groupingBy { it.fullName }
            .eachCount()
            .filterValues { it > 1 }
            .keys
    if (duplicatedSourceNames.isEmpty()) return true
    reportCreamError(
        InvalidCreamUsageException(
            message =
                "@$annotationName on ${targetFunction.displayName()} lists the same source class " +
                    "more than once: ${duplicatedSourceNames.joinToString(", ")}. " +
                    "Duplicated sources would generate conflicting overloads with the same signature.",
            solution = "Remove the duplicated classes from @$annotationName.sources.",
        ),
        targetFunction,
    )
    return false
}

/**
 * Reject a source whose bridge parameter name (the source class's lowerCamelCase simple name)
 * collides with one of the target function's own parameters: the generated signature would
 * declare the same parameter name twice.
 */
internal fun KSPLogger.validateNoSourceParamNameCollision(
    targetFunction: KSFunctionDeclaration,
    sourceClasses: List<KSClassDeclaration>,
): Boolean {
    val existingParamNames =
        targetFunction.parameters
            .mapNotNull { it.name?.asString() }
            .toSet()
    val collidedSources =
        sourceClasses.filter { callFromSourceParamName(it) in existingParamNames }
    collidedSources.forEach { sourceClass ->
        reportCreamError(
            InvalidCreamUsageException(
                message =
                    "@$annotationName source ${sourceClass.fullName} produces the bridge parameter name " +
                        "\"${callFromSourceParamName(sourceClass)}\", which collides with a parameter of " +
                        "${targetFunction.displayName()}.",
                solution =
                    "Rename the colliding parameter of ${targetFunction.displayName()}, " +
                        "or rename ${sourceClass.fullName}.",
            ),
            targetFunction,
        )
    }
    return collidedSources.isEmpty()
}
