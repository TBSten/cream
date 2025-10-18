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
import me.tbsten.cream.ksp.GenerateSourceAnnotation

internal fun KSValueParameter.findMatchedProperty(
    source: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
): KSPropertyDeclaration? {
    val parameterName = this.name?.asString()
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

    val propertyMappings = (generateSourceAnnotation as? GenerateSourceAnnotation.CopyMapping)
        ?.propertyMappings
        ?: emptyList()

    findSourcePropertyWithCopyMappingAnnotation(source, parameterName, propertyMappings)
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
    val sourcePropertyName = propertyMappings.firstOrNull { (_, target) ->
        target == parameterName
    }?.first ?: return null

    return source.getAllProperties()
        .firstOrNull {
            it.simpleName.asString() == sourcePropertyName &&
                    isTypeCompatible(this.type.resolve(), it.type.resolve())
        }
}

private fun KSValueParameter.findSourcePropertyWithCopyToAnnotation(
    source: KSClassDeclaration,
    parameterName: String,
): KSPropertyDeclaration? {
    return source.getAllProperties()
        .firstOrNull { sourceProperty ->
            // First try to get annotation from property itself
            val propertyAnnotation = sourceProperty
                .getAnnotationsByType(CopyTo.Map::class)
                .firstOrNull()

            // If not found on property, try to get it from the corresponding constructor parameter
            val constructorParamAnnotation = if (propertyAnnotation == null) {
                source.primaryConstructor?.parameters
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
}

private fun KSValueParameter.findSourcePropertyWithCombineToAnnotation(
    source: KSClassDeclaration,
    parameterName: String,
): KSPropertyDeclaration? {
    return source.getAllProperties()
        .firstOrNull { sourceProperty ->
            // First try to get annotation from property itself
            val propertyAnnotation = sourceProperty
                .getAnnotationsByType(CombineTo.Map::class)
                .firstOrNull()

            // If not found on property, try to get it from the corresponding constructor parameter
            val constructorParamAnnotation = if (propertyAnnotation == null) {
                source.primaryConstructor?.parameters
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
}

/**
 * Find source property using @CombineFrom.Map annotation on target parameter.
 * Target parameter specifies which source property to use.
 * Example: @CombineFrom.Map("sourcePropertyB") val targetProperty: String
 */
private fun KSValueParameter.findSourcePropertyWithCombineFromAnnotationOnTarget(
    source: KSClassDeclaration,
): KSPropertyDeclaration? {
    val combineFromPropertyAnnotation = this
        .getAnnotationsByType(CombineFrom.Map::class)
        .firstOrNull()

    if (combineFromPropertyAnnotation != null) {
        val sourcePropertyNames = combineFromPropertyAnnotation.propertyNames

        return source.getAllProperties()
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
): KSPropertyDeclaration? {
    return source.getAllProperties()
        .firstOrNull { sourceProperty ->
            // First try to get annotation from property itself
            val propertyAnnotation = sourceProperty
                .getAnnotationsByType(CombineFrom.Map::class)
                .firstOrNull()

            // If not found on property, try to get it from the corresponding constructor parameter
            val constructorParamAnnotation = if (propertyAnnotation == null) {
                source.primaryConstructor?.parameters
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
}

private fun KSValueParameter.findSourcePropertyWithCopyFromAnnotation(
    source: KSClassDeclaration,
): KSPropertyDeclaration? {
    val copyFromPropertyAnnotation = this
        .getAnnotationsByType(CopyFrom.Map::class)
        .firstOrNull()

    if (copyFromPropertyAnnotation != null) {
        val sourcePropertyNames = copyFromPropertyAnnotation.propertyNames

        return source.getAllProperties()
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
): KSPropertyDeclaration? {
    return source
        .getAllProperties()
        .firstOrNull {
            it.simpleName.asString() == parameterName &&
                    isTypeCompatible(this.type.resolve(), it.type.resolve())
        }
}

/**
 * Check if two types are compatible for property matching.
 * This includes:
 * - Direct assignability (normal cases)
 * - Both are type parameters with the same name (generic cases)
 */
private fun isTypeCompatible(targetType: KSType, sourceType: KSType): Boolean {
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
