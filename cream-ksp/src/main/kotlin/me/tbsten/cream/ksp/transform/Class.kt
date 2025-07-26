package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import me.tbsten.cream.CopyFrom
import me.tbsten.cream.CopyTo
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.util.asString
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.underPackageName
import me.tbsten.cream.ksp.util.visibilityStr
import java.io.BufferedWriter


internal fun BufferedWriter.appendCopyToClassFunction(
    source: KSClassDeclaration,
    targetClass: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
    options: CreamOptions,
) {
    targetClass.getConstructors().forEach { constructor ->
        appendKDoc(source, targetClass, constructor, generateSourceAnnotation)
        val funName = copyFunctionName(source, targetClass, options)
        appendLine(
            "${targetClass.visibilityStr} fun ${source.fullName}.$funName("
        )
        constructor.parameters.forEach { parameter ->
            append("    ")
            append("${parameter.name!!.asString()}: ${parameter.type.resolve().asString}")
            parameter.findMatchedProperty(source)?.let {
                append(" = this.${it.simpleName.asString()}")
            }
            append(",\n")
        }
        appendLine(") : ${targetClass.fullName} = ${targetClass.fullName}(")
        constructor.parameters.forEach { param ->
            appendLine("    ${param.name!!.asString()} = ${param.name!!.asString()},")
        }
        appendLine(")")
        appendLine()
    }
}

private fun BufferedWriter.appendKDoc(
    source: KSClassDeclaration,
    target: KSClassDeclaration,
    constructor: KSFunctionDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
) {
    appendLine("/**")
    appendLine(" * (${autoGenerateKDoc(generateSourceAnnotation)})")
    appendLine(" * ")
    appendLine(" * [${source.underPackageName}] -> [${target.underPackageName}] copy function.")
    appendLine(" * ")
    appendLine(" * @see ${source.underPackageName}")
    appendLine(" * @see ${target.underPackageName}")
    appendLine(" */")
}

@OptIn(KspExperimental::class)
private fun KSValueParameter.findMatchedProperty(
    source: KSClassDeclaration,
): KSPropertyDeclaration? {
    val parameterName = this.name?.asString()
    if (parameterName == null) return null

    // Try @CopyTo.Map annotation matching first
    findSourcePropertyWithCopyToAnnotation(source, parameterName)
        ?.let { return it }

    // Try @CopyFrom.Map annotation matching
    findSourcePropertyWithCopyFromAnnotation(source, parameterName)
        ?.let { return it }

    // Fall back to original name-based matching
    return findSourcePropertyByName(source, parameterName)
}

@OptIn(KspExperimental::class)
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

@OptIn(KspExperimental::class)
private fun KSValueParameter.findSourcePropertyWithCopyFromAnnotation(
    source: KSClassDeclaration,
    parameterName: String,
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
