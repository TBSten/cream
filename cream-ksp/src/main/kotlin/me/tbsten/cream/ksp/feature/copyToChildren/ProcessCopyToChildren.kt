package me.tbsten.cream.ksp.feature.copyToChildren

import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.validate
import me.tbsten.cream.CopyToChildren
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.ProcessContext
import me.tbsten.cream.ksp.core.common.annotationsOf
import me.tbsten.cream.ksp.core.common.createNewKotlinFile
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.core.common.reportCreamError
import me.tbsten.cream.ksp.core.common.underPackageName
import me.tbsten.cream.ksp.core.copyFun.appendCopyFunction
import me.tbsten.cream.ksp.util.ksp.isSealed
import me.tbsten.cream.ksp.util.with

context(processContext: ProcessContext)
internal fun processCopyToChildren(): List<KSAnnotated> {
    val (copyToChildrenTargets, invalidCopyToChildrenTargets) =
        processContext.resolver
            .getSymbolsWithAnnotation(
                annotationName = CopyToChildren::class.fullName,
            ).partition { it.validate() }

    copyToChildrenTargets.forEach { copyToChildren ->
        if (copyToChildren !is KSClassDeclaration) {
            processContext.logger.reportCopyToChildrenNotADeclaration(copyToChildren)
            return@forEach
        }

        if (!copyToChildren.isSealed()) {
            processContext.logger.reportCopyToChildrenNotSealed(copyToChildren)
            return@forEach
        }

        val sourceSealedClass = copyToChildren
        val copyToChildrenAnnotation =
            sourceSealedClass.getAnnotationsByType(CopyToChildren::class).firstOrNull() ?: return@forEach

        val generateSourceAnnotation = GenerateSourceAnnotation.CopyToChildren(annotation = copyToChildrenAnnotation)

        // Enclose notCopyToObject in runCatching because it may cause an error if notCopyToObject cannot be obtained.
        val notCopyToObject =
            runCatching {
                copyToChildrenAnnotation.notCopyToObject
            }.getOrDefault(processContext.options.notCopyToObject)

        val targetClasses = sourceSealedClass.getSealedSubclasses()

        // Warn for @CopyToChildren.Exclude on non-abstract properties (no-op: not in generated copy functions)
        sourceSealedClass
            .getAllProperties()
            .filter { !it.isAbstract() && it.annotationsOf(CopyToChildren.Exclude::class).any() }
            .forEach { prop ->
                processContext.logger.warn(
                    "@Exclude on '${prop.simpleName.asString()}' has no effect: not a matched property",
                    prop,
                )
            }

        processContext.codeGenerator
            .createNewKotlinFile(
                dependencies =
                    Dependencies(
                        aggregating = true,
                        sourceSealedClass.containingFile!!,
                    ),
                packageName = sourceSealedClass.packageName,
                fileName = "CopyToChildren__${sourceSealedClass.underPackageName}",
            ) {
                targetClasses.forEach { targetClass ->
                    // generate sourceClass to sourceClass copy function
                    with(processContext.options, processContext.logger) {
                        it.appendCopyFunction(
                            source = sourceSealedClass,
                            target = targetClass,
                            omitPackages =
                                listOf(
                                    "kotlin",
                                    sourceSealedClass.packageName.asString(),
                                ),
                            generateSourceAnnotation = generateSourceAnnotation,
                            notCopyToObject = notCopyToObject,
                        )
                    }
                }
            }
    }

    return invalidCopyToChildrenTargets
}

// ---------------------------------------------------------------------------
// Diagnostic helpers
// ---------------------------------------------------------------------------

private fun KSPLogger.reportCopyToChildrenNotADeclaration(annotated: KSAnnotated) {
    reportCreamError(
        InvalidCreamUsageException(
            message =
                "@${CopyToChildren::class.simpleName} annotation must be applied to a sealed class/interface." +
                    if (annotated is KSDeclaration) {
                        annotated.simpleName.asString() + " is not sealed class/interface"
                    } else {
                        ""
                    },
            solution = (annotated as? KSDeclaration)?.let { "Make ${it.fullName} a sealed class/interface." },
        ),
        annotated,
    )
}

private fun KSPLogger.reportCopyToChildrenNotSealed(annotated: KSClassDeclaration) {
    // Avoid `fullName`, which throws UnknownCreamException when qualifiedName is null
    // (e.g. local/anonymous declarations) and would mask this InvalidCreamUsageException.
    val displayName = annotated.qualifiedName?.asString() ?: annotated.simpleName.asString()
    reportCreamError(
        InvalidCreamUsageException(
            message =
                "@${CopyToChildren::class.simpleName} annotation must be applied to a sealed class/interface, " +
                    "but $displayName is not sealed (classKind: ${annotated.classKind}).",
            solution = "Make $displayName a sealed class/interface.",
        ),
        annotated,
    )
}
