package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Visibility

// The soundness rules behind findValueClassConversionOutcome; the model, entry points and
// default-value rendering live in ValueClassMapping.kt.

/** A guard against pathological or erroneous hierarchies looping the probe, not a modeling limit. */
private const val MAX_VALUE_CLASS_CONVERSION_DEPTH = 8

/**
 * The outcome for one (parameter type, candidate source property) pair. Unwrap is probed first and
 * wins (see [findValueClassConversionOutcome]), but a wrap [ValueClassConversionOutcome.Applied]
 * still beats an unwrap near miss, so a real conversion is never lost to a warning.
 */
internal fun conversionOutcomeFor(
    parameterName: String,
    targetType: KSType,
    sourceProperty: KSPropertyDeclaration,
): ValueClassConversionOutcome {
    val sourceType = sourceProperty.type.resolve()
    val unwrap = unwrapOutcome(parameterName, targetType, sourceType, sourceProperty)
    if (unwrap is ValueClassConversionOutcome.Applied) return unwrap
    val wrap = wrapOutcome(parameterName, targetType, sourceType, sourceProperty)
    if (wrap is ValueClassConversionOutcome.Applied) return wrap
    return unwrap.takeIf { it !is ValueClassConversionOutcome.None } ?: wrap
}

/**
 * Peels the target type one value-class layer at a time (`V2` -> `V1` -> `String`) until a layer's
 * underlying type accepts the source, then wraps once per peeled layer. A nullable source wraps
 * through `?.let { }` only when the parameter is nullable too — otherwise `null` would have to
 * become a value class instance, so that is a near miss.
 */
private fun wrapOutcome(
    parameterName: String,
    targetType: KSType,
    sourceType: KSType,
    sourceProperty: KSPropertyDeclaration,
): ValueClassConversionOutcome {
    val layers = mutableListOf<KSType>()
    val visited = mutableSetOf<String>()
    var constructorsAccessible = true
    var inaccessible: KSClassDeclaration? = null
    var current = targetType
    while (layers.size < MAX_VALUE_CLASS_CONVERSION_DEPTH) {
        val valueClass = current.asEligibleValueClass(visited) ?: return ValueClassConversionOutcome.None
        if (!valueClass.hasAccessiblePrimaryConstructor()) {
            constructorsAccessible = false
            if (inaccessible == null) inaccessible = valueClass
        }
        val underlyingType =
            valueClass
                .valueClassUnderlyingParameter()
                ?.type
                ?.resolve()
                ?: return ValueClassConversionOutcome.None
        // Render from the resolved declaration, not the alias, so a typealias parameter type still
        // produces a valid constructor call. Eligible value classes are non-generic.
        layers += if (current.declaration is KSTypeAlias) valueClass.asStarProjectedType() else current

        if (underlyingType.isAssignableFrom(sourceType)) {
            if (!constructorsAccessible) {
                return inaccessibleConstructorNearMiss(parameterName, inaccessible, sourceProperty)
            }
            return ValueClassConversionOutcome.Applied(
                ValueClassConversion.Wrap(sourceProperty = sourceProperty, layers = layers.toList(), useSafeCall = false),
            )
        }
        if (sourceType.isMarkedNullable && underlyingType.isAssignableFrom(sourceType.makeNotNullable())) {
            if (!targetType.isMarkedNullable) {
                return ValueClassConversionOutcome.NearMiss(
                    candidate = sourceProperty,
                    message =
                        "Automatic value class mapping for '$parameterName' skipped: " +
                            "source property '${sourceProperty.simpleName.asString()}' is nullable " +
                            "but the parameter type is non-null. " +
                            "Make the parameter nullable or pass the value explicitly.",
                )
            }
            if (!constructorsAccessible) {
                return inaccessibleConstructorNearMiss(parameterName, inaccessible, sourceProperty)
            }
            return ValueClassConversionOutcome.Applied(
                ValueClassConversion.Wrap(sourceProperty = sourceProperty, layers = layers.toList(), useSafeCall = true),
            )
        }
        current = underlyingType
    }
    return ValueClassConversionOutcome.None
}

/**
 * Follows the source's underlying properties (`this.x.v1.value`) until the expression type is
 * assignable to the parameter type. A hop after a nullable type uses `?.` and keeps the whole
 * expression nullable, so a non-null parameter can no longer be satisfied — a near miss.
 */
private fun unwrapOutcome(
    parameterName: String,
    targetType: KSType,
    sourceType: KSType,
    sourceProperty: KSPropertyDeclaration,
): ValueClassConversionOutcome {
    val accessors = mutableListOf<ValueClassConversion.UnderlyingAccess>()
    val visited = mutableSetOf<String>()
    var accessible = true
    var inaccessibleAccess: Pair<KSClassDeclaration, String>? = null
    var current = sourceType
    while (accessors.size < MAX_VALUE_CLASS_CONVERSION_DEPTH) {
        val valueClass = current.asEligibleValueClass(visited) ?: return ValueClassConversionOutcome.None
        val underlying = valueClass.valueClassUnderlyingParameter() ?: return ValueClassConversionOutcome.None
        val underlyingName = underlying.name?.asString() ?: return ValueClassConversionOutcome.None
        if (!valueClass.hasAccessibleUnderlyingProperty(underlyingName)) {
            accessible = false
            if (inaccessibleAccess == null) inaccessibleAccess = valueClass to underlyingName
        }
        val viaSafeCall = current.isMarkedNullable
        accessors += ValueClassConversion.UnderlyingAccess(propertyName = underlyingName, viaSafeCall = viaSafeCall)
        val underlyingType = underlying.type.resolve()
        current = if (viaSafeCall) underlyingType.makeNullable() else underlyingType

        if (targetType.isAssignableFrom(current)) {
            if (!accessible) {
                val (owner, name) = inaccessibleAccess ?: (valueClass to underlyingName)
                return ValueClassConversionOutcome.NearMiss(
                    candidate = sourceProperty,
                    message =
                        "Automatic value class mapping for '$parameterName' skipped: " +
                            "the underlying property '$name' of value class '${owner.simpleName.asString()}' " +
                            "is not accessible from generated code.",
                )
            }
            return ValueClassConversionOutcome.Applied(
                ValueClassConversion.Unwrap(sourceProperty = sourceProperty, accessors = accessors.toList()),
            )
        }
        if (current.isMarkedNullable &&
            !targetType.isMarkedNullable &&
            targetType.isAssignableFrom(current.makeNotNullable())
        ) {
            return ValueClassConversionOutcome.NearMiss(
                candidate = sourceProperty,
                message =
                    "Automatic value class mapping for '$parameterName' skipped: " +
                        "unwrapping '${sourceProperty.simpleName.asString()}' yields a nullable value " +
                        "but the parameter type is non-null. " +
                        "Make the parameter nullable or pass the value explicitly.",
            )
        }
    }
    return ValueClassConversionOutcome.None
}

private fun inaccessibleConstructorNearMiss(
    parameterName: String,
    valueClass: KSClassDeclaration?,
    sourceProperty: KSPropertyDeclaration,
): ValueClassConversionOutcome.NearMiss =
    ValueClassConversionOutcome.NearMiss(
        candidate = sourceProperty,
        message =
            "Automatic value class mapping for '$parameterName' skipped: " +
                "the primary constructor of value class '${valueClass?.simpleName?.asString()}' " +
                "is not accessible from generated code.",
    )

/**
 * The value class this conversion can cross, or `null`. Detection is modifier-based ([Modifier.VALUE]),
 * so `@JvmInline value class` and multiplatform / `expect value class` qualify alike. Generic value
 * classes (still experimental in Kotlin) are excluded, and [visited] guards cycles.
 */
private fun KSType.asEligibleValueClass(visited: MutableSet<String>): KSClassDeclaration? {
    val declaration = declaration.resolveToClassDeclaration() ?: return null
    if (Modifier.VALUE !in declaration.modifiers) return null
    if (declaration.typeParameters.isNotEmpty()) return null
    val qualifiedName = declaration.qualifiedName?.asString() ?: return null
    if (!visited.add(qualifiedName)) return null
    return declaration
}

/** The `u` of `V(val u: U)`, or `null` for an erroneous or `expect` declaration without it. */
private fun KSClassDeclaration.valueClassUnderlyingParameter(): KSValueParameter? = primaryConstructor?.parameters?.singleOrNull()

/**
 * Generated files live in the processed module, so `internal` is callable for source declarations
 * (`containingFile != null`) but not for classpath ones — the library-to-library mapping case.
 */
private fun KSClassDeclaration.hasAccessiblePrimaryConstructor(): Boolean =
    when (primaryConstructor?.getVisibility()) {
        Visibility.PUBLIC -> true
        Visibility.INTERNAL -> containingFile != null
        else -> false
    }

/** [hasAccessiblePrimaryConstructor]'s counterpart for the unwrap-side property read. */
private fun KSClassDeclaration.hasAccessibleUnderlyingProperty(underlyingName: String): Boolean {
    val property =
        getAllProperties().firstOrNull { it.simpleName.asString() == underlyingName }
            ?: return false
    return when (property.getVisibility()) {
        Visibility.PUBLIC -> true
        Visibility.INTERNAL -> containingFile != null
        else -> false
    }
}
