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
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.isCountMoreThan
import me.tbsten.cream.ksp.util.lines
import me.tbsten.cream.ksp.util.toModifierString
import me.tbsten.cream.ksp.util.underPackageName
import java.io.BufferedWriter

internal fun BufferedWriter.appendCopyToClassFunction(
    source: KSClassDeclaration,
    targetClass: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
    omitPackages: List<String>,
    options: CreamOptions,
    visibility: CopyVisibility = CopyVisibility.INHERIT,
    funNameTemplate: String = DefaultCopyFunctionName,
    logger: KSPLogger? = null,
) {
    targetClass.getConstructors().forEach { constructor ->
        val typeParameters =
            getCopyFunctionTypeParameters(
                sourceClass = source,
                targetConstructor = constructor,
            )

        // Generate copy function
        val funName =
            resolveFunName(
                funNameTemplate = funNameTemplate,
                source = source,
                target = targetClass,
                options = options,
            )

        // A parameter is REQUIRED when it does NOT receive a `= this.x` default in the emitted
        // signature, mirroring the default-emission condition below.
        val requiredParamNames =
            constructor.parameters
                .filter { parameter ->
                    val matched = parameter.findMatchedProperty(source, generateSourceAnnotation)
                    matched == null || parameter.isExcludedFromCopy(matched, source, generateSourceAnnotation)
                }.map { it.name!!.asString() }

        appendCopyToClassKDoc(source, targetClass, generateSourceAnnotation, funName, requiredParamNames)

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
        append(source.fullName)
        if (source.typeParameters.isNotEmpty()) {
            append("<")
            append(
                source.typeParameters
                    .joinToString(", ") { typeParam ->
                        typeParameters.getNameFromSourceClassTypeParameters(typeParam)
                            ?: throw UnknownCreamException(
                                message =
                                    lines(
                                        "Can not find type parameter ${typeParam.name.asString()} in ${source.fullName}'s type parameters.",
                                        "  find type parameter: ${typeParam.name.asString()}",
                                        "  ${source.fullName}'s type parameters: ${
                                            source.typeParameters.joinToString(", ") { it.simpleName.asString() }
                                        }",
                                    ),
                                solution =
                                    reportToGithub(
                                        "${targetClass.fullName} and related definitions",
                                        "${source.fullName} and related definitions",
                                    ),
                            )
                    },
            )
            append(">")
        }
        append(".")
        append("$funName(")
        appendLine()

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
                                            "Can not find type parameter ${typeParameter.name.asString()} in ${source.fullName}'s type parameters.",
                                            "  find type parameter: ${typeParameter.name.asString()}",
                                            "  ${source.fullName}'s type parameters: ${
                                                source.typeParameters.joinToString(", ") { it.simpleName.asString() }
                                            }",
                                        ),
                                    solution =
                                        reportToGithub(
                                            "${targetClass.fullName} and related definitions",
                                            "${source.fullName} and related definitions",
                                        ),
                                )
                        },
                    )
            val modifiers = if (parameter.isVararg) "vararg " else ""
            append("    $modifiers$paramName: $paramType")

            val matchedProperty = parameter.findMatchedProperty(source, generateSourceAnnotation)
            if (logger != null) {
                parameter.warnIfTargetExcludeHasNoEffect(matchedProperty, generateSourceAnnotation, logger)
            }
            if (matchedProperty != null &&
                !parameter.isExcludedFromCopy(matchedProperty, source, generateSourceAnnotation)
            ) {
                append(" = this.${matchedProperty.simpleName.asString()}")
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
                                        "Can not find type parameter ${typeParam.name.asString()} in ${source.fullName}'s type parameters.",
                                        "  find type parameter: ${typeParam.name.asString()}",
                                        "  ${targetClass.fullName}'s type parameters: ${
                                            targetClass.typeParameters.joinToString(", ") { it.simpleName.asString() }
                                        }",
                                    ),
                                solution =
                                    reportToGithub(
                                        "${targetClass.fullName} and related definitions",
                                        "${source.fullName} and related definitions",
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
            appendLine("    ${param.name!!.asString()} = ${param.name!!.asString()},")
        }

        appendLine(")")
        appendLine()
    }
}

private fun BufferedWriter.appendCopyToClassKDoc(
    source: KSClassDeclaration,
    target: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
    funName: String,
    requiredParamNames: List<String>,
) {
    val requiredArgs = requiredParamNames.joinToString(", ") { "$it = $it" }
    val basicCall = if (requiredArgs.isEmpty()) "" else requiredArgs
    val overrideCall =
        if (requiredArgs.isEmpty()) "property = value" else "$requiredArgs, property = value"
    appendAutoGeneratedFunctionKDoc(
        generateSourceAnnotation = generateSourceAnnotation,
        seeClasses = listOf(source, target),
        autoDescription = {
            appendLine("${source.underPackageName} -> ${target.underPackageName} copy function.")
        },
        autoExamples = {
            appendExample(
                "Example: Basic",
                """
                val source = ${source.simpleName.asString()}(...)
                val target = source.$funName($basicCall)
                """.trimIndent(),
            )

            appendExample(
                "Example: Override property values",
                """
                val source = ${source.simpleName.asString()}(...)
                val target = source.$funName($overrideCall)
                """.trimIndent(),
            )
        },
    )
}
