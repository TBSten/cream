package me.tbsten.cream.ksp.core.sealedCopy

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import me.tbsten.cream.SealedCopy
import me.tbsten.cream.ksp.core.common.annotationsOf
import me.tbsten.cream.ksp.core.common.underPackageName
import me.tbsten.cream.ksp.util.ksp.getArgument

/**
 * A single `@SealedCopy.Via` misuse, ready to be reported as a positioned `COMPILATION_ERROR`
 * ([message] anchored at [node]).
 */
internal data class SealedCopyViaError(
    val message: String,
    val node: KSNode,
)

/**
 * Validate every subtype's [SealedCopy.Via] usage against the sealed parent's [abstractProperties], applying
 * the same strictness the default `copy` path enforces (the delegate must supply every abstract property).
 * Returns one [SealedCopyViaError] per problem; an empty list means the `@Via` delegates are safe to generate.
 *
 * Rules (all → `logger.error`):
 *  0. At most one `@SealedCopy.Via` per subtype (multiple would silently pick one).
 *  1. Every abstract property must be supplied by some `@Via` parameter (by name or via [SealedCopy.Map]);
 *     otherwise the update would be silently dropped.
 *  2. Every `@Via` parameter must either bind to an abstract property or have a default value; otherwise cream
 *     cannot supply it and the call would fail at the user's compiler.
 *  3. A [SealedCopy.Map]`("x")` must reference an existing abstract property, and the parameter type must be
 *     assignable from that property's type.
 *  4. The `@Via` function's return type must be assignable to the subtype itself; its result stands in for
 *     the subtype in the generated `when` branch, so anything else would fail at the user's compiler.
 */
internal fun KSClassDeclaration.collectSealedCopyViaErrors(abstractProperties: List<KSPropertyDeclaration>): List<SealedCopyViaError> {
    val viaFunctions = viaFunctions()
    if (viaFunctions.isEmpty()) return emptyList()

    if (viaFunctions.size > 1) {
        val names = viaFunctions.joinToString { it.simpleName.asString() }
        return listOf(
            SealedCopyViaError(
                message =
                    "Subtype '$underPackageName' declares more than one @SealedCopy.Via function ($names). " +
                        "Mark exactly one function with @SealedCopy.Via.",
                node = this,
            ),
        )
    }

    val via = viaFunctions.first()
    val abstractByName = abstractProperties.associateBy { it.simpleName.asString() }
    val errors = mutableListOf<SealedCopyViaError>()

    // Rules 2 & 3, per parameter.
    via.parameters.forEach { param ->
        val paramName = param.name?.asString() ?: return@forEach
        val mapValue =
            param
                .annotationsOf(SealedCopy.Map::class)
                .firstOrNull()
                ?.getArgument<String>("value")
        val abstractName = mapValue ?: paramName
        val abstractProperty = abstractByName[abstractName]

        when {
            abstractProperty != null -> {
                // Rule 3 (type): the parameter must accept the abstract property's value.
                if (!isParameterAssignableFromProperty(
                        abstractProperty.type.resolve(),
                        param.type.resolve(),
                    )
                ) {
                    errors +=
                        SealedCopyViaError(
                            message =
                                "@SealedCopy.Via parameter '$paramName' is not assignable from abstract property " +
                                    "'$abstractName'. Its type must accept the property's value.",
                            node = param,
                        )
                }
            }

            // Rule 3 (existence): @SealedCopy.Map("x") named a property that is not an abstract property.
            mapValue != null ->
                errors +=
                    SealedCopyViaError(
                        message =
                            "@SealedCopy.Map(\"$mapValue\") on @SealedCopy.Via parameter '$paramName' does not " +
                                "reference an abstract property of the sealed parent. " +
                                "Known abstract properties: ${abstractByName.keys.joinToString()}.",
                        node = param,
                    )

            // Rule 2: an unmapped parameter with no default cannot be supplied by cream.
            !param.hasDefault ->
                errors +=
                    SealedCopyViaError(
                        message =
                            "@SealedCopy.Via parameter '$paramName' neither matches an abstract property " +
                                "(by name or @SealedCopy.Map) nor has a default value, so cream cannot supply it. " +
                                "Rename it to an abstract property, add @SealedCopy.Map(\"<property>\"), or give it a default.",
                        node = param,
                    )
        }
    }

    // Rule 4: the delegate's result stands in for this subtype in the generated `when` branch, so its
    // return type must be assignable to the subtype itself. Star-project both sides so generic subtypes
    // (e.g. `Success<T>`) compare by declaration rather than by unresolvable type arguments. Error types
    // are skipped: the user's compiler already reports them.
    val returnType = via.returnType?.resolve()
    if (returnType != null &&
        !returnType.isError &&
        !asStarProjectedType().isAssignableFrom(returnType.starProjection())
    ) {
        errors +=
            SealedCopyViaError(
                message =
                    "@SealedCopy.Via function '${via.simpleName.asString()}' returns '$returnType', " +
                        "which is not assignable to '$underPackageName'. " +
                        "Change its return type to '$underPackageName' (or a subtype of it).",
                node = via,
            )
    }

    // Rule 1: every abstract property must be covered by some parameter.
    val coveredNames =
        via.parameters
            .mapNotNull { param -> param.mappedAbstractPropertyName() }
            .filter { abstractByName.containsKey(it) }
            .toSet()
    val missing = abstractProperties.map { it.simpleName.asString() }.filter { it !in coveredNames }
    if (missing.isNotEmpty()) {
        errors +=
            SealedCopyViaError(
                message =
                    "@SealedCopy.Via function '${via.simpleName.asString()}' does not supply every abstract " +
                        "property of '$underPackageName'. Missing: ${missing.joinToString()}. " +
                        "Add a parameter named after each missing property (or @SealedCopy.Map(\"<property>\")).",
                node = via,
            )
    }

    return errors
}
