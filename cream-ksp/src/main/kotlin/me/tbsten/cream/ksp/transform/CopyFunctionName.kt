package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.ksp.options.CreamOptions

internal fun copyFunctionName(
    source: KSClassDeclaration,
    target: KSClassDeclaration,
    options: CreamOptions,
): String {
    val prefix = options.copyFunNamePrefix
    val funName =
        options
            .copyFunNamingStrategy
            .funName(source, target)
            .let(options.escapeDot.escape)
            .let { if (prefix.last().isLetter()) it.replaceFirstChar { it.uppercase() } else it }
    return "$prefix$funName"
}
