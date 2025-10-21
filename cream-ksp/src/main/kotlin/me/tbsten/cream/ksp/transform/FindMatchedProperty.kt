package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSValueParameter
import me.tbsten.cream.CombineFrom
import me.tbsten.cream.CombineTo
import me.tbsten.cream.CopyFrom
import me.tbsten.cream.CopyTo
import me.tbsten.cream.CopyToChildren
import me.tbsten.cream.ksp.GenerateSourceAnnotation

internal fun KSValueParameter.findMatchedProperty(
    source: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
): KSPropertyDeclaration? {
    val parameterName =
        this.name?.asString()
            ?: return null

    // Check if this parameter is excluded via @CombineFrom.Exclude
    if (this.hasExcludeAnnotation<me.tbsten.cream.CombineFrom.Exclude>()) {
        return null
    }

    // Check if this parameter is excluded via @CopyFrom.Exclude
    if (this.hasExcludeAnnotation<me.tbsten.cream.CopyFrom.Exclude>()) {
        return null
    }

    // Check if this property is excluded in CopyMapping or CombineMapping
    when (generateSourceAnnotation) {
        is GenerateSourceAnnotation.CopyMapping -> {
            if (parameterName in generateSourceAnnotation.excludes) {
                return null
            }
        }
        is GenerateSourceAnnotation.CombineMapping -> {
            if (parameterName in generateSourceAnnotation.excludes) {
                return null
            }
        }
        else -> {}
    }

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
        .getAllProperties()
        .firstOrNull {
            it.simpleName.asString() == sourcePropertyName &&
                isTypeCompatible(this.type.resolve(), it.type.resolve())
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
        .getAllProperties()
        .firstOrNull {
            it.simpleName.asString() == sourcePropertyName &&
                isTypeCompatible(this.type.resolve(), it.type.resolve())
        }
}

private fun KSValueParameter.findSourcePropertyWithCopyToAnnotation(
    source: KSClassDeclaration,
    parameterName: String,
): KSPropertyDeclaration? =
    source
        .getAllProperties()
        .firstOrNull { sourceProperty ->
            // Check if property is excluded
            if (sourceProperty.hasExcludeAnnotation<CopyTo.Exclude>(source)) {
                return@firstOrNull false
            }

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
                    isTypeCompatible(this.type.resolve(), sourceProperty.type.resolve())
            } else {
                false
            }
        }

private fun KSValueParameter.findSourcePropertyWithCombineToAnnotation(
    source: KSClassDeclaration,
    parameterName: String,
): KSPropertyDeclaration? =
    source
        .getAllProperties()
        .firstOrNull { sourceProperty ->
            // Check if property is excluded
            if (sourceProperty.hasExcludeAnnotation<CombineTo.Exclude>(source)) {
                return@firstOrNull false
            }

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
                    isTypeCompatible(this.type.resolve(), sourceProperty.type.resolve())
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
            .getAllProperties()
            .firstOrNull {
                it.simpleName.asString() in sourcePropertyNames &&
                    isTypeCompatible(this.type.resolve(), it.type.resolve())
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
        .getAllProperties()
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
                    isTypeCompatible(this.type.resolve(), sourceProperty.type.resolve())
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
            .getAllProperties()
            .firstOrNull {
                it.simpleName.asString() in sourcePropertyNames &&
                    isTypeCompatible(this.type.resolve(), it.type.resolve())
            }
    }

    return null
}

private fun KSValueParameter.findSourcePropertyByName(
    source: KSClassDeclaration,
    parameterName: String,
): KSPropertyDeclaration? =
    source
        .getAllProperties()
        .firstOrNull { sourceProperty ->
            // Check if property is excluded via @CopyTo.Exclude
            if (sourceProperty.hasExcludeAnnotation<CopyTo.Exclude>(source)) {
                return@firstOrNull false
            }

            // Check if property is excluded via @CombineTo.Exclude
            if (sourceProperty.hasExcludeAnnotation<CombineTo.Exclude>(source)) {
                return@firstOrNull false
            }

            // Check if property is excluded via @CopyToChildren.Exclude
            if (sourceProperty.hasExcludeAnnotation<CopyToChildren.Exclude>(source)) {
                return@firstOrNull false
            }

            sourceProperty.simpleName.asString() == parameterName &&
                isTypeCompatible(this.type.resolve(), sourceProperty.type.resolve())
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

/**
 * Check if a value parameter or property has an Exclude annotation
 */
private inline fun <reified T : Annotation> KSValueParameter.hasExcludeAnnotation() = this.getAnnotationsByType(T::class).any()

/**
 * Check if a property has an Exclude annotation (checks both property and constructor parameter)
 */
private inline fun <reified T : Annotation> KSPropertyDeclaration.hasExcludeAnnotation(source: KSClassDeclaration): Boolean {
    // Check property itself
    if (this.getAnnotationsByType(T::class).any()) {
        return true
    }

    // Check corresponding constructor parameter
    source.primaryConstructor
        ?.parameters
        ?.firstOrNull { it.name?.asString() == this.simpleName.asString() }
        ?.let {
            if (it.getAnnotationsByType(T::class).any()) {
                return true
            }
        }

    return false
}
