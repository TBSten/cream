package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import me.tbsten.cream.ksp.UnknownCreamException
import me.tbsten.cream.ksp.reportToGithub
import me.tbsten.cream.ksp.util.escapeKotlinIdentifier
import me.tbsten.cream.ksp.util.isCountMoreThan
import me.tbsten.cream.ksp.util.ksp.asString
import me.tbsten.cream.ksp.util.lines
import java.io.BufferedWriter

/**
 * Appends the generic type parameter list `<T : Bound, ...>` for a generated copy/combine function,
 * based on [typeParameters]. Each parameter is rendered with its single inline bound when present
 * (but not `kotlin.Any?`). Emits nothing when [typeParameters] is empty.
 */
internal fun BufferedWriter.appendTypeParameterList(
    typeParameters: CopyFunctionTypeParameters,
    omitPackages: List<String>,
) {
    if (typeParameters.isEmpty()) return
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

/**
 * Appends the source class name with its type arguments resolved through [typeParameters],
 * e.g. `com.example.Source<T1, T2>`. If [sourceClass] has no type parameters, only the
 * class name is appended. Throws [UnknownCreamException] if a type parameter cannot be resolved.
 */
internal fun BufferedWriter.appendSourceClassWithTypeArgs(
    sourceClass: KSClassDeclaration,
    targetClass: KSClassDeclaration,
    typeParameters: CopyFunctionTypeParameters,
) {
    append(sourceClass.fullName)
    if (sourceClass.typeParameters.isEmpty()) return
    append("<")
    append(
        sourceClass.typeParameters
            .joinToString(", ") { typeParam ->
                typeParameters.getNameFromSourceClassTypeParameters(typeParam)
                    ?: throw UnknownCreamException(
                        message =
                            lines(
                                "Can not find type parameter ${typeParam.name.asString()} in ${sourceClass.fullName}'s type parameters.",
                                "  find type parameter: ${typeParam.name.asString()}",
                                "  ${sourceClass.fullName}'s type parameters: ${
                                    sourceClass.typeParameters.joinToString(", ") { it.simpleName.asString() }
                                }",
                            ),
                        solution =
                            reportToGithub(
                                "${targetClass.fullName} and related definitions",
                                "${sourceClass.fullName} and related definitions",
                            ),
                    )
            },
    )
    append(">")
}

/**
 * Appends the target class name with its type arguments resolved through [typeParameters],
 * e.g. `com.example.Target<T1, T2>`. If [targetClass] has no type parameters, only the
 * class name is appended. Throws [UnknownCreamException] if a type parameter cannot be resolved.
 */
internal fun BufferedWriter.appendTargetClassWithTypeArgs(
    sourceClass: KSClassDeclaration,
    targetClass: KSClassDeclaration,
    typeParameters: CopyFunctionTypeParameters,
) {
    append(targetClass.fullName)
    if (targetClass.typeParameters.isEmpty()) return
    append("<")
    append(
        targetClass.typeParameters
            .joinToString(", ") { typeParam ->
                typeParameters.getNameFromTargetConstructorTypeParameters(typeParam)
                    ?: throw UnknownCreamException(
                        message =
                            lines(
                                "Can not find type parameter ${typeParam.name.asString()} in ${sourceClass.fullName}'s type parameters.",
                                "  find type parameter: ${typeParam.name.asString()}",
                                "  ${targetClass.fullName}'s type parameters: ${
                                    targetClass.typeParameters.joinToString(", ") { it.simpleName.asString() }
                                }",
                            ),
                        solution =
                            reportToGithub(
                                "${targetClass.fullName} and related definitions",
                                "${sourceClass.fullName} and related definitions",
                            ),
                    )
            },
    )
    append(">")
}

/**
 * Resolves the string representation of a type parameter appearing in a constructor parameter's
 * type, using [typeParameters] to map to the generated function's parameter names.
 * Throws [UnknownCreamException] if it cannot be resolved.
 */
internal fun resolveConstructorParamTypeParameter(
    typeParameter: KSTypeParameter,
    sourceClass: KSClassDeclaration,
    targetClass: KSClassDeclaration,
    typeParameters: CopyFunctionTypeParameters,
): String =
    typeParameters.getNameFromTargetConstructorTypeParameters(typeParameter)
        ?: typeParameters.getNameFromSourceClassTypeParameters(typeParameter)
        ?: throw UnknownCreamException(
            message =
                lines(
                    "Can not find type parameter ${typeParameter.name.asString()} in ${sourceClass.fullName}'s type parameters.",
                    "  find type parameter: ${typeParameter.name.asString()}",
                    "  ${sourceClass.fullName}'s type parameters: ${
                        sourceClass.typeParameters.joinToString(", ") { it.simpleName.asString() }
                    }",
                ),
            solution =
                reportToGithub(
                    "${targetClass.fullName} and related definitions",
                    "${sourceClass.fullName} and related definitions",
                ),
        )

/**
 * Appends the ` where T : A, T : B` clause for type parameters that carry more than one upper
 * bound. Returns without output when no parameter needs it.
 */
internal fun BufferedWriter.appendWhereClause(
    typeParameters: CopyFunctionTypeParameters,
    omitPackages: List<String>,
) {
    val whereTypeParameters =
        typeParameters
            .filter { (_, typeParam) -> typeParam.bounds.isCountMoreThan(2, include = true) }
    if (whereTypeParameters.isEmpty()) return
    append(" where ")
    append(
        whereTypeParameters
            .flatMap { (name, typeParam) ->
                typeParam.bounds.map {
                    Pair(name, it)
                }
            }.joinToString(", ") { (name, bound) ->
                "$name : ${bound.resolve().asString(omitPackages = omitPackages)}"
            },
    )
}

/**
 * Appends the `= TargetClass(\n    p1 = p1,\n    p2 = p2,\n)` constructor call body.
 * Used at the end of both copy and combine generated function bodies.
 */
internal fun BufferedWriter.appendConstructorCallBody(
    targetClass: KSClassDeclaration,
    constructor: KSFunctionDeclaration,
) {
    append(" = ${targetClass.fullName}(")
    appendLine()
    constructor.parameters.forEach { param ->
        val escaped = param.name!!.asString().escapeKotlinIdentifier()
        appendLine("    $escaped = $escaped,")
    }
    appendLine(")")
    appendLine()
}
