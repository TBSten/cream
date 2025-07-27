package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.UnknownCreamException
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.util.asString
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.isCountMoreThan
import me.tbsten.cream.ksp.util.lines
import me.tbsten.cream.ksp.util.underPackageName
import me.tbsten.cream.ksp.util.visibilityStr
import java.io.BufferedWriter


internal fun BufferedWriter.appendCopyToClassFunction(
    source: KSClassDeclaration,
    targetClass: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
    omitPackages: List<String>,
    options: CreamOptions,
) {
    targetClass.getConstructors().forEach { constructor ->
        val typeParameters =
            getCopyFunctionTypeParameters(
                sourceClass = source,
                targetConstructor = constructor,
            )

        // Generate copy function
        appendKDoc(source, targetClass, constructor, generateSourceAnnotation)
        val funName = copyFunctionName(source, targetClass, options)

        append("${targetClass.visibilityStr} fun ")
        if (typeParameters.isNotEmpty()) {
            append("<")
            append(
                typeParameters.entries.joinToString(", ") { (name, typeParam) ->
                    buildString {
                        append(name)
                        val bound = typeParam.bounds.singleOrNull()?.resolve()?.asString
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
        append(source.fullName)
        if (source.typeParameters.isNotEmpty()) {
            append("<")
            append(
                source.typeParameters
                    .joinToString(", ") { typeParam ->
                        typeParameters.getNameFromSourceClassTypeParameters(typeParam)
                            ?: throw UnknownCreamException(
                                message = lines(
                                    "Can not find type parameter ${typeParam.name.asString()} in ${source.fullName}'s type parameters.",
                                    "  find type parameter: ${typeParam.name.asString()}",
                                    "  ${source.fullName}'s type parameters: ${
                                        source.typeParameters.joinToString(", ") { it.simpleName.asString() }
                                    }",
                                    // TODO solution = report(with = listOf("${targetClass.fullName} and related definitions", "${source.fullName} and related definitions"))
                                ),
                            )
                    }
            )
            append(">")
        }
        append(".")
        append("$funName(")
        appendLine()

        constructor.parameters.forEach { parameter ->
            val paramName = parameter.name!!.asString()
            val paramType = parameter.type.resolve()
                .asString(
                    typeParameterToString = {
                        val typeParameter = it.declaration as KSTypeParameter
                        typeParameters.getNameFromTargetConstructorTypeParameters(typeParameter)
                            ?: typeParameters.getNameFromSourceClassTypeParameters(typeParameter)
                            ?: throw UnknownCreamException(
                                message = lines(
                                    "Can not find type parameter ${typeParameter.name.asString()} in ${source.fullName}'s type parameters.",
                                    "  find type parameter: ${typeParameter.name.asString()}",
                                    "  ${source.fullName}'s type parameters: ${
                                        source.typeParameters.joinToString(", ") { it.simpleName.asString() }
                                    }",
                                    // TODO solution = report(with = listOf("${targetClass.fullName} and related definitions", "${source.fullName} and related definitions"))
                                ),
                            )
                    },
                )
            append("    ${paramName}: $paramType")
            parameter.findMatchedProperty(source)
                ?.let { " = this.${it.simpleName.asString()}" }
                ?.let(::append)
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
                                    "Can not find type parameter ${typeParam.name.asString()} in ${source.fullName}'s type parameters.",
                                    "  find type parameter: ${typeParam.name.asString()}",
                                    "  ${targetClass.fullName}'s type parameters: ${
                                        targetClass.typeParameters.joinToString(", ") { it.simpleName.asString() }
                                    }",
                                ),
                                // TODO solution = report(with = listOf("${targetClass.fullName} and related definitions", "${source.fullName} and related definitions"))
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
                        "$name : ${bound.resolve().asString}"
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

