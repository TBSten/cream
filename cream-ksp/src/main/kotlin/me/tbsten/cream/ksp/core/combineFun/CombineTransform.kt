package me.tbsten.cream.ksp.core.combineFun

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.options.CreamOptions
import java.io.BufferedWriter

context(options: CreamOptions, logger: KSPLogger)
internal fun BufferedWriter.appendCombineToFunction(
    primarySource: KSClassDeclaration,
    otherSources: List<KSClassDeclaration>,
    target: KSClassDeclaration,
    omitPackages: List<String>,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
    annotated: KSDeclaration = primarySource,
) {
    when (target.classKind) {
        ClassKind.CLASS,
        ClassKind.ANNOTATION_CLASS,
        ->
            appendCombineToClassFunction(
                primarySource,
                otherSources,
                target,
                generateSourceAnnotation,
                omitPackages,
                annotated = annotated,
            )

        ClassKind.OBJECT ->
            if (!options.notCopyToObject) {
                appendCombineToObjectFunction(
                    primarySource,
                    otherSources,
                    target,
                    generateSourceAnnotation,
                    annotated = annotated,
                )
            }

        // A combine target must be constructable (class / annotation class / object). Interfaces and
        // enums cannot be built, so reject them — branches are listed explicitly (no `else`) so a new
        // ClassKind forces a compile-time decision here.
        ClassKind.INTERFACE,
        ClassKind.ENUM_CLASS,
        ClassKind.ENUM_ENTRY,
        -> reportUnsupportedCombineTarget(target)
    }
}

/**
 * Report a [target] whose [ClassKind] cannot be a `@CombineTo` / `@CombineFrom` / `@CombineMapping`
 * target. A clean positioned `COMPILATION_ERROR` via [KSPLogger.error]
 * (leaving no partial generated file).
 */
context(logger: KSPLogger)
private fun reportUnsupportedCombineTarget(target: KSClassDeclaration) {
    val exception =
        InvalidCreamUsageException(
            message =
                "Unsupported combine to ${
                    target.classKind.name.lowercase().replace("_", " ")
                } (${target.fullName}).",
            solution = "Please make ${target.fullName} a class or object.",
        )
    logger.error(exception.message.orEmpty(), target)
}
