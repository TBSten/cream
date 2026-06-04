package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.DefaultCopyFunctionName
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.util.CopyTargetRejection
import me.tbsten.cream.ksp.util.concreteClassRejection
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.isSealed
import java.io.BufferedWriter

/**
 * Report a target that cannot be a copy / combine target.
 *
 * When a [KSPLogger] is available the rejection is emitted as a clean compilation error via
 * [KSPLogger.error] (a normal `COMPILATION_ERROR` that never leaves a half-written generated
 * file), with the offending [target] attached as the [com.google.devtools.ksp.symbol.KSNode]
 * so the diagnostic carries the file/line location and IDE navigation works.
 *
 * When no logger is provided (the `@CopyMapping` / `@CombineMapping` paths do not yet thread one
 * through) the rejection is *fail-closed* by throwing [InvalidCreamUsageException]: it surfaces
 * as an `INTERNAL_ERROR` rather than the cleaner `COMPILATION_ERROR`, but an invalid target is
 * never silently dropped. Threading a logger into the mapping processors so they too get a clean
 * diagnostic is a follow-up.
 */
private fun KSPLogger?.reportRejection(
    rejection: CopyTargetRejection,
    target: KSClassDeclaration,
) {
    val exception = rejection.asException(target.fullName)
    // CreamException always builds a non-null message; orEmpty() keeps the call non-null without
    // an unsafe assertion.
    if (this != null) {
        error(exception.message.orEmpty(), target)
    } else {
        throw exception
    }
}

internal fun BufferedWriter.appendCopyFunction(
    source: KSClassDeclaration,
    target: KSClassDeclaration,
    omitPackages: List<String>,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
    options: CreamOptions,
    notCopyToObject: Boolean,
    visibility: CopyVisibility = CopyVisibility.INHERIT,
    funNameTemplate: String = DefaultCopyFunctionName,
    generateTargetToSealedSubclasses: Boolean = true,
    logger: KSPLogger? = null,
) {
    when (target.classKind) {
        // An annotation class cannot currently be used as a copy target; it is rejected with a
        // clean diagnostic.
        ClassKind.ANNOTATION_CLASS -> logger.reportRejection(CopyTargetRejection.ANNOTATION_CLASS, target)

        ClassKind.CLASS ->
            when (val rejection = target.concreteClassRejection()) {
                null ->
                    appendCopyToClassFunction(
                        source,
                        target,
                        generateSourceAnnotation,
                        omitPackages,
                        options,
                        visibility,
                        funNameTemplate,
                        logger,
                    )

                else -> logger.reportRejection(rejection, target)
            }

        ClassKind.OBJECT ->
            if (!notCopyToObject) {
                appendCopyToObjectFunction(source, target, generateSourceAnnotation, options, visibility, funNameTemplate)
            }

        // A sealed interface fans out to its concrete subclasses; a non-sealed interface cannot
        // be a copy target.
        ClassKind.INTERFACE ->
            if (target.isSealed()) {
                if (generateTargetToSealedSubclasses) {
                    appendCopyToSealedClassFunction(
                        source,
                        target,
                        omitPackages,
                        options,
                        generateSourceAnnotation,
                        notCopyToObject,
                        visibility,
                        funNameTemplate,
                        logger,
                    )
                } else {
                    // no op
                }
            } else {
                logger.reportRejection(CopyTargetRejection.NON_SEALED_INTERFACE, target)
            }

        ClassKind.ENUM_CLASS,
        ClassKind.ENUM_ENTRY,
        -> logger.reportRejection(CopyTargetRejection.ENUM_CLASS, target)
    }
}

internal fun BufferedWriter.appendCombineToFunction(
    primarySource: KSClassDeclaration,
    otherSources: List<KSClassDeclaration>,
    target: KSClassDeclaration,
    omitPackages: List<String>,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
    options: CreamOptions,
    visibility: CopyVisibility = CopyVisibility.INHERIT,
    funNameTemplate: String = DefaultCopyFunctionName,
    logger: KSPLogger? = null,
) {
    when (target.classKind) {
        // Combine builds a single instance, so the target must be directly constructable: a
        // concrete class (with a reachable constructor) or an annotation class. There is no
        // single subclass to fan out to, so any interface is rejected.
        ClassKind.ANNOTATION_CLASS ->
            appendCombineToClassFunction(
                primarySource,
                otherSources,
                target,
                generateSourceAnnotation,
                omitPackages,
                options,
                visibility,
                funNameTemplate,
                logger,
            )

        ClassKind.CLASS ->
            when (val rejection = target.concreteClassRejection()) {
                null ->
                    appendCombineToClassFunction(
                        primarySource,
                        otherSources,
                        target,
                        generateSourceAnnotation,
                        omitPackages,
                        options,
                        visibility,
                        funNameTemplate,
                        logger,
                    )

                else -> logger.reportRejection(rejection, target)
            }

        ClassKind.OBJECT ->
            if (!options.notCopyToObject) {
                appendCombineToObjectFunction(
                    primarySource,
                    otherSources,
                    target,
                    generateSourceAnnotation,
                    options,
                    visibility,
                    funNameTemplate,
                )
            }

        ClassKind.INTERFACE -> logger.reportRejection(CopyTargetRejection.INTERFACE_NOT_COMBINABLE, target)

        ClassKind.ENUM_CLASS,
        ClassKind.ENUM_ENTRY,
        -> logger.reportRejection(CopyTargetRejection.ENUM_CLASS, target)
    }
}
