package me.tbsten.cream.ksp.feature.combineTo

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.validate
import me.tbsten.cream.CombineTo
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.ProcessContext
import me.tbsten.cream.ksp.core.combineFun.appendCombineToFunction
import me.tbsten.cream.ksp.core.common.GenerateSourceAnnotation
import me.tbsten.cream.ksp.core.common.annotationsOf
import me.tbsten.cream.ksp.core.common.asDeclarationOrReport
import me.tbsten.cream.ksp.core.common.createNewKotlinFile
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.core.common.funNameTemplate
import me.tbsten.cream.ksp.core.common.omitPackagesFor
import me.tbsten.cream.ksp.core.common.onInvalid
import me.tbsten.cream.ksp.core.common.reportCreamError
import me.tbsten.cream.ksp.core.common.resolveClassDeclarationOrReport
import me.tbsten.cream.ksp.core.common.resolveClassListOrReport
import me.tbsten.cream.ksp.core.common.resolveToClassDeclaration
import me.tbsten.cream.ksp.core.common.underPackageName
import me.tbsten.cream.ksp.core.common.validateFunName
import me.tbsten.cream.ksp.core.common.warnIfSourceExcludeHasNoEffect
import me.tbsten.cream.ksp.util.with

private val annotationName = CombineTo::class.simpleName!!

private data class CombineToSourceEntry(
    val sourceDeclaration: KSDeclaration,
    val annotation: KSAnnotation,
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
internal fun processCombineTo(): List<KSAnnotated> =
    with(processContext.logger, processContext.options) {
        val (combineToTargets, invalidCombineToTargets) =
            processContext.resolver
                .getSymbolsWithAnnotation(
                    annotationName = CombineTo::class.fullName,
                ).partition { it.validate() }

        // Group source classes by their target classes
        val targetToSourcesMap = TargetSourcesMapForCombineTo()

        combineToTargets.forEach { target ->
            val sourceDeclaration =
                target.asDeclarationOrReport(annotationName) ?: return@forEach
            val sourceClass =
                sourceDeclaration.resolveClassDeclarationOrReport(
                    annotationName = annotationName,
                    logger = processContext.logger,
                ) ?: return@forEach

            val combineToAnnotations = target.annotationsOf(CombineTo::class)

            // CombineTo.targets: List<KClass<*>>. If any target cannot be resolved to a class, an error
            // has been reported and we skip this source so no partial file is emitted.
            val targetClasses =
                combineToAnnotations.resolveClassListOrReport("targets", annotationName, sourceDeclaration) ?: return@forEach

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
                            "Duplicate target $duplicateNames in @$annotationName of ${sourceClass.fullName}.",
                        solution = "Remove the duplicate target from @$annotationName (list each target at most once).",
                    ).message.orEmpty(),
                    sourceDeclaration,
                )
                return@forEach
            }

            val combineToAnnotation =
                combineToAnnotations.firstOrNull() ?: return@forEach
            val generateSourceAnnotation =
                GenerateSourceAnnotation.CombineTo(annotation = combineToAnnotation).also { gsa ->
                    gsa
                        .validateFunName(
                            generatesMultipleFunctions = targetClasses.size > 1,
                            declarationFullName = sourceClass.fullName,
                            ksNode = sourceDeclaration,
                        ).onInvalid { return@forEach }
                }

            // Warn ONCE per source for @CombineTo.Exclude properties that match no parameter in ANY of
            // this source's target classes. Computed here (per source, union of all targets) rather than
            // inside the per-(target, source) generation loop below — doing it there duplicated the
            // warning once per target and falsely warned for a property matched in a sibling target.
            val allTargetParams = targetClasses.flatMap { it.primaryConstructor?.parameters.orEmpty() }
            sourceClass.getAllProperties().forEach { prop ->
                prop.warnIfSourceExcludeHasNoEffect(allTargetParams, sourceClass, generateSourceAnnotation, processContext.logger)
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
                        it.appendCombineToFunction(
                            primarySource = sourceClass,
                            otherSources = otherSourceClasses,
                            target = targetClass,
                            omitPackages = omitPackagesFor(sourceClass.packageName),
                            generateSourceAnnotation = generateSourceAnnotation,
                        )
                    }
            }
        }

        return invalidCombineToTargets
    }
