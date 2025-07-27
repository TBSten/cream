package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import me.tbsten.cream.CopyFrom
import me.tbsten.cream.CopyTo

internal fun KSValueParameter.findMatchedProperty(
    source: KSClassDeclaration,
): KSPropertyDeclaration? {
    val parameterName = this.name?.asString()
    if (parameterName == null) return null

    // Try @CopyTo.Map annotation matching first
    findSourcePropertyWithCopyToAnnotation(source, parameterName)
        ?.let { return it }

    // Try @CopyFrom.Map annotation matching
    findSourcePropertyWithCopyFromAnnotation(source)
        ?.let { return it }

    // Fall back to original name-based matching
    return findSourcePropertyByName(source, parameterName)
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
                copyToPropertyAnnotation.value == parameterName &&
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
        val sourcePropertyName = copyFromPropertyAnnotation.value

        return source.getAllProperties()
            .firstOrNull {
                it.simpleName.asString() == sourcePropertyName &&
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
