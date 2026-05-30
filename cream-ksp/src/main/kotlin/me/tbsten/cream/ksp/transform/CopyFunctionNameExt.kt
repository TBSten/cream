package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.symbol.KSClassDeclaration
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
 * Fail the build when [funNameTemplate] is a plain literal (contains no naming token) yet the
 * annotation generates more than one function: the functions would all share that one name
 * and collide. A template containing any token is fine because each generated function then
 * derives a distinct name from its own target.
 */
internal fun requireFunNameSupportsFanout(
    funNameTemplate: String,
    generatesMultipleFunctions: Boolean,
    annotationSimpleName: String,
    declarationFullName: String,
) {
    if (!generatesMultipleFunctions) return
    if (containsAnyCopyFunNameToken(funNameTemplate)) return
    throw InvalidCreamUsageException(
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
}

/**
 * Fail when two of the [functions] share an identity key — i.e. the same mapping written more than
 * once. Each entry is `identityKey to displayName`; the caller builds the key from the receiver,
 * name, and mapped classes. This catches the common copy-paste duplicate with a clear cream error
 * instead of a redeclaration at the user's compiler. Used by the repeatable mapping annotations,
 * where stacked occurrences land in one file.
 *
 * It is deliberately not a full overload-collision check: two entries with *different* mapped
 * classes get distinct keys and pass here even if they would emit the same Kotlin signature
 * (Kotlin ignores the return type in overload resolution). Detecting that precisely would mean
 * replicating Kotlin's signature rules, so cream leaves it to the compiler's "Conflicting
 * overloads" instead — see the funName note: cream does not pre-validate what the compiler enforces.
 */
internal fun requireNoDuplicateGeneratedFunctions(
    functions: List<Pair<String, String>>,
    annotationSimpleName: String,
    declarationFullName: String,
) {
    val seen = HashSet<String>()
    for ((key, display) in functions) {
        if (!seen.add(key)) {
            throw InvalidCreamUsageException(
                message =
                    lines(
                        "@$annotationSimpleName on $declarationFullName generates the function $display more than once.",
                        "Stacked occurrences resolve to the same receiver and name (an identical signature), which collide.",
                    ),
                solution =
                    lines(
                        "Remove the duplicate occurrence, or give the occurrences distinct funName values.",
                    ),
            )
        }
    }
}
