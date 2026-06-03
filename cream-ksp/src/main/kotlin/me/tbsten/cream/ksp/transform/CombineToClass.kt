package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.DefaultCopyFunctionName
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.UnknownCreamException
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.reportToGithub
import me.tbsten.cream.ksp.util.asString
import me.tbsten.cream.ksp.util.escapeKotlinIdentifier
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.isCountMoreThan
import me.tbsten.cream.ksp.util.lines
import me.tbsten.cream.ksp.util.toModifierString
import me.tbsten.cream.ksp.util.underPackageName
import java.io.BufferedWriter

internal fun BufferedWriter.appendCombineToClassFunction(
    primarySource: KSClassDeclaration,
    otherSources: List<KSClassDeclaration>,
    targetClass: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
    omitPackages: List<String>,
    options: CreamOptions,
    visibility: CopyVisibility = CopyVisibility.INHERIT,
    funNameTemplate: String = DefaultCopyFunctionName,
    logger: KSPLogger? = null,
) {
    val allSources = listOf(primarySource) + otherSources

    targetClass.getConstructors().forEach { constructor ->
        val typeParameters =
            getCopyFunctionTypeParameters(
                sourceClass = primarySource,
                targetConstructor = constructor,
            )

        // Generate copy function
        val funName =
            resolveFunName(
                funNameTemplate = funNameTemplate,
                source = primarySource,
                target = targetClass,
                options = options,
            )

        // A ctor param is REQUIRED when it does NOT receive a `= ...` default in the emitted
        // signature: either no source supplies its value, or it is @Exclude-marked in any source.
        // This mirrors the matched/excluded logic in the parameter loop below.
        val requiredCtorParamNames =
            constructor.parameters
                .filter { parameter ->
                    val matched =
                        allSources.any { source ->
                            parameter.findMatchedProperty(source, generateSourceAnnotation) != null
                        }
                    val excluded =
                        allSources.any { source ->
                            parameter
                                .findMatchedProperty(source, generateSourceAnnotation)
                                ?.let { property -> parameter.isExcludedFromCopy(property, source, generateSourceAnnotation) }
                                ?: false
                        }
                    !matched || excluded
                }.map { it.name!!.asString() }

        appendCombineToClassKDoc(allSources, targetClass, generateSourceAnnotation, funName, requiredCtorParamNames)

        deprecatedAnnotationLine(allSources)?.let { appendLine(it) }

        append("${visibility.toModifierString(targetClass)} fun ")
        if (typeParameters.isNotEmpty()) {
            append("<")
            append(
                typeParameters.entries.joinToString(", ") { (name, typeParam) ->
                    buildString {
                        append(name)
                        val bound =
                            typeParam
                                .bounds
                                .singleOrNull()
                                ?.resolve()
                                ?.asString(omitPackages = omitPackages)
                        if (bound != null && bound != "kotlin.Any?") {
                            append(" : ")
                            append(bound)
                        }
                    }
                },
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
                                message =
                                    lines(
                                        "Can not find type parameter ${typeParam.name.asString()} in ${primarySource.fullName}'s type parameters.",
                                        "  find type parameter: ${typeParam.name.asString()}",
                                        "  ${primarySource.fullName}'s type parameters: ${
                                            primarySource.typeParameters.joinToString(", ") { it.simpleName.asString() }
                                        }",
                                    ),
                                solution =
                                    reportToGithub(
                                        "${targetClass.fullName} and related definitions",
                                        "${primarySource.fullName} and related definitions",
                                    ),
                            )
                    },
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
                        typeParameters.entries
                            .find { (_, param) ->
                                param.source?.name?.asString() == typeParam.name.asString() ||
                                    param.target?.name?.asString() == typeParam.name.asString()
                            }?.key ?: typeParam.name.asString()
                    },
                )
                append(">")
            }

            append(",")
            appendLine()
        }

        // Add parameters for constructor
        constructor.parameters.forEach { parameter ->
            val paramName = parameter.name!!.asString()
            val paramType =
                parameter.type
                    .resolve()
                    .asString(
                        omitPackages = omitPackages,
                        typeParameterToString = {
                            val typeParameter = it.declaration as KSTypeParameter
                            typeParameters.getNameFromTargetConstructorTypeParameters(typeParameter)
                                ?: typeParameters.getNameFromSourceClassTypeParameters(typeParameter)
                                ?: throw UnknownCreamException(
                                    message =
                                        lines(
                                            "Can not find type parameter ${typeParameter.name.asString()} in ${primarySource.fullName}'s type parameters.",
                                            "  find type parameter: ${typeParameter.name.asString()}",
                                            "  ${primarySource.fullName}'s type parameters: ${
                                                primarySource.typeParameters.joinToString(", ") { it.simpleName.asString() }
                                            }",
                                        ),
                                    solution =
                                        reportToGithub(
                                            "${targetClass.fullName} and related definitions",
                                            "${primarySource.fullName} and related definitions",
                                        ),
                                )
                        },
                    )
            val modifiers = if (parameter.isVararg) "vararg " else ""
            append("    $modifiers${paramName.escapeKotlinIdentifier()}: $paramType")

            // Try to find matching property in any of the source classes
            // Use asReversed() to prioritize later source classes when properties overlap
            val matchedSourceAndProperty =
                allSources.asReversed().firstNotNullOfOrNull { source ->
                    parameter
                        .findMatchedProperty(source, generateSourceAnnotation)
                        ?.let { property -> source to property }
                }

            // Suppress the auto-copy default when the matched property is @Exclude-marked in ANY
            // contributing source — not only the "winning" source that supplies the value. With
            // overlapping property names the winner differs per generated function, so checking
            // only the winner would make an explicit @CombineTo.Exclude silently ineffective in
            // some generated functions.
            val excluded =
                allSources.any { source ->
                    parameter
                        .findMatchedProperty(source, generateSourceAnnotation)
                        ?.let { property -> parameter.isExcludedFromCopy(property, source, generateSourceAnnotation) }
                        ?: false
                }

            if (matchedSourceAndProperty != null) {
                val (matchedSource, matchedProperty) = matchedSourceAndProperty
                if (!excluded) {
                    if (matchedSource == primarySource) {
                        append(" = this.${matchedProperty.simpleName.asString().escapeKotlinIdentifier()}")
                    } else {
                        val sourceParamName = matchedSource.simpleName.asString().replaceFirstChar { it.lowercase() }
                        append(" = $sourceParamName.${matchedProperty.simpleName.asString().escapeKotlinIdentifier()}")
                    }
                }
            } else if (logger != null) {
                parameter.warnIfTargetExcludeHasNoEffect(null, generateSourceAnnotation, logger)
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
                                message =
                                    lines(
                                        "Can not find type parameter ${typeParam.name.asString()} in ${primarySource.fullName}'s type parameters.",
                                        "  find type parameter: ${typeParam.name.asString()}",
                                        "  ${targetClass.fullName}'s type parameters: ${
                                            targetClass.typeParameters.joinToString(", ") { it.simpleName.asString() }
                                        }",
                                    ),
                                solution =
                                    reportToGithub(
                                        "${targetClass.fullName} and related definitions",
                                        "${primarySource.fullName} and related definitions",
                                    ),
                            )
                    },
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
                                it,
                            )
                        }
                    }.joinToString(", ") { (name, bound) ->
                        "$name : ${bound.resolve().asString(omitPackages = omitPackages)}"
                    },
            )
        }
        append(" = ${targetClass.fullName}(")
        appendLine()

        constructor.parameters.forEach { param ->
            val escaped = param.name!!.asString().escapeKotlinIdentifier()
            appendLine("    $escaped = $escaped,")
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
    requiredCtorParamNames: List<String>,
) {
    val requiredArgs = requiredCtorParamNames.map { "$it = $it" }
    appendAutoGeneratedFunctionKDoc(
        generateSourceAnnotation = generateSourceAnnotation,
        seeClasses = sources + target,
        autoDescription = {
            appendCombineAutoDescription(sources, target)
        },
        autoExamples = {
            appendExample("Example: Basic", combineExampleBody(sources, funName, extraArgs = requiredArgs))
            appendExample(
                "Example: Override property values",
                combineExampleBody(sources, funName, extraArgs = requiredArgs + "property = value"),
            )
        },
    )
}
