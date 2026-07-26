package me.tbsten.cream.ksp.core.copyFun

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.ksp.core.common.CopyTargetRejection
import me.tbsten.cream.ksp.core.common.GenerateSourceAnnotation
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

context(options: CreamOptions, logger: KSPLogger)
internal fun Appendable.appendCopyFunction(
    source: KSClassDeclaration,
    target: KSClassDeclaration,
    omitPackages: List<String>,
    generateSourceAnnotation: GenerateSourceAnnotation,
) {
    // Each source annotation decides for itself whether an object target is skipped. Of cream's
    // own annotations only @CopyToChildren carries a notCopyToObject control, and it falls back to
    // the cream.notCopyToObject option when the user leaves that control unset; every other one
    // names its (possibly object) target explicitly, so it always generates.
    val notCopyToObject = generateSourceAnnotation.skipsObjectTarget(options.notCopyToObject)

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
                    omitPackages,
                    generateSourceAnnotation,
                )
            } else {
                when (val rejection = target.concreteClassRejection()) {
                    null ->
                        appendCopyToClassFunction(
                            source,
                            target,
                            generateSourceAnnotation,
                            omitPackages,
                        )

                    else -> reportRejection(rejection, target)
                }
            }

        ClassKind.OBJECT ->
            if (!notCopyToObject) {
                appendCopyToObjectFunction(source, target, generateSourceAnnotation)
            }

        // A sealed interface fans out to its concrete subclasses; a non-sealed interface cannot
        // be a copy target.
        ClassKind.INTERFACE ->
            if (target.isSealed()) {
                appendCopyToSealedClassFunction(
                    source,
                    target,
                    omitPackages,
                    generateSourceAnnotation,
                )
            } else {
                reportRejection(CopyTargetRejection.NON_SEALED_INTERFACE, target)
            }

        ClassKind.ENUM_CLASS,
        ClassKind.ENUM_ENTRY,
        -> reportRejection(CopyTargetRejection.ENUM_CLASS, target)
    }
}
