package me.tbsten.cream.ksp.feature.combineTo

import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.validate
import me.tbsten.cream.CombineTo
import me.tbsten.cream.DefaultCopyFunctionName
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.ProcessContext
import me.tbsten.cream.ksp.core.combineFun.appendCombineToFunction
import me.tbsten.cream.ksp.core.common.annotationsOf
import me.tbsten.cream.ksp.core.common.asDeclarationOrReport
import me.tbsten.cream.ksp.core.common.createNewKotlinFile
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.core.common.reportCreamError
import me.tbsten.cream.ksp.core.common.requireFunNameSupportsFanout
import me.tbsten.cream.ksp.core.common.resolveClassDeclarationOrReport
import me.tbsten.cream.ksp.core.common.resolveClassListOrReport
import me.tbsten.cream.ksp.core.common.resolveToClassDeclaration
import me.tbsten.cream.ksp.core.common.underPackageName
import me.tbsten.cream.ksp.core.common.warnIfSourceExcludeHasNoEffect
import me.tbsten.cream.ksp.util.with

private data class CombineToSourceEntry(
    val sourceDeclaration: KSDeclaration,
    val annotation: me.tbsten.cream.CombineTo,
)

private class TargetSourcesMapForCombineTo : MutableMap<KSClassDeclaration, MutableList<CombineToSourceEntry>> by mutableMapOf() {
    fun put(
        targetClass: KSClassDeclaration,
        entry: CombineToSourceEntry,
    ) {
        getOrPut(targetClass) { mutableListOf() }.add(entry)
    }
}

context(processContext: ProcessContext)
internal fun processCombineTo(): List<KSAnnotated> {
    val (combineToTargets, invalidCombineToTargets) =
        processContext.resolver
            .getSymbolsWithAnnotation(
                annotationName = CombineTo::class.fullName,
            ).partition { it.validate() }

    // Group source classes by their target classes
    val targetToSourcesMap = TargetSourcesMapForCombineTo()

    combineToTargets.forEach { target ->
        val sourceDeclaration =
            with(processContext.logger) { target.asDeclarationOrReport(CombineTo::class.simpleName!!) } ?: return@forEach
        val sourceClass =
            sourceDeclaration.resolveClassDeclarationOrReport(
                annotationName = CombineTo::class.simpleName!!,
                logger = processContext.logger,
            ) ?: return@forEach

        val combineToAnnotations = target.annotationsOf(CombineTo::class)

        // CombineTo.targets: List<KClass<*>>. If any target cannot be resolved to a class, an error
        // has been reported and we skip this source so no partial file is emitted.
        val targetClasses =
            with(processContext.logger) {
                combineToAnnotations.resolveClassListOrReport("targets", CombineTo::class.simpleName!!, sourceDeclaration)
            } ?: return@forEach

        // @CombineTo is NOT @Repeatable: it lists its targets in a single `vararg targets`. Listing
        // the same target twice (`@CombineTo(Foo::class, Foo::class)`) is an unambiguous user
        // mistake — cream would write the same generated file twice and crash with a
        // FileAlreadyExistsException (a KSP INTERNAL_ERROR). Reject it up front with a clean
        // positioned diagnostic and skip generating for this source so no partial file is left
        // behind (issue #101). The source declaration carries the file/line for IDE navigation.
        val duplicateTargets =
            targetClasses
                .groupingBy { it }
                .eachCount()
                .filterValues { it > 1 }
                .keys
        if (duplicateTargets.isNotEmpty()) {
            val duplicateNames = duplicateTargets.joinToString(", ") { it.fullName }
            processContext.logger.error(
                InvalidCreamUsageException(
                    message =
                        "Duplicate target $duplicateNames in @${CombineTo::class.simpleName} of ${sourceClass.fullName}.",
                    solution = "Remove the duplicate target from @${CombineTo::class.simpleName} (list each target at most once).",
                ).message.orEmpty(),
                sourceDeclaration,
            )
            return@forEach
        }

        val combineToAnnotation =
            target.getAnnotationsByType(CombineTo::class).firstOrNull() ?: return@forEach

        val funNameOk =
            requireFunNameSupportsFanout(
                funNameTemplate = runCatching { combineToAnnotation.funName }.getOrDefault(DefaultCopyFunctionName),
                generatesMultipleFunctions = targetClasses.size > 1,
                annotationSimpleName = CombineTo::class.simpleName!!,
                declarationFullName = sourceClass.fullName,
                logger = processContext.logger,
                ksNode = sourceDeclaration,
            )
        if (!funNameOk) return@forEach

        // Warn ONCE per source for @CombineTo.Exclude properties that match no parameter in ANY of
        // this source's target classes. Computed here (per source, union of all targets) rather than
        // inside the per-(target, source) generation loop below — doing it there duplicated the
        // warning once per target and falsely warned for a property matched in a sibling target.
        val generateSourceAnnotationForWarning = GenerateSourceAnnotation.CombineTo(annotation = combineToAnnotation)
        val allTargetParams = targetClasses.flatMap { it.primaryConstructor?.parameters.orEmpty() }
        sourceClass.getAllProperties().forEach { prop ->
            prop.warnIfSourceExcludeHasNoEffect(allTargetParams, sourceClass, generateSourceAnnotationForWarning, processContext.logger)
        }

        // Group source classes by target class
        targetClasses.forEach { targetClass ->
            targetToSourcesMap.put(
                targetClass,
                CombineToSourceEntry(
                    sourceDeclaration = sourceDeclaration,
                    annotation = combineToAnnotation,
                ),
            )
        }
    }

    // For each target class, generate copy functions for each source class
    targetToSourcesMap.forEach { (targetClass, sourceEntries) ->
        sourceEntries.forEach { sourceEntry ->
            val sourceDeclaration = sourceEntry.sourceDeclaration
            val sourceClass = sourceDeclaration.resolveToClassDeclaration()!!
            val otherSourceClasses =
                sourceEntries
                    .filter { it.sourceDeclaration != sourceDeclaration }
                    .map { it.sourceDeclaration.resolveToClassDeclaration()!! }

            val generateSourceAnnotation = GenerateSourceAnnotation.CombineTo(annotation = sourceEntry.annotation)

            processContext.codeGenerator
                .createNewKotlinFile(
                    dependencies = Dependencies(aggregating = true, sourceDeclaration.containingFile!!),
                    packageName = sourceClass.packageName,
                    fileName = "CombineTo__${sourceClass.underPackageName}__${targetClass.underPackageName}",
                ) {
                    // Generate combine function with multiple sources
                    with(processContext.options, processContext.logger) {
                        it.appendCombineToFunction(
                            primarySource = sourceClass,
                            otherSources = otherSourceClasses,
                            target = targetClass,
                            omitPackages = listOf("kotlin", sourceClass.packageName.asString()),
                            generateSourceAnnotation = generateSourceAnnotation,
                        )
                    }
                }
        }
    }

    return invalidCombineToTargets
}
