package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.ksp.core.common.resolveFunNameTemplate
import me.tbsten.cream.ksp.options.ClassDeclarationInfo
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.core.common.copyFunctionName as copyFunctionNameShared

internal fun KSClassDeclaration.toClassDeclarationInfo(): ClassDeclarationInfo {
    val kspClass = this
    return object : ClassDeclarationInfo {
        override val packageName: String = kspClass.packageName.asString()
        override val underPackageName: String = kspClass.underPackageName
        override val simpleName: String = kspClass.simpleName.asString()
        override val fullName: String = kspClass.fullName
    }
}

internal fun copyFunctionName(
    source: KSClassDeclaration,
    target: KSClassDeclaration,
    options: CreamOptions,
) = copyFunctionNameShared(
    source = source.toClassDeclarationInfo(),
    target = target.toClassDeclarationInfo(),
    options = options,
)

/**
 * Resolve [funNameTemplate] into the concrete name for the function generated from [source] to
 * [target]. cream intentionally does not validate the result's Kotlin-legality — an invalid name
 * (a keyword, illegal characters, …) simply fails to compile at the use site, and hard-coding
 * Kotlin's identifier rules here would only become a maintenance burden as they change.
 */
internal fun resolveFunName(
    funNameTemplate: String,
    source: KSClassDeclaration,
    target: KSClassDeclaration,
    options: CreamOptions,
): String =
    resolveFunNameTemplate(
        template = funNameTemplate,
        source = source.toClassDeclarationInfo(),
        target = target.toClassDeclarationInfo(),
        options = options,
    )

/**
 * Resolve the `funName` template for `@SealedCopy`. The target *is* the annotated [sealedClass],
 * and [me.tbsten.cream.DefaultCopyFunctionName] expands to `"copy"` (the sealed copy default)
 * rather than cream's `copyTo...` derived name.
 */
internal fun resolveSealedCopyFunName(
    funNameTemplate: String,
    sealedClass: KSClassDeclaration,
    options: CreamOptions,
): String {
    val info = sealedClass.toClassDeclarationInfo()
    return resolveFunNameTemplate(
        template = funNameTemplate,
        source = info,
        target = info,
        options = options,
        defaultName = "copy",
    )
}
