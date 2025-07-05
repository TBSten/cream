package me.tbsten.cream.ksp.util


import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.Modifier
import me.tbsten.cream.ksp.UnknownCreamException

internal fun KSClassDeclaration.isSealed(): Boolean =
    modifiers.contains(Modifier.SEALED)

internal val KSDeclaration.fullName: String
    get() = qualifiedName?.asString()
        ?: throw UnknownCreamException("qualifiedName is null")

internal val KSClassDeclaration.name: String
    get() = fullName.replace("${packageName.asString()}.", "")
