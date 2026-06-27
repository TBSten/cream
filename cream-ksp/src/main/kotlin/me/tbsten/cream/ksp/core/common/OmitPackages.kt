package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSName

internal fun omitPackagesFor(basePackage: KSName): List<String> = listOf("kotlin", basePackage.asString())
