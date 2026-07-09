package me.tbsten.cream.ksp.feature.callFrom

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import me.tbsten.cream.CallFrom
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.core.callFrom.CallFromBridgeSignature
import me.tbsten.cream.ksp.core.callFrom.callFromBridgeSignature
import me.tbsten.cream.ksp.core.callFrom.existingFunctionSignature
import me.tbsten.cream.ksp.core.callFrom.resolveCallFromFunName
import me.tbsten.cream.ksp.core.common.GenerateSourceAnnotation
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.core.common.reportCreamError
import me.tbsten.cream.ksp.core.common.underPackageName

private val annotationName = CallFrom::class.simpleName!!

/**
 * One annotated function's pending generation: the validated target function, its resolved
 * source classes, the triggering annotation, and the pre-computed visibility modifier. Bridges
 * for every unit sharing the same generated file name are emitted into one file (two same-name
 * overloads both annotated with `@CallFrom` used to crash the processor with a
 * `FileAlreadyExistsException`).
 */
internal class CallFromGenerationUnit(
    val targetFunction: KSFunctionDeclaration,
    val sourceClasses: List<KSClassDeclaration>,
    val generateSourceAnnotation: GenerateSourceAnnotation.CallFrom,
    val visibilityModifier: String,
) {
    val fileName: String get() = "CallFrom__${targetFunction.underPackageName.replace(".", "_")}"

    /**
     * The (raw) name every bridge of this unit is declared with — the target function's own name
     * by default, or the custom `funName`. Collisions are keyed on this, not the target function's
     * name, so a custom `funName` shared across functions (or matching an existing declaration) is
     * detected while distinct `funName`s on same-name overloads no longer conflict.
     */
    val bridgeName: String get() = resolveCallFromFunName(generateSourceAnnotation.funNameTemplate, targetFunction)
}

private class BridgeDescriptor(
    val unit: CallFromGenerationUnit,
    val source: KSClassDeclaration,
    val signature: CallFromBridgeSignature,
) {
    /** Bridges can only conflict within the same package + bridge name + receiver. */
    val scopeKey: String
        get() =
            listOf(
                unit.targetFunction.packageName.asString(),
                unit.bridgeName,
                signature.receiver.orEmpty(),
            ).joinToString("|")
}

/**
 * Drop units whose generated bridge would be a redeclaration conflict, reporting a positioned
 * error for each. Two kinds are detected:
 *
 * 1. **bridge vs bridge** — two annotated functions (same name, same scope) whose bridges end up
 *    with identical parameter types, e.g. `fun f(x: Int)` / `fun f(y: Int)` both annotated with
 *    the same source. kotlinc would reject the generated file with "conflicting overloads".
 * 2. **bridge vs existing function** — a user-written top-level function already has exactly the
 *    signature a bridge would get. Without this check the error would appear inside the
 *    generated file, pointing at code the user never wrote.
 *
 * A colliding unit is skipped entirely (all its sources), so no partial file is emitted for it;
 * non-colliding units sharing the same file still generate.
 */
internal fun KSPLogger.filterOutCollidingUnits(
    resolver: Resolver,
    units: List<CallFromGenerationUnit>,
): List<CallFromGenerationUnit> {
    val descriptors =
        units.flatMap { unit ->
            unit.sourceClasses.map { source ->
                BridgeDescriptor(
                    unit = unit,
                    source = source,
                    signature = callFromBridgeSignature(source, unit.targetFunction, unit.generateSourceAnnotation),
                )
            }
        }

    val failedUnits = mutableSetOf<CallFromGenerationUnit>()

    descriptors
        .groupBy { it.scopeKey to it.signature }
        .values
        .filter { it.size > 1 }
        .forEach { colliding ->
            colliding
                .distinctBy { it.unit }
                .forEach { descriptor ->
                    val others =
                        colliding
                            .filter { it.unit != descriptor.unit }
                            .joinToString(", ") { it.unit.targetFunction.displayName() }
                    reportCreamError(
                        InvalidCreamUsageException(
                            message =
                                "@$annotationName source ${descriptor.source.fullName} on " +
                                    "${descriptor.unit.targetFunction.displayName()} generates a bridge with " +
                                    "the same signature as the bridge generated for: $others. " +
                                    "kotlinc would reject the generated overloads as conflicting.",
                            solution =
                                "Give them distinct funName values, distinguishable parameter lists, or " +
                                    "different source classes; or rename one of the functions.",
                        ),
                        descriptor.unit.targetFunction,
                    )
                    failedUnits += descriptor.unit
                }
        }

    descriptors
        .filter { it.unit !in failedUnits }
        .forEach { descriptor ->
            val clashingExisting =
                resolver
                    .topLevelFunctionsNamed(
                        packageName =
                            descriptor.unit.targetFunction.packageName
                                .asString(),
                        simpleName = descriptor.unit.bridgeName,
                    ).firstOrNull { existing -> existing.existingFunctionSignature() == descriptor.signature }
            if (clashingExisting != null) {
                reportCreamError(
                    InvalidCreamUsageException(
                        message =
                            "@$annotationName source ${descriptor.source.fullName} on " +
                                "${descriptor.unit.targetFunction.displayName()} would generate a bridge with " +
                                "the same signature as the existing function " +
                                "${clashingExisting.displayName()}. kotlinc would reject the generated " +
                                "declaration as a conflicting overload.",
                        solution =
                            "Give the bridge a distinct funName, remove the existing overload " +
                                "${clashingExisting.displayName()}, or remove ${descriptor.source.fullName} " +
                                "from @$annotationName.sources.",
                    ),
                    descriptor.unit.targetFunction,
                )
                failedUnits += descriptor.unit
            }
        }

    return units.filter { it !in failedUnits }
}

/**
 * All top-level functions named [simpleName] in [packageName] (including extension functions —
 * the only declarations a generated top-level bridge can conflict with).
 */
private fun Resolver.topLevelFunctionsNamed(
    packageName: String,
    simpleName: String,
): Sequence<KSFunctionDeclaration> {
    val qualified =
        listOf(packageName, simpleName)
            .filter { it.isNotEmpty() }
            .joinToString(".")
    return getFunctionDeclarationsByName(getKSNameFromString(qualified), true)
}
