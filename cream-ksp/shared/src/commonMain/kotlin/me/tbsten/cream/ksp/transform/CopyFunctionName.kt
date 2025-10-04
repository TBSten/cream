package me.tbsten.cream.ksp.transform

import me.tbsten.cream.InternalCreamApi
import me.tbsten.cream.ksp.options.ClassDeclarationInfo
import me.tbsten.cream.ksp.options.CreamOptions

@InternalCreamApi
fun copyFunctionName(
    source: ClassDeclarationInfo,
    target: ClassDeclarationInfo,
    options: CreamOptions,
): CopyFunctionName {
    val prefix = options.copyFunNamePrefix
    val targetName =
        options
            .copyFunNamingStrategy
            .funName(source, target)
            .let(options.escapeDot.escape)
            .let { if (prefix.lastOrNull()?.isLetter() ?: false) it.replaceFirstChar { it.uppercase() } else it }
    return CopyFunctionName(
        prefix = prefix,
        targetName = targetName,
    )
}

data class CopyFunctionName(
    val prefix: String,
    val targetName: String,
) {
    override fun toString() = "$prefix$targetName"
}