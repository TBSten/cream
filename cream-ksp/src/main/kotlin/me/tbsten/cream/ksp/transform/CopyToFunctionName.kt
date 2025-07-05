package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.ksp.options.CreamOptions

internal fun copyToFunctionName(
    source: KSClassDeclaration,
    target: KSClassDeclaration,
    options: CreamOptions,
) = "${options.copyFunNamePrefix}${options.copyFunNamingStrategy.funName(source, target)}"
    .let { options.escapeDot.escape(it) }
    .let { it.replaceFirstChar { it.uppercase() } }
