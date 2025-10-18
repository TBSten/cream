package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.underPackageName
import me.tbsten.cream.ksp.util.visibilityStr
import java.io.BufferedWriter


internal fun BufferedWriter.appendCopyToObjectFunction(
    source: KSClassDeclaration,
    targetObject: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
    options: CreamOptions,
) {
    appendKDoc(source, targetObject, generateSourceAnnotation)
    appendLine(
        "${targetObject.visibilityStr} fun ${source.fullName}.${
            copyFunctionName(
                source.toClassDeclarationInfo(),
                targetObject.toClassDeclarationInfo(),
                options,
            )
        }() = ${targetObject.fullName}"
    )
}

internal fun BufferedWriter.appendCombineToObjectFunction(
    primarySource: KSClassDeclaration,
    otherSources: List<KSClassDeclaration>,
    targetObject: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
    options: CreamOptions,
) {
    val allSources = listOf(primarySource) + otherSources
    appendCombineToKDoc(allSources, targetObject, generateSourceAnnotation)
    appendLine(
        "${targetObject.visibilityStr} fun ${primarySource.fullName}.${
            copyFunctionName(
                primarySource.toClassDeclarationInfo(),
                targetObject.toClassDeclarationInfo(),
                options,
            )
        }(${
            otherSources.joinToString(", ") { otherSource ->
                "${otherSource.underPackageName.replaceFirstChar { it.lowercase() }}: ${otherSource.fullName}"
            }
        }) = ${targetObject.fullName}"
    )
}

private fun BufferedWriter.appendCombineToKDoc(
    sources: List<KSClassDeclaration>,
    target: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
) {
    appendLine("/**")
    appendLine(" * (${autoGenerateKDoc(generateSourceAnnotation)})")
    appendLine(" * ")
    val sourcesStr = sources.joinToString(" + ") { "[${it.underPackageName}]" }
    appendLine(" * $sourcesStr -> [${target.underPackageName}] copy function.")
    appendLine(" * ")
    sources.forEach { source ->
        appendLine(" * @see ${source.underPackageName}")
    }
    appendLine(" * @see ${target.underPackageName}")
    appendLine(" */")
}

private fun BufferedWriter.appendKDoc(
    source: KSClassDeclaration,
    target: KSClassDeclaration,
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
