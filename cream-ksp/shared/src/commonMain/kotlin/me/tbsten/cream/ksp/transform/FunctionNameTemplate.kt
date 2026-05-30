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

/**
 * Render [template] for display in diagnostics: replace each internal token placeholder
 * `{{cream:X}}` with its public token const name `X`, so an error shows the tokens the user
 * referenced (e.g. `bad-CopyTargetSimpleName`) rather than cream's internal wrapper
 * (`bad-{{cream:CopyTargetSimpleName}}`).
 */
@InternalCreamApi
fun displayFunNameTemplate(template: String): String = template.replace(Regex("\\{\\{cream:([^}]*)}}"), "$1")

/**
 * Whether [name] is usable as the simple name of the top-level function cream generates:
 * a plain identifier (`toSuccess`) or a backtick-quoted identifier (`` `to success` ``).
 * Rejects the empty string and any name containing a character illegal in a Kotlin
 * function name — including a leftover, unexpanded token placeholder (which contains
 * `:` / `{` / `}`).
 */
@InternalCreamApi
fun isValidGeneratedFunctionName(name: String): Boolean {
    if (name.isEmpty()) return false
    if (name.startsWith("`")) {
        if (name.length < 3 || !name.endsWith("`")) return false
        val inner = name.substring(1, name.length - 1)
        return inner.isNotEmpty() && inner.none { it == '`' || it in forbiddenFunctionNameChars }
    }
    if (name.firstOrNull()?.isDigit() == true) return false
    // A bare Kotlin hard keyword (`is`, `fun`, `in`, ...) is a valid identifier characters-wise
    // but cannot be a function name without backticks; reject it so cream reports a clear error
    // instead of emitting `fun X.is(...)` that fails at the user's compiler.
    if (isKotlinHardKeyword(name)) return false
    return name.all { it.isLetterOrDigit() || it == '_' }
}

/**
 * Whether [name] is a Kotlin hard keyword — one that cannot be used as a plain (non-backtick)
 * function name. Soft/modifier keywords (`data`, `value`, `field`, ...) are deliberately not
 * included because they are valid function names.
 */
@InternalCreamApi
fun isKotlinHardKeyword(name: String): Boolean = name in kotlinHardKeywords

private val kotlinHardKeywords =
    setOf(
        "as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if",
        "in", "interface", "is", "null", "object", "package", "return", "super", "this",
        "throw", "true", "try", "typealias", "typeof", "val", "var", "when", "while",
    )

private val forbiddenFunctionNameChars = charArrayOf('.', ';', '[', ']', '/', '<', '>', ':', '\\', '\r', '\n')

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
