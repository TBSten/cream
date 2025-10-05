package me.tbsten.cream

import me.tbsten.cream.ksp.options.ClassDeclarationInfo

data class OptionBuilderClassDeclarationInfo(
    override val packageName: String,
    override val underPackageName: String,
) : ClassDeclarationInfo {
    override val simpleName: String = underPackageName.split(".").lastOrNull() ?: ""
    override val fullName: String = buildString {
        append(packageName)
        if (packageName.isNotBlank()) append(".")
        append(underPackageName)
    }
}