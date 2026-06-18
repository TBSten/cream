package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSDeclaration
import me.tbsten.cream.ksp.UnknownCreamException
import kotlin.reflect.KClass

internal val KSDeclaration.fullName: String
    get() =
        qualifiedName?.asString()
            ?: throw UnknownCreamException("qualifiedName is null")

internal val KSDeclaration.underPackageName: String
    get() = fullName.replace("${packageName.asString()}.", "")

internal val KClass<*>.fullName: String
    get() =
        qualifiedName
            ?: throw UnknownCreamException("qualifiedName is null")
