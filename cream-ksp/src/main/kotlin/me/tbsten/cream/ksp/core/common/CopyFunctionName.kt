package me.tbsten.cream.ksp.core.common

import me.tbsten.cream.ksp.options.ClassDeclarationInfo
import me.tbsten.cream.ksp.options.CreamOptions

internal fun copyFunctionName(
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

internal data class CopyFunctionName(
    val prefix: String,
    val targetName: String,
) {
    override fun toString(): String = "$prefix$targetName"
}
