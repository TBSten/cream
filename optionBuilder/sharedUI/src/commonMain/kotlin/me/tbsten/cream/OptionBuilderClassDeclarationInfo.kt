package me.tbsten.cream

import kotlinx.serialization.Serializable
import me.tbsten.cream.ksp.options.ClassDeclarationInfo

@Serializable
data class OptionBuilderClassDeclarationInfo(
    override val packageName: String,
    override val underPackageName: String,
) : ClassDeclarationInfo {
    override val simpleName: String = underPackageName.split(".").lastOrNull() ?: ""
    override val fullName: String =
        buildString {
            append(packageName)
            if (packageName.isNotBlank()) append(".")
            append(underPackageName)
        }
}
