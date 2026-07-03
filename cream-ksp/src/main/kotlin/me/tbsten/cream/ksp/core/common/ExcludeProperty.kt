package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import me.tbsten.cream.CombineFrom
import me.tbsten.cream.CombineTo
import me.tbsten.cream.CopyFrom
import me.tbsten.cream.CopyTo
import me.tbsten.cream.CopyToChildren
import me.tbsten.cream.ksp.core.common.annotationsOf
import me.tbsten.cream.ksp.util.ksp.collectConcreteSubclasses
import me.tbsten.cream.ksp.util.ksp.isSealed
import kotlin.reflect.KClass

/**
 * Returns true if the matched auto-copy default should be suppressed for this parameter.
 *
 * annotation-scoped: determined by the [generateSourceAnnotation] type so that
 * @SealedCopy.Exclude and @CopyToChildren.Exclude do not accidentally suppress each
 * other's parameters when both annotations coexist on the same sealed parent.
 *
 * @SealedCopy is handled separately in appendSealedCopyHeader (not via this function).
 */
internal fun KSValueParameter.isExcludedFromCopy(
    matchedProperty: KSPropertyDeclaration?,
    matchedSource: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation,
): Boolean =
    when (generateSourceAnnotation) {
        is GenerateSourceAnnotation.CopyTo ->
            matchedProperty?.isSourcePropertyExcluded(matchedSource, CopyTo.Exclude::class) == true
        is GenerateSourceAnnotation.CopyFrom ->
            annotationsOf(CopyFrom.Exclude::class).any()
        is GenerateSourceAnnotation.CombineTo ->
            matchedProperty?.isSourcePropertyExcluded(matchedSource, CombineTo.Exclude::class) == true
        is GenerateSourceAnnotation.CombineFrom ->
            annotationsOf(CombineFrom.Exclude::class).any()
        is GenerateSourceAnnotation.CopyToChildren ->
            matchedProperty?.annotationsOf(CopyToChildren.Exclude::class)?.any() == true
        // CopyMapping / CombineMapping cannot annotate external classes with @Exclude, so the annotation-level
        // `excludes` names the generated (target-side) parameter whose auto-copy default should be dropped.
        is GenerateSourceAnnotation.CopyMapping ->
            name?.asString() in generateSourceAnnotation.excludedParameterNames
        is GenerateSourceAnnotation.CombineMapping ->
            name?.asString() in generateSourceAnnotation.excludedParameterNames
        // SealedCopy is handled separately in appendSealedCopyHeader (not via this function).
        is GenerateSourceAnnotation.SealedCopy -> false
    }

/**
 * Dual-lookup: checks both the property-site and the ctor-param-site for the Exclude
 * annotation, mirroring the pattern used by FindMatchedProperty for .Map.
 */
internal fun KSPropertyDeclaration.isSourcePropertyExcluded(
    source: KSClassDeclaration,
    excludeClass: KClass<out Annotation>,
): Boolean {
    if (annotationsOf(excludeClass).any()) return true
    val ctorParam =
        source.primaryConstructor
            ?.parameters
            ?.firstOrNull { it.name?.asString() == simpleName.asString() }
    return ctorParam?.annotationsOf(excludeClass)?.any() == true
}

/**
 * Warns when a target-side @Exclude (on a VALUE_PARAMETER) annotates a parameter that
 * has no matched source property — i.e. the Exclude is a no-op because the parameter
 * already had no auto-copy default.
 */
internal fun KSValueParameter.warnIfTargetExcludeHasNoEffect(
    matchedProperty: KSPropertyDeclaration?,
    generateSourceAnnotation: GenerateSourceAnnotation,
    logger: KSPLogger,
) {
    if (matchedProperty != null) return
    val paramName = name?.asString() ?: return
    val hasExclude =
        when (generateSourceAnnotation) {
            is GenerateSourceAnnotation.CopyFrom -> annotationsOf(CopyFrom.Exclude::class).any()
            is GenerateSourceAnnotation.CombineFrom -> annotationsOf(CombineFrom.Exclude::class).any()
            // Source-side and sealed-side annotations are warned elsewhere.
            is GenerateSourceAnnotation.CopyTo,
            is GenerateSourceAnnotation.CombineTo,
            is GenerateSourceAnnotation.CopyToChildren,
            is GenerateSourceAnnotation.SealedCopy,
            is GenerateSourceAnnotation.CopyMapping,
            is GenerateSourceAnnotation.CombineMapping,
            -> false
        }
    if (hasExclude) {
        logger.warn("@Exclude on '$paramName' has no effect: not a matched property", this)
    }
}

/**
 * Warns for each `@CopyMapping` / `@CombineMapping` `excludes` entry that names no auto-defaulted parameter of
 * [targetClass]'s generated copy function(s). An entry that matches no such parameter drops no default, so it is a
 * no-op — mirroring the unmatched-`@Exclude` warning. Non-mapping annotations are ignored.
 *
 * The candidate parameters mirror what generation actually emits (see [generatedCopyParameters]): a class fans out
 * over **every** constructor, and a sealed target fans out over its concrete leaves — so the check is correct for
 * multi-constructor and sealed targets, not just a single primary constructor.
 *
 * For `@CopyMapping(canReverse = true)` the reverse function excludes via the source-side translation of each
 * entry (see [GenerateSourceAnnotation.CopyMapping.excludedParameterNames]), so an entry that misses every
 * forward parameter can still drop a reverse default. Such an entry is not a no-op, so it must not warn —
 * keeping the warning consistent with what the reverse generation actually does.
 */
internal fun GenerateSourceAnnotation.warnUnmatchedExcludes(
    targetClass: KSClassDeclaration,
    sources: List<KSClassDeclaration>,
    node: KSNode,
    logger: KSPLogger,
) {
    val gsa = this
    // `excludes` as written (target-side names), plus the reversed-direction GSA when `canReverse`
    // also generates a reverse function (null otherwise).
    val (excludes, reversedGsa) =
        when (gsa) {
            is GenerateSourceAnnotation.CopyMapping ->
                gsa.excludedParameterNames to (if (gsa.canReverse) gsa.copy(reversed = true) else null)
            is GenerateSourceAnnotation.CombineMapping -> gsa.excludedParameterNames to null
            else -> return
        }
    if (excludes.isEmpty()) return
    val autoDefaultedNames =
        targetClass
            .generatedCopyParameters()
            .filter { param -> sources.any { source -> param.findMatchedProperty(source, gsa) != null } }
            .mapNotNull { it.name?.asString() }
            .toSet()
    // The reverse function copies target -> source, so its auto-defaulted parameters come from the *source*
    // classes' constructors matched against [targetClass]. `reversedGsa.excludedParameterNames` is the
    // entry-by-entry source-side translation of `excludes` (same index = same entry).
    val reverseExcludes = reversedGsa?.excludedParameterNames
    val reverseAutoDefaultedNames =
        reversedGsa
            ?.let { reversed ->
                sources
                    .flatMap { forwardSource ->
                        forwardSource
                            .generatedCopyParameters()
                            .filter { param -> param.findMatchedProperty(targetClass, reversed) != null }
                            .mapNotNull { param -> param.name?.asString() }
                    }.toSet()
            }.orEmpty()
    excludes.forEachIndexed { index, name ->
        val effectiveForward = name in autoDefaultedNames
        val effectiveReverse = reverseExcludes != null && reverseExcludes[index] in reverseAutoDefaultedNames
        if (!effectiveForward && !effectiveReverse) {
            logger.warn("excludes entry '$name' has no effect: not an auto-defaulted parameter", node)
        }
    }
}

/**
 * The value parameters across every constructor a copy function is generated for, matching the generation dispatch
 * (`appendCopyFunction`): a sealed target fans out over its concrete leaves' constructors, an `object` has none, and
 * any other class fans out over all of its own constructors.
 */
private fun KSClassDeclaration.generatedCopyParameters(): List<KSValueParameter> =
    when {
        isSealed() -> collectConcreteSubclasses().flatMap { leaf -> leaf.getConstructors().flatMap { it.parameters } }.toList()
        classKind == ClassKind.OBJECT -> emptyList()
        else -> getConstructors().flatMap { it.parameters }.toList()
    }

/**
 * Warns when a source-side @Exclude (on a source property) never suppresses any
 * auto-copy default — i.e. none of the target parameters matched this source property.
 */
internal fun KSPropertyDeclaration.warnIfSourceExcludeHasNoEffect(
    targetParameters: List<KSValueParameter>,
    source: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation,
    logger: KSPLogger,
) {
    val excludeClass =
        when (generateSourceAnnotation) {
            is GenerateSourceAnnotation.CopyTo -> CopyTo.Exclude::class
            is GenerateSourceAnnotation.CombineTo -> CombineTo.Exclude::class
            // Target-side and sealed-side annotations are warned elsewhere.
            is GenerateSourceAnnotation.CopyFrom,
            is GenerateSourceAnnotation.CombineFrom,
            is GenerateSourceAnnotation.CopyToChildren,
            is GenerateSourceAnnotation.SealedCopy,
            is GenerateSourceAnnotation.CopyMapping,
            is GenerateSourceAnnotation.CombineMapping,
            -> return
        }
    if (!isSourcePropertyExcluded(source, excludeClass)) return
    val propName = simpleName.asString()
    val isMatched =
        targetParameters.any { param ->
            param.findMatchedProperty(source, generateSourceAnnotation)?.simpleName?.asString() == propName
        }
    if (!isMatched) {
        logger.warn("@Exclude on '$propName' has no effect: not a matched property", this)
    }
}
