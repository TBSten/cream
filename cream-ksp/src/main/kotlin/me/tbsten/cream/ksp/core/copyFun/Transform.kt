package me.tbsten.cream.ksp.core.copyFun

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.ksp.core.common.CopyTargetRejection
import me.tbsten.cream.ksp.core.common.FindMappedSourceProperty
import me.tbsten.cream.ksp.core.common.GenerateSourceAnnotation
import me.tbsten.cream.ksp.core.common.IsExcluded
import me.tbsten.cream.ksp.core.common.concreteClassRejection
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.util.ksp.isSealed

/**
 * Report a target that cannot be a copy target.
 *
 * The rejection is emitted as a clean compilation error via [KSPLogger.error] (a normal
 * `COMPILATION_ERROR` that never leaves a half-written generated file), with the offending [target]
 * attached as the [com.google.devtools.ksp.symbol.KSNode] so the diagnostic carries the file/line
 * location and IDE navigation works.
 */
context(logger: KSPLogger)
private fun reportRejection(
    rejection: CopyTargetRejection,
    target: KSClassDeclaration,
) {
    val exception = rejection.asException(target.fullName)
    // CreamException always builds a non-null message; orEmpty() keeps the call non-null without
    // an unsafe assertion.
    logger.error(exception.message.orEmpty(), target)
}

/**
 * Emit the copy function(s) from [source] into [target], dispatching on the target's kind.
 *
 * [skipsObjectTarget] decides whether an `object` target is skipped instead of getting a function
 * that returns the singleton; it is passed in rather than derived here so a caller that does not
 * drive generation from a cream annotation can decide it directly. For cream's own annotations the
 * feature computes it via [GenerateSourceAnnotation.skipsObjectTarget].
 */
context(options: CreamOptions, logger: KSPLogger)
internal fun Appendable.appendCopyFunction(
    source: KSClassDeclaration,
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
            if (target.isSealed()) {
                // A sealed class, like a sealed interface, cannot be instantiated directly; fan out
                // to its concrete subclasses. The sealed check must come BEFORE concreteClassRejection()
                // because a sealed class is also abstract and the abstract check would pre-empt it.
                appendCopyToSealedClassFunction(
                    source,
                    target,
                    generateSourceAnnotation,
                    findMappedSourceProperty,
                    isExcluded,
                    skipsObjectTarget,
                    omitPackages,
                )
            } else {
                when (val rejection = target.concreteClassRejection()) {
                    null ->
                        appendCopyToClassFunction(
                            source,
                            target,
                            generateSourceAnnotation,
                            findMappedSourceProperty,
                            isExcluded,
                            omitPackages,
                        )

                    else -> reportRejection(rejection, target)
                }
            }

        ClassKind.OBJECT ->
            if (!skipsObjectTarget) {
                appendCopyToObjectFunction(source, target, generateSourceAnnotation)
            }

        // A sealed interface fans out to its concrete subclasses; a non-sealed interface cannot
        // be a copy target.
        ClassKind.INTERFACE ->
            if (target.isSealed()) {
                appendCopyToSealedClassFunction(
                    source,
                    target,
                    generateSourceAnnotation,
                    findMappedSourceProperty,
                    isExcluded,
                    skipsObjectTarget,
                    omitPackages,
                )
            } else {
                reportRejection(CopyTargetRejection.NON_SEALED_INTERFACE, target)
            }

        ClassKind.ENUM_CLASS,
        ClassKind.ENUM_ENTRY,
        -> reportRejection(CopyTargetRejection.ENUM_CLASS, target)
    }
}
