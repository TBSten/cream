package me.tbsten.cream.ksp.util.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier

internal fun KSClassDeclaration.isSealed(): Boolean = modifiers.contains(Modifier.SEALED)
