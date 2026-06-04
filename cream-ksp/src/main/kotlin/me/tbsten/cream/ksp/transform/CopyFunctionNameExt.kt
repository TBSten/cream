package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.options.ClassDeclarationInfo
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.lines
import me.tbsten.cream.ksp.util.underPackageName

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
) = copyFunctionName(
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

/**
 * Build the [InvalidCreamUsageException] reported when a plain-literal [funNameTemplate] would
 * fan out to more than one identically named function. Single source of truth for the message.
 */
private fun fixedFunNameFanoutException(
    funNameTemplate: String,
    annotationSimpleName: String,
    declarationFullName: String,
): InvalidCreamUsageException =
    InvalidCreamUsageException(
        message =
            lines(
                "@$annotationSimpleName on $declarationFullName sets a fixed funName \"$funNameTemplate\",",
                "but it generates more than one function (multiple targets or sources, a sealed",
                "target, or a reversible mapping). Those functions would all share that name and collide.",
            ),
        solution =
            lines(
                "Include a naming token so each generated function gets a distinct name, e.g.",
                "  funName = \"to\" + CopyTargetSimpleName",
                "or split the declaration into separate annotations.",
            ),
    )

/**
 * Reject (with a clean positioned `COMPILATION_ERROR` via [KSPLogger.error] anchored at [ksNode])
 * a [funNameTemplate] that is a plain literal (contains no naming token) yet generates more than
 * one function: the functions would all share that one name and collide. A template containing any
 * token is fine because each generated function then derives a distinct name from its own target.
 *
 * @return `true` when the funName is acceptable (caller proceeds); `false` when it was rejected and
 * an error has already been reported (caller must skip the offending unit so no partial file is
 * emitted).
 */
internal fun requireFunNameSupportsFanout(
    funNameTemplate: String,
    generatesMultipleFunctions: Boolean,
    annotationSimpleName: String,
    declarationFullName: String,
    logger: KSPLogger,
    ksNode: KSNode?,
): Boolean {
    if (!generatesMultipleFunctions) return true
    if (containsAnyCopyFunNameToken(funNameTemplate)) return true
    logger.error(
        fixedFunNameFanoutException(funNameTemplate, annotationSimpleName, declarationFullName).message.orEmpty(),
        ksNode,
    )
    return false
}
