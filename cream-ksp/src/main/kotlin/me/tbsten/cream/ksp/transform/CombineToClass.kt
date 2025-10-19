package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.UnknownCreamException
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.reportToGithub
import me.tbsten.cream.ksp.util.*
import java.io.BufferedWriter


internal fun BufferedWriter.appendCombineToClassFunction(
    primarySource: KSClassDeclaration,
    otherSources: List<KSClassDeclaration>,
    targetClass: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
    omitPackages: List<String>,
    options: CreamOptions,
) {
    val allSources = listOf(primarySource) + otherSources

    targetClass.getConstructors().forEach { constructor ->
        val typeParameters =
            getCopyFunctionTypeParameters(
                sourceClass = primarySource,
                targetConstructor = constructor,
            )

        // Generate copy function
        val funName = copyFunctionName(primarySource, targetClass, options)
        appendCombineToClassKDoc(allSources, targetClass, generateSourceAnnotation, funName.toString())

        append("${targetClass.visibilityStr} fun ")
        if (typeParameters.isNotEmpty()) {
            append("<")
            append(
                typeParameters.entries.joinToString(", ") { (name, typeParam) ->
                    buildString {
                        append(name)
                        val bound = typeParam
                            .bounds
                            .singleOrNull()
                            ?.resolve()
                            ?.asString(omitPackages = omitPackages)
                        if (bound != null && bound != "kotlin.Any?") {
                            append(" : ")
                            append(bound)
                        }
                    }
                }
            )
            append(">")
        }
        append(" ")
        append(primarySource.fullName)
        if (primarySource.typeParameters.isNotEmpty()) {
            append("<")
            append(
                primarySource.typeParameters
                    .joinToString(", ") { typeParam ->
                        typeParameters.getNameFromSourceClassTypeParameters(typeParam)
                            ?: throw UnknownCreamException(
                                message = lines(
                                    "Can not find type parameter ${typeParam.name.asString()} in ${primarySource.fullName}'s type parameters.",
                                    "  find type parameter: ${typeParam.name.asString()}",
                                    "  ${primarySource.fullName}'s type parameters: ${
                                        primarySource.typeParameters.joinToString(", ") { it.simpleName.asString() }
                                    }",
                                ),
                                solution = reportToGithub(
                                    "${targetClass.fullName} and related definitions",
                                    "${primarySource.fullName} and related definitions",
                                ),
                            )
                    }
            )
            append(">")
        }
        append(".")
        append("$funName(")
        appendLine()

        // Add parameters for other source classes
        otherSources.forEach { otherSource ->
            val paramName = otherSource.underPackageName.replaceFirstChar { it.lowercase() }
            append("    $paramName: ${otherSource.fullName}")

            // Add type parameters if the source class has them
            // Map type parameter names to ensure consistency across all sources and target
            if (otherSource.typeParameters.isNotEmpty()) {
                append("<")
                append(
                    otherSource.typeParameters.joinToString(", ") { typeParam ->
                        // Find the mapped type parameter name in the typeParameters map
                        // This ensures all sources and target use the same type parameter names
                        typeParameters.entries.find { (_, param) ->
                            param.source?.name?.asString() == typeParam.name.asString() ||
                                    param.target?.name?.asString() == typeParam.name.asString()
                        }?.key ?: typeParam.name.asString()
                    }
                )
                append(">")
            }

            append(",")
            appendLine()
        }

        // Add parameters for constructor
        constructor.parameters.forEach { parameter ->
            val paramName = parameter.name!!.asString()
            val paramType = parameter.type.resolve()
                .asString(
                    omitPackages = omitPackages,
                    typeParameterToString = {
                        val typeParameter = it.declaration as KSTypeParameter
                        typeParameters.getNameFromTargetConstructorTypeParameters(typeParameter)
                            ?: typeParameters.getNameFromSourceClassTypeParameters(typeParameter)
                            ?: throw UnknownCreamException(
                                message = lines(
                                    "Can not find type parameter ${typeParameter.name.asString()} in ${primarySource.fullName}'s type parameters.",
                                    "  find type parameter: ${typeParameter.name.asString()}",
                                    "  ${primarySource.fullName}'s type parameters: ${
                                        primarySource.typeParameters.joinToString(", ") { it.simpleName.asString() }
                                    }",
                                ),
                                solution = reportToGithub(
                                    "${targetClass.fullName} and related definitions",
                                    "${primarySource.fullName} and related definitions",
                                ),
                            )
                    },
                )
            append("    ${paramName}: $paramType")

            // Try to find matching property in any of the source classes
            // Use asReversed() to prioritize later source classes when properties overlap
            val matchedSourceAndProperty = allSources.asReversed().firstNotNullOfOrNull { source ->
                parameter.findMatchedProperty(source, generateSourceAnnotation)
                    ?.let { property -> source to property }
            }

            if (matchedSourceAndProperty != null) {
                val (matchedSource, matchedProperty) = matchedSourceAndProperty
                if (matchedSource == primarySource) {
                    append(" = this.${matchedProperty.simpleName.asString()}")
                } else {
                    val sourceParamName = matchedSource.simpleName.asString().replaceFirstChar { it.lowercase() }
                    append(" = $sourceParamName.${matchedProperty.simpleName.asString()}")
                }
            }

            append(",\n")
        }
        append(") : ")
        append(targetClass.fullName)
        if (targetClass.typeParameters.isNotEmpty()) {
            append("<")
            append(
                targetClass.typeParameters
                    .joinToString(", ") { typeParam ->
                        typeParameters.getNameFromTargetConstructorTypeParameters(typeParam)
                            ?: throw UnknownCreamException(
                                message = lines(
                                    "Can not find type parameter ${typeParam.name.asString()} in ${primarySource.fullName}'s type parameters.",
                                    "  find type parameter: ${typeParam.name.asString()}",
                                    "  ${targetClass.fullName}'s type parameters: ${
                                        targetClass.typeParameters.joinToString(", ") { it.simpleName.asString() }
                                    }",
                                ),
                                solution = reportToGithub(
                                    "${targetClass.fullName} and related definitions",
                                    "${primarySource.fullName} and related definitions",
                                ),
                            )
                    }
            )
            append(">")
        }
        val whereTypeParameters =
            typeParameters
                .filter { (_, typeParam) -> typeParam.bounds.isCountMoreThan(2, include = true) }
        if (whereTypeParameters.isNotEmpty()) {
            append(" where ")
            append(
                whereTypeParameters
                    .flatMap { (name, typeParam) ->
                        typeParam.bounds.map {
                            Pair(
                                name,
                                it
                            )
                        }
                    }
                    .joinToString(", ") { (name, bound) ->
                        "$name : ${bound.resolve().asString(omitPackages = omitPackages)}"
                    }
            )
        }
        append(" = ${targetClass.fullName}(")
        appendLine()

        constructor.parameters.forEach { param ->
            appendLine("    ${param.name!!.asString()} = ${param.name!!.asString()},")
        }

        appendLine(")")
        appendLine()
    }
}

private fun BufferedWriter.appendCombineToClassKDoc(
    sources: List<KSClassDeclaration>,
    target: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
    funName: String,
) {
    appendAutoGeneratedFunctionKDoc(
        generateSourceAnnotation = generateSourceAnnotation,
        seeClasses = sources + target,
    ) {
        val sourcesStr = sources.joinToString(" + ") { "[${it.underPackageName}]" }
        appendLine("$sourcesStr -> [${target.underPackageName}] copy function.")
        appendLine()

        val primarySource = sources.first()
        val otherSourceParams = sources.drop(1).joinToString(", ") {
            "${it.underPackageName.replaceFirstChar { c -> c.lowercase() }} = ${it.simpleName.asString()}(...)"
        }

        appendExample("Example: Basic", buildString {
            appendLine("val ${primarySource.underPackageName.replaceFirstChar { it.lowercase() }} = ${primarySource.simpleName.asString()}(...)")
            sources.drop(1).forEach { otherSource ->
                appendLine("val ${otherSource.underPackageName.replaceFirstChar { it.lowercase() }} = ${otherSource.simpleName.asString()}(...)")
            }
            append("val target = ${primarySource.underPackageName.replaceFirstChar { it.lowercase() }}.$funName($otherSourceParams)")
        })

        appendExample("Example: Override property values", buildString {
            appendLine("val ${primarySource.underPackageName.replaceFirstChar { it.lowercase() }} = ${primarySource.simpleName.asString()}(...)")
            sources.drop(1).forEach { otherSource ->
                appendLine("val ${otherSource.underPackageName.replaceFirstChar { it.lowercase() }} = ${otherSource.simpleName.asString()}(...)")
            }
            append("val target = ${primarySource.underPackageName.replaceFirstChar { it.lowercase() }}.$funName($otherSourceParams, property = value)")
        })
    }
}
