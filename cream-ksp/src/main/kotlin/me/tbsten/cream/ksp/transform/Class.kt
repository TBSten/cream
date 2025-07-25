package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.util.asString
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.underPackageName
import me.tbsten.cream.ksp.util.visibilityStr
import java.io.BufferedWriter


internal fun BufferedWriter.appendCopyToClassFunction(
    source: KSClassDeclaration,
    targetClass: KSClassDeclaration,
    options: CreamOptions,
) {
    targetClass.getConstructors().forEach { constructor ->
        appendKDoc(source, targetClass, constructor)
        val funName = copyFunctionName(source, targetClass, options)
        appendLine(
            "${targetClass.visibilityStr} fun ${source.fullName}.$funName("
        )
        constructor.parameters.forEach { parameter ->
            append("    ")
            append("${parameter.name!!.asString()}: ${parameter.type.resolve().asString}")
            parameter.findMatchedProperty(source, targetClass)?.let {
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
) {
    appendLine("/**")
    appendLine(" * (auto generated)")
    appendLine(" * [${source.underPackageName}] -> [${target.underPackageName}] copy function.")
    appendLine(" * ")
    appendLine(" * @see ${source.underPackageName}")
    appendLine(" * @see ${target.underPackageName}")
    appendLine(" */")
}

private fun KSValueParameter.findMatchedProperty(
    source: KSClassDeclaration,
    targetClass: KSClassDeclaration,
): com.google.devtools.ksp.symbol.KSPropertyDeclaration? {
    // Find the corresponding property in the target class
    val targetProperty = targetClass.getAllProperties()
        .firstOrNull { it.simpleName.asString() == this.name?.asString() }
    
    // Check if the target property has @CopyFrom.Property annotation
    val copyFromPropertyAnnotation = targetProperty?.annotations
        ?.firstOrNull { 
            it.annotationType.resolve().declaration.qualifiedName?.asString() == "me.tbsten.cream.CopyFrom.Property"
        }
    
    if (copyFromPropertyAnnotation != null) {
        val sourcePropertyName = copyFromPropertyAnnotation.arguments
            .firstOrNull { it.name?.asString() == "value" }
            ?.value as? String
        
        if (sourcePropertyName != null) {
            return source.getAllProperties()
                .firstOrNull { 
                    it.simpleName.asString() == sourcePropertyName &&
                    this.type.resolve().isAssignableFrom(it.type.resolve())
                }
        }
    }
    
    // Fall back to original name-based matching
    return source
        .getAllProperties()
        .firstOrNull {
            it.simpleName == this.name &&
                    this.type.resolve().isAssignableFrom(it.type.resolve())
        }
}
