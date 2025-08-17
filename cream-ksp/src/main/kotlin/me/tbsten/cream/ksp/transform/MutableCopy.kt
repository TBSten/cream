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
    val funName = getMutableCopyFunctionName(source, target, options)

    // Generate the mutable copy function
    appendMutableCopyFunctionKDoc(source, target, generateSourceAnnotation)
    appendLine("${target.visibilityStr} fun ${source.fullName}.$funName(")
    
    // Add mutableTarget parameter
    appendLine("    mutableTarget: ${target.fullName},")
    
    // Add parameters for each target property (both mutable and inherited)
    val sourceProperties = source.getAllProperties().toList()
    val targetProperties = target.getAllProperties().toList()

    targetProperties.forEach { targetProp ->
        val propName = targetProp.simpleName.asString()
        val propType = targetProp.type.resolve().asString(omitPackages)
        val matchingSourceProp = sourceProperties.find { sourceProp ->
            sourceProp.simpleName.asString() == targetProp.simpleName.asString() &&
            sourceProp.type.resolve().asString(omitPackages) == targetProp.type.resolve().asString(omitPackages)
        }
        
        if (matchingSourceProp != null) {
            // Property has matching source property, use it as default value
            appendLine("    $propName: $propType = this.${matchingSourceProp.simpleName.asString()},")
        } else {
            // Property has no matching source property, require explicit value
            appendLine("    $propName: $propType,")
        }
    }
    
    append("): ${target.fullName} {")
    appendLine()

    // Assign all parameters to target properties
    targetProperties.forEach { targetProp ->
        val propName = targetProp.simpleName.asString()
        // Only assign to mutable properties
        if (targetProp.isMutable) {
            appendLine("    mutableTarget.$propName = $propName")
        }
    }

    appendLine("    return mutableTarget")
    appendLine("}")
    appendLine()
}

private fun getMutableCopyFunctionName(
    source: KSClassDeclaration,
    target: KSClassDeclaration,
    options: CreamOptions,
): String {
    // Use the provided mutableCopyFunNamePrefix
    val prefix = options.mutableCopyFunNamePrefix
    
    // Get the target name part using a helper function
    val targetSuffix = getCopyFunctionTargetSuffix(source, target, options)
    
    return "$prefix$targetSuffix"
}

// Helper function to extract the target suffix for the copy function name
private fun getCopyFunctionTargetSuffix(
    source: KSClassDeclaration,
    target: KSClassDeclaration,
    options: CreamOptions,
): String {
    // Always use "copyTo" as the prefix to extract the suffix
    val fullCopyFunctionName = copyFunctionName(source, target, options.copy(copyFunNamePrefix = "copyTo"))
    return if (fullCopyFunctionName.startsWith("copyTo")) {
        fullCopyFunctionName.removePrefix("copyTo")
    } else {
        fullCopyFunctionName
    }
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
    appendLine(" * Copies properties from source to target with explicit parameter values.")
    appendLine(" * Properties with matching names and types use source values as defaults.")
    appendLine(" * ")
    appendLine(" * @param mutableTarget The target object to copy properties to")
    target.getAllProperties().forEach { targetProp ->
        val propName = targetProp.simpleName.asString()
        appendLine(" * @param $propName Value for target.$propName property")
    }
    appendLine(" * @return The modified target object")
    appendLine(" * ")
    appendLine(" * @see ${source.underPackageName}")
    appendLine(" * @see ${target.underPackageName}")
    appendLine(" */")
}