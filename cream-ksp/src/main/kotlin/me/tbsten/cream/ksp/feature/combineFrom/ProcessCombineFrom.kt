package me.tbsten.cream.ksp.feature.combineFrom

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.validate
import me.tbsten.cream.CombineFrom
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
import me.tbsten.cream.ksp.core.common.reportCreamError
import me.tbsten.cream.ksp.core.common.resolveClassDeclarationOrReport
import me.tbsten.cream.ksp.core.common.resolveClassListOrReport
import me.tbsten.cream.ksp.core.common.resolveFunName
import me.tbsten.cream.ksp.core.common.underPackageName
import me.tbsten.cream.ksp.util.lines
import me.tbsten.cream.ksp.util.with

private val annotationName = CombineFrom::class.simpleName!!

/**
 * One resolved `@CombineFrom` occurrence: its raw [annotation], the (deduped) [sources] it combines
 * ([primarySource] = the extension-function receiver), and the [funName] those resolve to.
 * `@CombineFrom` is `@Repeatable` and every occurrence becomes its own combine function, so each is
 * processed independently.
 */
private data class CombineFromOccurrence(
    val annotation: KSAnnotation,
    val sources: List<KSClassDeclaration>,
    val primarySource: KSClassDeclaration,
    val funName: String,
) {
    // Two occurrences collide as a redeclaration only when they emit the SAME overload: identical
    // resolved funName AND identical ordered source list (the receiver = sources[0] and the leading
    // parameter types = sources[1..] fix the signature; the target constructor params are constant).
    // Different source sets coexist as overloads even under one name. Keyed on the source
    // declarations themselves (not their full names) — the same equality the source dedupe relies on.
    val overloadKey: Any
        get() = funName to sources
}

context(processContext: ProcessContext)
internal fun processCombineFrom(): List<KSAnnotated> =
    with(processContext.logger, processContext.options) {
        val (combineFromTargets, invalidCombineFromTargets) =
            processContext.resolver
                .getSymbolsWithAnnotation(
                    annotationName = CombineFrom::class.fullName,
                ).partition { it.validate() }

        combineFromTargets.forEach { target ->
            val targetDeclaration =
                target.asDeclarationOrReport(annotationName) ?: return@forEach
            val targetClass =
                targetDeclaration.resolveClassDeclarationOrReport(
                    annotationName = annotationName,
                    logger = processContext.logger,
                ) ?: return@forEach

            val combineFromAnnotations = target.annotationsOf(CombineFrom::class).toList()
            if (combineFromAnnotations.isEmpty()) {
                // Defensive: getSymbolsWithAnnotation matched but no @CombineFrom occurrence could be
                // resolved (e.g. a broken classpath entry).
                return@forEach
            }

            // Resolve every occurrence independently — sources are NOT flattened across occurrences.
            // @CombineFrom is @Repeatable and each occurrence is its own combine function (a different
            // source set yields different parameter types, so same-named functions coexist as
            // overloads). Mirrors the per-occurrence @SealedCopy model.
            val resolvedOccurrences =
                combineFromAnnotations.map { annotation ->
                    // Dedupe sources WITHIN one occurrence: @CombineFrom(A::class, A::class) would emit
                    // two identically named parameters ("Conflicting declarations") and re-list the same
                    // source in KDoc.
                    val sources =
                        sequenceOf(annotation)
                            .resolveClassListOrReport("sources", annotationName, targetDeclaration)
                            ?.distinct()
                            ?: return@map null
                    val primarySource = sources.firstOrNull()
                    if (primarySource == null) {
                        processContext.logger.reportCombineFromNoSources(targetDeclaration)
                        return@map null
                    }
                    CombineFromOccurrence(
                        annotation = annotation,
                        sources = sources,
                        primarySource = primarySource,
                        funName =
                            resolveFunName(
                                funNameTemplate = annotation.funNameTemplate(),
                                source = primarySource,
                                target = targetClass,
                                options = processContext.options,
                            ),
                    )
                }
            // Any occurrence that failed to resolve already reported a clean error; skip the whole
            // target so no partial file is emitted.
            if (resolvedOccurrences.any { it == null }) return@forEach
            val occurrences = resolvedOccurrences.filterNotNull()

            // All occurrences are written to one file, so two that would emit the same overload
            // (same resolved funName + same sources) are a redeclaration — reject with a clean cream
            // error rather than letting it fail at the user's compiler. Mirrors @SealedCopy's
            // duplicate-funName guard.
            val duplicateOverload =
                occurrences
                    .groupBy { it.overloadKey }
                    .values
                    .firstOrNull { it.size > 1 }
                    ?.first()
            if (duplicateOverload != null) {
                processContext.logger.reportCombineFromDuplicateOverload(
                    targetDeclaration,
                    targetClass,
                    duplicateOverload,
                )
                return@forEach
            }

            processContext.codeGenerator
                .createNewKotlinFile(
                    dependencies = Dependencies(aggregating = true, targetDeclaration.containingFile!!),
                    packageName = targetClass.packageName,
                    fileName = "CombineFrom__${targetClass.underPackageName}",
                ) {
                    occurrences.forEach { occurrence ->
                        val primarySource = occurrence.primarySource
                        val otherSources = occurrence.sources.drop(1)

                        // Pass the raw per-occurrence annotation: @CombineFrom is @Repeatable and each
                        // occurrence is its own combine function, so GSA must read kdoc / visibility /
                        // funName from *this* occurrence rather than a cross-occurrence merge.
                        it.appendCombineToFunction(
                            primarySource = primarySource,
                            otherSources = otherSources,
                            target = targetClass,
                            omitPackages = omitPackagesFor(primarySource.packageName),
                            generateSourceAnnotation = GenerateSourceAnnotation.CombineFrom(annotation = occurrence.annotation),
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
            message = "@$annotationName requires at least one source class.",
            solution = "Specify at least one source class in @$annotationName.sources of ${targetDeclaration.fullName}.",
        ),
        targetDeclaration,
    )
}

private fun KSPLogger.reportCombineFromDuplicateOverload(
    targetDeclaration: KSDeclaration,
    targetClass: KSClassDeclaration,
    duplicate: CombineFromOccurrence,
) {
    // Spell out the colliding overload so the user can see WHICH function is duplicated: the
    // extension receiver (sources[0]) and the leading parameter types (sources[1..]).
    val receiver =
        duplicate.sources
            .first()
            .simpleName
            .asString()
    val parameterTypes = duplicate.sources.drop(1).joinToString { it.simpleName.asString() }
    val overload = "$receiver.${duplicate.funName}($parameterTypes)"
    reportCreamError(
        InvalidCreamUsageException(
            message =
                lines(
                    "@$annotationName on ${targetClass.fullName} generates the same overload more than once: $overload.",
                    "Stacked @$annotationName annotations are written to one file, so each must produce a distinct overload.",
                ),
            solution =
                lines(
                    "Give one of the duplicate @$annotationName a distinct funName, or change its sources.",
                ),
        ),
        targetDeclaration,
    )
}
