package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Visibility

// This file holds the wrap / unwrap probes behind findValueClassConversionOutcome (issue #21):
// the soundness rules deciding which (target parameter type, source property type) pairs convert,
// which almost convert (near miss), and which are silently left required. The model, the entry
// points and the default-value rendering live in ValueClassMapping.kt.

/**
 * How many `value class` layers a single conversion may cross (issue #21). A chain like
 * `V8(V7(...V1(x)))` is already far beyond real-world modeling; the cap only exists so that
 * pathological (or unresolvable/erroneous) hierarchies cannot loop the probe forever.
 */
private const val MAX_VALUE_CLASS_CONVERSION_DEPTH = 8

/**
 * The conversion outcome for one (parameter type, candidate source property) pair. Unwrap is
 * probed first and wins over wrap (the deterministic ambiguity rule — see
 * [findValueClassConversionOutcome]); a wrap [ValueClassConversionOutcome.Applied] still beats an
 * unwrap near miss, so a real conversion is never lost to a warning.
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
 * Wrap probe: peels the target parameter type one value-class layer at a time (`V2` -> `V1` ->
 * `String`) until a layer's underlying type accepts the source property, then wraps with one
 * constructor call per peeled layer. A nullable source can still wrap through `?.let { ... }`
 * when the parameter type is nullable; when the parameter type is non-null that is unsound
 * (a `null` cannot become a value class instance) and reported as a near miss.
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
        // Rendering the layer from the resolved class declaration (not the alias) keeps the
        // constructor call valid even for a typealias parameter type; eligible value classes are
        // non-generic, so the star projection is the plain type.
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
 * Unwrap probe: follows the source property's underlying properties (`this.x` -> `this.x.v1` ->
 * `this.x.v1.value`) until the expression's type is assignable to the target parameter type.
 * Every hop after a nullable type uses `?.` and keeps the expression nullable — so once the
 * expression is nullable, a non-null parameter can never be satisfied; when only that nullability
 * blocks an otherwise matching hop, it is reported as a near miss.
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
        // Once the expression is nullable it stays nullable (later hops use `?.`), so a non-null
        // parameter that would accept the non-null flavor can only be a near miss.
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
 * Resolves [this] type to a value class this conversion can cross, or `null`:
 * a (typealias-resolved) class with [Modifier.VALUE] — detection is modifier-based, so both
 * `@JvmInline value class` and multiplatform `value class` / `expect value class` declarations
 * qualify — that is non-generic (generic value classes are an experimental Kotlin feature) and
 * not yet [visited] (cycle guard for erroneous hierarchies).
 */
private fun KSType.asEligibleValueClass(visited: MutableSet<String>): KSClassDeclaration? {
    val declaration = declaration.resolveToClassDeclaration() ?: return null
    if (Modifier.VALUE !in declaration.modifiers) return null
    if (declaration.typeParameters.isNotEmpty()) return null
    val qualifiedName = declaration.qualifiedName?.asString() ?: return null
    if (!visited.add(qualifiedName)) return null
    return declaration
}

/**
 * The single primary-constructor parameter `u` of a value class `V(val u: U)`, or `null` when
 * the constructor shape is unexpected (defensive: the compiler enforces this shape for value
 * classes, but erroneous or `expect` declarations may not carry it).
 */
private fun KSClassDeclaration.valueClassUnderlyingParameter(): KSValueParameter? = primaryConstructor?.parameters?.singleOrNull()

/**
 * Whether generated code can call [this] value class's primary constructor. Generated files live
 * in the processed module, so `internal` is fine for source declarations ([KSClassDeclaration.containingFile]
 * != `null`) but not for classpath ones (the `@CopyMapping` / `@CombineMapping` library-to-library
 * case); `private` / `protected` never are.
 */
private fun KSClassDeclaration.hasAccessiblePrimaryConstructor(): Boolean =
    when (primaryConstructor?.getVisibility()) {
        Visibility.PUBLIC -> true
        Visibility.INTERNAL -> containingFile != null
        else -> false
    }

/** [hasAccessiblePrimaryConstructor]'s counterpart for the underlying property read on unwrap. */
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
