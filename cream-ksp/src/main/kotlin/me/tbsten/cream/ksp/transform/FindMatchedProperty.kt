package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import me.tbsten.cream.CopyFrom
import me.tbsten.cream.CopyTo
import me.tbsten.cream.ksp.GenerateSourceAnnotation

internal fun KSValueParameter.findMatchedProperty(
    source: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
): KSPropertyDeclaration? {
    val parameterName = this.name?.asString()
    if (parameterName == null) return null

    val propertyMappings = (generateSourceAnnotation as? GenerateSourceAnnotation.CopyMapping)
        ?.propertyMappings
        ?: emptyList()

    findSourcePropertyWithCopyMappingAnnotation(source, parameterName, propertyMappings)
        ?.let { return it }

    findSourcePropertyWithCopyToAnnotation(source, parameterName)
        ?.let { return it }

    findSourcePropertyWithCopyFromAnnotation(source)
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
                    this.type.resolve().isAssignableFrom(it.type.resolve())
        }
}

private fun KSValueParameter.findSourcePropertyWithCopyToAnnotation(
    source: KSClassDeclaration,
    parameterName: String,
): KSPropertyDeclaration? {
    return source.getAllProperties()
        .firstOrNull { sourceProperty ->
            val copyToPropertyAnnotation = sourceProperty
                .getAnnotationsByType(CopyTo.Map::class)
                .firstOrNull()

            if (copyToPropertyAnnotation != null) {
                parameterName in copyToPropertyAnnotation.propertyNames &&
                        this.type.resolve().isAssignableFrom(sourceProperty.type.resolve())
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
                        this.type.resolve().isAssignableFrom(it.type.resolve())
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
                    this.type.resolve().isAssignableFrom(it.type.resolve())
        }
}
