package me.tbsten.cream.ksp.transform

import me.tbsten.cream.CopyTargetFullName
import me.tbsten.cream.CopyTargetInnerName
import me.tbsten.cream.CopyTargetSimpleName
import me.tbsten.cream.CopyTargetUnderPackage
import me.tbsten.cream.DefaultCopyFunctionName
import me.tbsten.cream.InternalCreamApi
import me.tbsten.cream.copy_target_full_name
import me.tbsten.cream.copy_target_inner_name
import me.tbsten.cream.copy_target_simple_name
import me.tbsten.cream.copy_target_under_package
import me.tbsten.cream.ksp.options.ClassDeclarationInfo
import me.tbsten.cream.ksp.options.CopyFunNamingStrategy
import me.tbsten.cream.ksp.options.CreamOptions

/**
 * Expand every cream naming token (see `CopyFunctionNameToken.kt`) embedded in [template]
 * for a single generated function, producing its final name.
 *
 * [DefaultCopyFunctionName] expands to [defaultName] — by default cream's derived name (via
 * [copyFunctionName], project-option aware). Callers whose default is not the derived name
 * (e.g. `@SealedCopy`, whose default is `"copy"`) pass [defaultName] explicitly. The
 * `CopyTarget*` tokens render [target] with a fixed Pascal/snake strategy, independent of
 * `copyFunNamingStrategy` / `escapeDot`. A [template] with no token is returned verbatim.
 *
 * cream does not validate that the result is a legal Kotlin function name: an invalid name
 * (a keyword, illegal characters, …) simply fails to compile at the use site, and encoding
 * Kotlin's identifier rules here would only become a maintenance burden as they change.
 */
@InternalCreamApi
fun resolveFunNameTemplate(
    template: String,
    source: ClassDeclarationInfo,
    target: ClassDeclarationInfo,
    options: CreamOptions,
    defaultName: String = copyFunctionName(source, target, options).toString(),
): String {
    var result = template
    if (result.contains(DefaultCopyFunctionName)) {
        result = result.replace(DefaultCopyFunctionName, defaultName)
    }
    for ((placeholder, expand) in copyTargetTokenExpanders) {
        if (result.contains(placeholder)) {
            result = result.replace(placeholder, expand(source, target))
        }
    }
    return result
}

/**
 * Whether [template] embeds at least one cream naming token. Used to guard against a
 * plain-literal `funName` on an annotation that generates more than one function (which
 * would emit duplicate names).
 */
@InternalCreamApi
fun containsAnyCopyFunNameToken(template: String): Boolean =
    template.contains(DefaultCopyFunctionName) ||
        copyTargetTokenExpanders.any { (placeholder, _) -> template.contains(placeholder) }

private fun pascalCase(raw: String): String = raw.split(".").joinToString("") { segment -> segment.replaceFirstChar { it.uppercase() } }

private fun snakeCase(raw: String): String = raw.split(".").joinToString("_") { it.lowercase() }

/**
 * `CopyTarget*` placeholder -> expansion. Each reuses a fixed [CopyFunNamingStrategy] for the
 * raw name and renders it Pascal/snake here, deliberately ignoring the project's
 * strategy/escape options. [DefaultCopyFunctionName] is handled separately in
 * [resolveFunNameTemplate] because its expansion is supplied by the caller.
 */
private val copyTargetTokenExpanders: Map<String, (ClassDeclarationInfo, ClassDeclarationInfo) -> String> =
    mapOf(
        CopyTargetSimpleName to { source, target ->
            pascalCase(CopyFunNamingStrategy.`simple-name`.funName(source, target))
        },
        copy_target_simple_name to { source, target ->
            snakeCase(CopyFunNamingStrategy.`simple-name`.funName(source, target))
        },
        CopyTargetUnderPackage to { source, target ->
            pascalCase(CopyFunNamingStrategy.`under-package`.funName(source, target))
        },
        copy_target_under_package to { source, target ->
            snakeCase(CopyFunNamingStrategy.`under-package`.funName(source, target))
        },
        CopyTargetInnerName to { source, target ->
            pascalCase(CopyFunNamingStrategy.`inner-name`.funName(source, target))
        },
        copy_target_inner_name to { source, target ->
            snakeCase(CopyFunNamingStrategy.`inner-name`.funName(source, target))
        },
        CopyTargetFullName to { source, target ->
            pascalCase(CopyFunNamingStrategy.`full-name`.funName(source, target))
        },
        copy_target_full_name to { source, target ->
            snakeCase(CopyFunNamingStrategy.`full-name`.funName(source, target))
        },
    )
