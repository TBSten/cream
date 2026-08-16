package me.tbsten.cream.ksp.core.combineFun

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.ksp.core.common.FindMappedSourceProperty
import me.tbsten.cream.ksp.core.common.GenerateSourceAnnotation
import me.tbsten.cream.ksp.core.common.IsExcluded
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.core.error.InvalidCreamUsageException
import me.tbsten.cream.ksp.options.CreamOptions

/**
 * Emit the combine function(s) from [primarySource] + [otherSources] into [target], dispatching on
 * the target's kind.
 *
 * [skipsObjectTarget] decides whether an `object` target is skipped instead of getting a function
 * that returns the singleton; it is passed in rather than read from the options here so a caller
 * that does not drive generation from a cream annotation can decide it directly.
 */
context(options: CreamOptions, logger: KSPLogger)
internal fun Appendable.appendCombineToFunction(
    primarySource: KSClassDeclaration,
    otherSources: List<KSClassDeclaration>,
    target: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation,
    findMappedSourceProperty: FindMappedSourceProperty,
    isExcluded: IsExcluded,
    skipsObjectTarget: Boolean,
    omitPackages: List<String>,
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
                findMappedSourceProperty,
                isExcluded,
                omitPackages,
            )

        ClassKind.OBJECT ->
            if (!skipsObjectTarget) {
                appendCombineToObjectFunction(
                    primarySource,
                    otherSources,
                    target,
                    generateSourceAnnotation,
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
