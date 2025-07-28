package me.tbsten.cream.ksp.util


import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.Modifier
import me.tbsten.cream.ksp.UnknownCreamException
import kotlin.reflect.KClass

internal fun KSClassDeclaration.isSealed(): Boolean =
    modifiers.contains(Modifier.SEALED)

internal val KSDeclaration.fullName: String
    get() = qualifiedName?.asString()
        ?: throw UnknownCreamException("qualifiedName is null")

internal val KSClassDeclaration.underPackageName: String
    get() = fullName.replace("${packageName.asString()}.", "")

internal val KClass<*>.fullName: String
    get() = qualifiedName
        ?: throw UnknownCreamException("qualifiedName is null")
