package me.tbsten.cream.ksp.transform


import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.util.asString
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.underPackageName
import me.tbsten.cream.ksp.util.visibilityStr
import java.io.BufferedWriter

internal fun BufferedWriter.appendMutableCopyFunction(
    source: KSClassDeclaration,
    target: KSClassDeclaration,
    options: CreamOptions,
    omitPackages: List<String>,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
) {
    val funName = copyFunctionName(source, target, options)
    val scopeClassName = "CopyTo${target.simpleName.asString()}Scope"

    // Generate the mutable copy function
    appendMutableCopyFunctionKDoc(source, target, generateSourceAnnotation)
    appendLine("${target.visibilityStr} fun ${source.fullName}.$funName(")
    appendLine("    ${target.simpleName.asString().lowercase()}: ${target.fullName},")
    appendLine("    block: $scopeClassName.() -> Unit = {},")
    appendLine(") {")

    // Copy matching properties from source to target
    val sourceProperties = source.getAllProperties().toList()
    val targetProperties = target.getAllProperties().filter { it.isMutable }.toList()

    targetProperties.forEach { targetProp ->
        val matchingSourceProp = sourceProperties.find { sourceProp ->
            sourceProp.simpleName.asString() == targetProp.simpleName.asString() &&
            sourceProp.type.resolve().asString(omitPackages) == targetProp.type.resolve().asString(omitPackages)
        }
        
        if (matchingSourceProp != null) {
            appendLine("    ${target.simpleName.asString().lowercase()}.${targetProp.simpleName.asString()} = this.${matchingSourceProp.simpleName.asString()}")
        }
    }

    // Apply the customization block
    appendLine("    $scopeClassName(this, ${target.simpleName.asString().lowercase()}).block()")
    appendLine("}")
    appendLine()

    // Generate the scope class
    appendMutableCopyScopeClass(source, target, scopeClassName, omitPackages)
}

private fun BufferedWriter.appendMutableCopyScopeClass(
    source: KSClassDeclaration,
    target: KSClassDeclaration,
    scopeClassName: String,
    omitPackages: List<String>,
) {
    appendLine("/**")
    appendLine(" * Scope class for customizing mutable copy from ${source.underPackageName} to ${target.underPackageName}.")
    appendLine(" */")
    appendLine("class $scopeClassName(")
    appendLine("    val ${source.simpleName.asString().lowercase()}: ${source.fullName},")
    appendLine("    val ${target.simpleName.asString().lowercase()}: ${target.fullName},")
    appendLine(") {")

    // Generate properties for all target mutable properties
    val targetProperties = target.getAllProperties().filter { it.isMutable }.toList()
    
    targetProperties.forEach { targetProp ->
        val propName = targetProp.simpleName.asString()
        val propType = targetProp.type.resolve().asString(omitPackages)
        
        appendLine("    var $propName: $propType")
        appendLine("        get() = ${target.simpleName.asString().lowercase()}.$propName")
        appendLine("        set(value) { ${target.simpleName.asString().lowercase()}.$propName = value }")
        appendLine()
    }

    appendLine("}")
    appendLine()
}

private fun BufferedWriter.appendMutableCopyFunctionKDoc(
    source: KSClassDeclaration,
    target: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
) {
    appendLine("/**")
    appendLine(" * (${autoGenerateKDoc(generateSourceAnnotation)})")
    appendLine(" * ")
    appendLine(" * [${source.underPackageName}] -> [${target.underPackageName}] mutable copy function.")
    appendLine(" * ")
    appendLine(" * Copies properties from source to target and provides a scope for customization.")
    appendLine(" * ")
    appendLine(" * @param ${target.simpleName.asString().lowercase()} The target object to copy properties to")
    appendLine(" * @param block Lambda with receiver for customizing the copied values")
    appendLine(" * ")
    appendLine(" * @see ${source.underPackageName}")
    appendLine(" * @see ${target.underPackageName}")
    appendLine(" */")
}
