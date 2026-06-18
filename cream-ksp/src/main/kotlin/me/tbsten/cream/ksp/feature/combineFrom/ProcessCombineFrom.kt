package me.tbsten.cream.ksp.feature.combineFrom

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.validate
import me.tbsten.cream.CombineFrom
import me.tbsten.cream.DefaultCopyFunctionName
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.ProcessContext
import me.tbsten.cream.ksp.core.combineFun.appendCombineToFunction
import me.tbsten.cream.ksp.core.common.GenerateSourceAnnotation
import me.tbsten.cream.ksp.core.common.annotationsOf
import me.tbsten.cream.ksp.core.common.asDeclarationOrReport
import me.tbsten.cream.ksp.core.common.createNewKotlinFile
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.core.common.funNameTemplate
import me.tbsten.cream.ksp.core.common.reportCreamError
import me.tbsten.cream.ksp.core.common.resolveClassDeclarationOrReport
import me.tbsten.cream.ksp.core.common.resolveClassListOrReport
import me.tbsten.cream.ksp.core.common.resolveFunName
import me.tbsten.cream.ksp.core.common.underPackageName
import me.tbsten.cream.ksp.util.lines
import me.tbsten.cream.ksp.util.with

context(processContext: ProcessContext)
internal fun processCombineFrom(): List<KSAnnotated> {
    val (combineFromTargets, invalidCombineFromTargets) =
        processContext.resolver
            .getSymbolsWithAnnotation(
                annotationName = CombineFrom::class.fullName,
            ).partition { it.validate() }

    combineFromTargets.forEach { target ->
        val targetDeclaration =
            with(processContext.logger) { target.asDeclarationOrReport(CombineFrom::class.simpleName!!) } ?: return@forEach
        val targetClass =
            targetDeclaration.resolveClassDeclarationOrReport(
                annotationName = CombineFrom::class.simpleName!!,
                logger = processContext.logger,
            ) ?: return@forEach

        val combineFromAnnotations = target.annotationsOf(CombineFrom::class)

        // @CombineFrom is @Repeatable; the sources of every occurrence are flattened into one
        // merged copy function. Stacking it twice with the same source set (or simply repeating a
        // class across occurrences) is an idempotent re-declaration, so dedupe the collected
        // sources: keeping a duplicate would emit a function with two identically named parameters
        // ("Conflicting declarations") and re-list the same source in KDoc (issue #101).
        val sourceClasses =
            with(processContext.logger) {
                combineFromAnnotations.resolveClassListOrReport("sources", CombineFrom::class.simpleName!!, targetDeclaration)
            }?.distinct() ?: return@forEach

        // Need at least one source class
        if (sourceClasses.isEmpty()) {
            processContext.logger.reportCombineFromNoSources(targetDeclaration)
            return@forEach
        }

        // First source class is the primary source (extension function receiver)
        val primarySource = sourceClasses.firstOrNull() ?: return@forEach
        val otherSources = sourceClasses.drop(1)

        // @CombineFrom is @Repeatable and all occurrences are merged into ONE generated function,
        // so the funName must be unambiguous. Reading it from only the first occurrence would
        // silently drop a different funName set on a later one — instead, require the explicit
        // funName values to agree.
        val explicitFunNameTemplates =
            combineFromAnnotations
                .map { it.funNameTemplate() }
                .filter { it != DefaultCopyFunctionName }
                .distinct()
                .toList()
        // Compare the *resolved* names, not the raw (KSP-folded) templates: the occurrences are
        // merged into one function that takes a single name, so they are only ambiguous when they
        // resolve to different names. Resolving also keeps the diagnostic readable — it shows
        // "toFoo" rather than the internal "to{{cream:CopyTargetSimpleName}}" placeholder form.
        val explicitFunNames =
            explicitFunNameTemplates
                .map { resolveFunName(it, primarySource, targetClass, processContext.options) }
                .distinct()
        if (explicitFunNames.size > 1) {
            processContext.logger.reportCombineFromConflictingFunNames(
                targetDeclaration,
                targetClass,
                explicitFunNames,
            )
            return@forEach
        }
        val funNameTemplate = explicitFunNameTemplates.firstOrNull() ?: DefaultCopyFunctionName

        // All occurrences are merged into one function. GSA derives kdoc/visibility from the first
        // occurrence's raw annotation; funNameTemplate is the cross-occurrence merge result
        // (resolved above with conflict detection) and so is passed explicitly. See #134.
        val combineFromAnnotation =
            combineFromAnnotations.firstOrNull() ?: return@forEach

        val generateSourceAnnotation =
            GenerateSourceAnnotation.CombineFrom(
                annotation = combineFromAnnotation,
                funNameTemplate = funNameTemplate,
            )

        processContext.codeGenerator
            .createNewKotlinFile(
                dependencies = Dependencies(aggregating = true, targetDeclaration.containingFile!!),
                packageName = targetClass.packageName,
                fileName = "CombineFrom__${primarySource.underPackageName}__${targetClass.underPackageName}",
            ) {
                // Generate combine function with multiple sources
                with(processContext.options, processContext.logger) {
                    it.appendCombineToFunction(
                        primarySource = primarySource,
                        otherSources = otherSources,
                        target = targetClass,
                        omitPackages = listOf("kotlin", primarySource.packageName.asString()),
                        generateSourceAnnotation = generateSourceAnnotation,
                        annotated = targetDeclaration,
                    )
                }
            }
    }

    return invalidCombineFromTargets
}

// ---------------------------------------------------------------------------
// Diagnostic helpers
// ---------------------------------------------------------------------------

private fun KSPLogger.reportCombineFromNoSources(targetDeclaration: KSDeclaration) {
    reportCreamError(
        InvalidCreamUsageException(
            message = "@${CombineFrom::class.simpleName} requires at least one source class.",
            solution = "Specify at least one source class in @${CombineFrom::class.simpleName}.sources of ${targetDeclaration.fullName}.",
        ),
        targetDeclaration,
    )
}

private fun KSPLogger.reportCombineFromConflictingFunNames(
    targetDeclaration: KSDeclaration,
    targetClass: KSClassDeclaration,
    conflictingNames: List<String>,
) {
    reportCreamError(
        InvalidCreamUsageException(
            message =
                lines(
                    "@${CombineFrom::class.simpleName} on ${targetClass.fullName} is repeated with conflicting funName values:",
                    conflictingNames.joinToString(", ") { "\"$it\"" },
                    "Stacked @${CombineFrom::class.simpleName} annotations are merged into a single generated function, so funName must be unambiguous.",
                ),
            solution =
                lines(
                    "Set the same funName on every @${CombineFrom::class.simpleName} of ${targetClass.fullName}, or set it on only one.",
                ),
        ),
        targetDeclaration,
    )
}
