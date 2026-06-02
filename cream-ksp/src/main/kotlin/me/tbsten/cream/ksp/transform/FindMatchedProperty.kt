package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import me.tbsten.cream.CombineFrom
import me.tbsten.cream.CombineTo
import me.tbsten.cream.CopyFrom
import me.tbsten.cream.CopyTo
import me.tbsten.cream.ksp.GenerateSourceAnnotation

/**
 * Source properties eligible for auto-copy (`= this.x`).
 *
 * Auto-copy reads `this.x` at call time, so a source property qualifies only when reading it on a
 * fully-constructed instance is safe and side-effect-free:
 * - Constructor parameters and plain initialised properties have a backing field -> included.
 * - Abstract properties (an `abstract`/interface contract, e.g. the shared property of a sealed
 *   source used by `@CopyToChildren`) are included: the source declares no read implementation, so
 *   `this.x` dispatches to the concrete subclass's (stored) override, which is safe to read.
 * - Computed (`get()`-only) and delegated (`by`) members have no backing field -> excluded
 *   (a delegate must not be force-evaluated, e.g. `by lazy`, and a getter body must not be run).
 * - `lateinit var` members do have a backing field but may be uninitialised, so reading them can
 *   throw `UninitializedPropertyAccessException` -> excluded via the [Modifier.LATEINIT] check.
 *
 * Excluded source properties simply do not match any target parameter, so that parameter stays
 * REQUIRED (no `= this.x` default), exactly like a target parameter with no source counterpart.
 */
private fun KSClassDeclaration.autoCopyableProperties(): Sequence<KSPropertyDeclaration> =
    getAllProperties()
        .filter { it.isAbstract() || (it.hasBackingField && Modifier.LATEINIT !in it.modifiers) }

internal fun KSValueParameter.findMatchedProperty(
    source: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
): KSPropertyDeclaration? {
    val parameterName =
        this.name?.asString()
            ?: return null

    findSourcePropertyWithCopyToAnnotation(source, parameterName)
        ?.let { return it }

    findSourcePropertyWithCombineToAnnotation(source, parameterName)
        ?.let { return it }

    findSourcePropertyWithCopyFromAnnotation(source)
        ?.let { return it }

    findSourcePropertyWithCombineFromAnnotationOnTarget(source)
        ?.let { return it }

    findSourcePropertyWithCombineFromAnnotationOnSource(source, parameterName)
        ?.let { return it }

    val copyMappingPropertyMappings =
        (generateSourceAnnotation as? GenerateSourceAnnotation.CopyMapping)
            ?.propertyMappings
            ?: emptyList()

    findSourcePropertyWithCopyMappingAnnotation(source, parameterName, copyMappingPropertyMappings)
        ?.let { return it }

    val combineMappingPropertyMappings =
        (generateSourceAnnotation as? GenerateSourceAnnotation.CombineMapping)
            ?.propertyMappings
            ?: emptyList()

    findSourcePropertyWithCombineMappingAnnotation(source, parameterName, combineMappingPropertyMappings)
        ?.let { return it }

    return findSourcePropertyByName(source, parameterName)
}

/**
 * Find source property using CopyMapping.Map property mappings
 *
 * Property mappings define explicit source -> target property name mappings.
 * For example, if mapping is ("xProp" -> "yProp"), then when looking for target parameter "yProp",
 * this function will find source property "xProp".
 */
private fun KSValueParameter.findSourcePropertyWithCopyMappingAnnotation(
    source: KSClassDeclaration,
    parameterName: String,
    propertyMappings: List<Pair<String, String>>,
): KSPropertyDeclaration? {
    val sourcePropertyName =
        propertyMappings
            .firstOrNull { (_, target) ->
                target == parameterName
            }?.first ?: return null

    return source
        .autoCopyableProperties()
        .firstOrNull {
            it.simpleName.asString() == sourcePropertyName &&
                this.matchesSourcePropertyType(it.type.resolve())
        }
}

/**
 * Find source property using CombineMapping.Map property mappings
 *
 * Property mappings define explicit source -> target property name mappings.
 * For example, if mapping is ("xProp" -> "yProp"), then when looking for target parameter "yProp",
 * this function will find source property "xProp".
 */
private fun KSValueParameter.findSourcePropertyWithCombineMappingAnnotation(
    source: KSClassDeclaration,
    parameterName: String,
    propertyMappings: List<Pair<String, String>>,
): KSPropertyDeclaration? {
    val sourcePropertyName =
        propertyMappings
            .firstOrNull { (_, target) ->
                target == parameterName
            }?.first ?: return null

    return source
        .autoCopyableProperties()
        .firstOrNull {
            it.simpleName.asString() == sourcePropertyName &&
                this.matchesSourcePropertyType(it.type.resolve())
        }
}

private fun KSValueParameter.findSourcePropertyWithCopyToAnnotation(
    source: KSClassDeclaration,
    parameterName: String,
): KSPropertyDeclaration? =
    source
        .autoCopyableProperties()
        .firstOrNull { sourceProperty ->
            // First try to get annotation from property itself
            val propertyAnnotation =
                sourceProperty
                    .getAnnotationsByType(CopyTo.Map::class)
                    .firstOrNull()

            // If not found on property, try to get it from the corresponding constructor parameter
            val constructorParamAnnotation =
                if (propertyAnnotation == null) {
                    source.primaryConstructor
                        ?.parameters
                        ?.firstOrNull { it.name?.asString() == sourceProperty.simpleName.asString() }
                        ?.getAnnotationsByType(CopyTo.Map::class)
                        ?.firstOrNull()
                } else {
                    null
                }

            val copyToPropertyAnnotation = propertyAnnotation ?: constructorParamAnnotation

            if (copyToPropertyAnnotation != null) {
                parameterName in copyToPropertyAnnotation.propertyNames &&
                    this.matchesSourcePropertyType(sourceProperty.type.resolve())
            } else {
                false
            }
        }

private fun KSValueParameter.findSourcePropertyWithCombineToAnnotation(
    source: KSClassDeclaration,
    parameterName: String,
): KSPropertyDeclaration? =
    source
        .autoCopyableProperties()
        .firstOrNull { sourceProperty ->
            // First try to get annotation from property itself
            val propertyAnnotation =
                sourceProperty
                    .getAnnotationsByType(CombineTo.Map::class)
                    .firstOrNull()

            // If not found on property, try to get it from the corresponding constructor parameter
            val constructorParamAnnotation =
                if (propertyAnnotation == null) {
                    source.primaryConstructor
                        ?.parameters
                        ?.firstOrNull { it.name?.asString() == sourceProperty.simpleName.asString() }
                        ?.getAnnotationsByType(CombineTo.Map::class)
                        ?.firstOrNull()
                } else {
                    null
                }

            val combineToPropertyAnnotation = propertyAnnotation ?: constructorParamAnnotation

            if (combineToPropertyAnnotation != null) {
                parameterName in combineToPropertyAnnotation.propertyNames &&
                    this.matchesSourcePropertyType(sourceProperty.type.resolve())
            } else {
                false
            }
        }

/**
 * Find source property using @CombineFrom.Map annotation on target parameter.
 * Target parameter specifies which source property to use.
 * Example: @CombineFrom.Map("sourcePropertyB") val targetProperty: String
 */
private fun KSValueParameter.findSourcePropertyWithCombineFromAnnotationOnTarget(source: KSClassDeclaration): KSPropertyDeclaration? {
    val combineFromPropertyAnnotation =
        this
            .getAnnotationsByType(CombineFrom.Map::class)
            .firstOrNull()

    if (combineFromPropertyAnnotation != null) {
        val sourcePropertyNames = combineFromPropertyAnnotation.propertyNames

        return source
            .autoCopyableProperties()
            .firstOrNull {
                it.simpleName.asString() in sourcePropertyNames &&
                    this.matchesSourcePropertyType(it.type.resolve())
            }
    }

    return null
}

/**
 * Find source property using @CombineFrom.Map annotation on source property.
 * Source property specifies which target parameters it maps to.
 * Example: @CombineFrom.Map("targetPropertyA") val sourceProperty: String
 */
private fun KSValueParameter.findSourcePropertyWithCombineFromAnnotationOnSource(
    source: KSClassDeclaration,
    parameterName: String,
): KSPropertyDeclaration? =
    source
        .autoCopyableProperties()
        .firstOrNull { sourceProperty ->
            // First try to get annotation from property itself
            val propertyAnnotation =
                sourceProperty
                    .getAnnotationsByType(CombineFrom.Map::class)
                    .firstOrNull()

            // If not found on property, try to get it from the corresponding constructor parameter
            val constructorParamAnnotation =
                if (propertyAnnotation == null) {
                    source.primaryConstructor
                        ?.parameters
                        ?.firstOrNull { it.name?.asString() == sourceProperty.simpleName.asString() }
                        ?.getAnnotationsByType(CombineFrom.Map::class)
                        ?.firstOrNull()
                } else {
                    null
                }

            val combineFromPropertyAnnotation = propertyAnnotation ?: constructorParamAnnotation

            if (combineFromPropertyAnnotation != null) {
                parameterName in combineFromPropertyAnnotation.propertyNames &&
                    this.matchesSourcePropertyType(sourceProperty.type.resolve())
            } else {
                false
            }
        }

private fun KSValueParameter.findSourcePropertyWithCopyFromAnnotation(source: KSClassDeclaration): KSPropertyDeclaration? {
    val copyFromPropertyAnnotation =
        this
            .getAnnotationsByType(CopyFrom.Map::class)
            .firstOrNull()

    if (copyFromPropertyAnnotation != null) {
        val sourcePropertyNames = copyFromPropertyAnnotation.propertyNames

        return source
            .autoCopyableProperties()
            .firstOrNull {
                it.simpleName.asString() in sourcePropertyNames &&
                    this.matchesSourcePropertyType(it.type.resolve())
            }
    }

    return null
}

private fun KSValueParameter.findSourcePropertyByName(
    source: KSClassDeclaration,
    parameterName: String,
): KSPropertyDeclaration? =
    source
        .autoCopyableProperties()
        .firstOrNull {
            it.simpleName.asString() == parameterName &&
                this.matchesSourcePropertyType(it.type.resolve())
        }

/**
 * Element qualified name -> the primitive array qualified name a non-null primitive `vararg`
 * exposes. A `vararg nums: Int` property is an `IntArray` (not `Array<Int>`), so a source
 * property matching it must be that exact primitive array. Nullable primitives box to
 * `Array<out E?>` and are handled via the [kotlin.Array] branch instead.
 */
private val primitiveArrayByElement =
    mapOf(
        "kotlin.Int" to "kotlin.IntArray",
        "kotlin.Long" to "kotlin.LongArray",
        "kotlin.Short" to "kotlin.ShortArray",
        "kotlin.Byte" to "kotlin.ByteArray",
        "kotlin.Char" to "kotlin.CharArray",
        "kotlin.Boolean" to "kotlin.BooleanArray",
        "kotlin.Float" to "kotlin.FloatArray",
        "kotlin.Double" to "kotlin.DoubleArray",
        "kotlin.UInt" to "kotlin.UIntArray",
        "kotlin.ULong" to "kotlin.ULongArray",
        "kotlin.UShort" to "kotlin.UShortArray",
        "kotlin.UByte" to "kotlin.UByteArray",
    )

/**
 * Compatibility check between this target constructor parameter and a candidate source
 * property's type, accounting for `vararg`.
 *
 * For a non-`vararg` parameter this is the plain [isTypeCompatible] element comparison.
 * For a `vararg` parameter, `this.type.resolve()` is the ELEMENT type but the property the
 * source exposes is an ARRAY, so the element type must be lifted to the corresponding array
 * type before comparing:
 * - non-null primitive element -> source must be the matching primitive array (e.g. `IntArray`)
 * - object element (or nullable primitive, which boxes to `Array<out E?>`) -> source must be
 *   `kotlin.Array<out E'>` whose element `E'` is compatible with `E`.
 */
private fun KSValueParameter.matchesSourcePropertyType(sourceType: KSType): Boolean {
    val targetElementType = this.type.resolve()
    if (!this.isVararg) return isTypeCompatible(targetElementType, sourceType)

    // A vararg parameter is backed by a NON-NULL array (`Array<out E>` / `IntArray`), so a
    // nullable source array cannot supply its `= this.x` default without a type error. Reject it
    // here; the parameter then becomes a required vararg, which compiles.
    if (sourceType.isMarkedNullable) return false

    val sourceName = sourceType.declaration.qualifiedName?.asString()
    val targetElementName = targetElementType.declaration.qualifiedName?.asString()

    if (!targetElementType.isMarkedNullable) {
        val expectedArrayName = targetElementName?.let { primitiveArrayByElement[it] }
        if (expectedArrayName != null) return sourceName == expectedArrayName
    }

    if (sourceName == "kotlin.Array") {
        val sourceElementType =
            sourceType.arguments
                .firstOrNull()
                ?.type
                ?.resolve()
                ?: return false
        return isTypeCompatible(targetElementType, sourceElementType)
    }

    return false
}

/**
 * Check if two types are compatible for property matching.
 * This includes:
 * - Direct assignability (normal cases)
 * - Both are type parameters with the same name (generic cases)
 */
private fun isTypeCompatible(
    targetType: KSType,
    sourceType: KSType,
): Boolean {
    // Check direct assignability first
    if (targetType.isAssignableFrom(sourceType)) {
        return true
    }

    // Check if both are type parameters with the same name
    val targetDecl = targetType.declaration
    val sourceDecl = sourceType.declaration
    if (targetDecl is KSTypeParameter && sourceDecl is KSTypeParameter) {
        return targetDecl.name.asString() == sourceDecl.name.asString()
    }

    return false
}
