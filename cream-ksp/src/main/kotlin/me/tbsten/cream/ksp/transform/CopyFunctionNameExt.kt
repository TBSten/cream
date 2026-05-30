package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.DefaultCopyFunctionName
import me.tbsten.cream.ksp.GenerateSourceAnnotation
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
 * Resolve [funNameTemplate] into the concrete name for the function generated from
 * [source] to [target], then validate it.
 *
 * The resolved name is always validated. A user-composed name that is not a valid Kotlin
 * function name fails the build with an [InvalidCreamUsageException]. When `funName` was
 * omitted, every supported option (`lower-camel-case` / `replace-to-underscore`) yields a
 * valid name and the derived name is emitted unchanged; the one exception is
 * `escapeDot=backquote` with a non-empty prefix, which produces an invalid name (e.g.
 * `` copyTo`Target` ``) and is now reported against the naming option rather than silently
 * emitted as broken code.
 */
internal fun resolveValidatedFunName(
    funNameTemplate: String,
    source: KSClassDeclaration,
    target: KSClassDeclaration,
    options: CreamOptions,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
): String {
    val name =
        resolveFunNameTemplate(
            template = funNameTemplate,
            source = source.toClassDeclarationInfo(),
            target = target.toClassDeclarationInfo(),
            options = options,
        )
    if (isValidGeneratedFunctionName(name)) return name

    val annotationName = generateSourceAnnotation.annotationClass.simpleName ?: "cream annotation"
    // Name the *annotated* declaration, not `source`: for @CopyMapping / @CombineMapping the
    // source is a mapped (often un-owned) class with no annotation, and for @CopyFrom it is the
    // copied-from class. The annotation actually lives on annotationTarget.
    val annotatedName =
        generateSourceAnnotation.annotationTarget.let { it.qualifiedName?.asString() ?: it.simpleName.asString() }

    if (funNameTemplate == DefaultCopyFunctionName) {
        // funName was omitted, yet cream's derived name is not a valid Kotlin function name. A
        // project naming option produced it — in practice cream.escapeDot=backquote together with a
        // non-empty cream.copyFunNamePrefix (e.g. "copyTo`Target`"). Report it against the option,
        // which the user actually set, rather than funName, which they did not.
        throw InvalidCreamUsageException(
            message =
                lines(
                    "cream's derived name \"$name\" for ${target.fullName} (from @$annotationName on $annotatedName)",
                    "is not a valid Kotlin function name. A project naming option produced it — most likely",
                    "cream.escapeDot=backquote together with a non-empty cream.copyFunNamePrefix.",
                ),
            solution =
                lines(
                    "Use cream.escapeDot=lower-camel-case or replace-to-underscore, set cream.copyFunNamePrefix",
                    "to \"\", or set funName explicitly on the declaration.",
                ),
        )
    }

    throw InvalidCreamUsageException(
        message =
            lines(
                "@$annotationName on $annotatedName produced an invalid function name \"$name\".",
                "  funName template : \"$funNameTemplate\"",
                "  target           : ${target.fullName}",
            ),
        solution = invalidFunNameSolution(name, funNameTemplate),
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
                "but it generates more than one function (multiple targets, a sealed target,",
                "or a reversible mapping). Those functions would all share that name and collide.",
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
 * Resolve and validate the `funName` template for `@SealedCopy`. The target *is* the
 * annotated [sealedClass], and [DefaultCopyFunctionName] expands to `"copy"` (the sealed
 * copy default) rather than cream's `copyTo...` derived name. A bare default is left
 * unvalidated; any user-composed name is validated like the other annotations.
 */
internal fun resolveValidatedSealedCopyFunName(
    funNameTemplate: String,
    sealedClass: KSClassDeclaration,
    options: CreamOptions,
): String {
    val info = sealedClass.toClassDeclarationInfo()
    val name =
        resolveFunNameTemplate(
            template = funNameTemplate,
            source = info,
            target = info,
            options = options,
            defaultName = "copy",
        )
    if (funNameTemplate != DefaultCopyFunctionName && !isValidGeneratedFunctionName(name)) {
        throw InvalidCreamUsageException(
            message =
                lines(
                    "@SealedCopy on ${sealedClass.fullName} produced an invalid function name \"$name\".",
                    "  funName template : \"$funNameTemplate\"",
                ),
            solution = invalidFunNameSolution(name, funNameTemplate),
        )
    }
    return name
}

/**
 * Shared "solution" text for an invalid generated function name. Adds a targeted hint when the
 * name is a Kotlin keyword (suggest backticks) or when the template embeds DefaultCopyFunctionName
 * under escapeDot=backquote (the default is itself backtick-quoted and cannot take an affix).
 */
private fun invalidFunNameSolution(
    name: String,
    funNameTemplate: String,
): String =
    lines(
        *buildList {
            add("The generated function name must be a valid Kotlin function name —")
            add("a plain identifier, or a backtick-quoted name.")
            add("Adjust funName, or the tokens it expands to, so it produces one.")
            if (isKotlinHardKeyword(name)) {
                add("Note: \"$name\" is a Kotlin keyword; wrap it in backticks to use it as a name, e.g. funName = \"`$name`\".")
            }
            if (DefaultCopyFunctionName in funNameTemplate) {
                add(
                    "Note: when cream.escapeDot=backquote, DefaultCopyFunctionName is itself a " +
                        "backtick-quoted name and cannot take a prefix/suffix — use a CopyTarget* token instead.",
                )
            }
        }.toTypedArray(),
    )
