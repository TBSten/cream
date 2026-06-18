package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import me.tbsten.cream.CombineFrom
import me.tbsten.cream.CombineTo
import me.tbsten.cream.CopyFrom
import me.tbsten.cream.CopyTo
import me.tbsten.cream.CopyToChildren
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.core.common.annotationsOf
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
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
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
        // SealedCopy is handled separately in appendSealedCopyHeader (not via this function).
        // CopyMapping / CombineMapping: library-to-library, @Exclude not applicable.
        is GenerateSourceAnnotation.SealedCopy,
        is GenerateSourceAnnotation.CopyMapping,
        is GenerateSourceAnnotation.CombineMapping,
        -> false
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
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
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
 * Warns when a source-side @Exclude (on a source property) never suppresses any
 * auto-copy default — i.e. none of the target parameters matched this source property.
 */
internal fun KSPropertyDeclaration.warnIfSourceExcludeHasNoEffect(
    targetParameters: List<KSValueParameter>,
    source: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
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
