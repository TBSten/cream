package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.util.ksp.collectConcreteSubclasses
import me.tbsten.cream.ksp.util.ksp.isSealed
import kotlin.reflect.KClass

/**
 * Returns true if the matched auto-copy default should be suppressed for this parameter.
 *
 * annotation-scoped: each [GenerateSourceAnnotation] answers for its own `@Exclude` semantics via
 * [GenerateSourceAnnotation.isExcluded], so that e.g. @SealedCopy.Exclude and
 * @CopyToChildren.Exclude do not accidentally suppress each other's parameters when both
 * annotations coexist on the same sealed parent.
 *
 * @SealedCopy is handled separately in appendSealedCopyHeader (not via this function).
 */
internal fun KSValueParameter.isExcludedFromCopy(
    matchedProperty: KSPropertyDeclaration?,
    matchedSource: KSClassDeclaration,
    isExcluded: IsExcluded,
): Boolean = isExcluded(this, matchedProperty, matchedSource)

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
 * Warns when a target-side @Exclude (on a VALUE_PARAMETER) annotates a parameter that has no
 * candidate auto-copy default — i.e. the Exclude is a no-op because the parameter was required
 * anyway. [hasCandidateDefault] must be `true` when the parameter matched a source property OR
 * resolved a value-class conversion ([findValueClassConversion]): an Exclude that suppresses a
 * conversion default IS effective and must not be warned about.
 *
 * Only annotations that name a [GenerateSourceAnnotation.warnedTargetExcludeAnnotation] are
 * checked; source-side and sealed-side annotations are warned elsewhere.
 */
internal fun KSValueParameter.warnIfTargetExcludeHasNoEffect(
    hasCandidateDefault: Boolean,
    generateSourceAnnotation: GenerateSourceAnnotation,
    logger: KSPLogger,
) {
    if (hasCandidateDefault) return
    val paramName = name?.asString() ?: return
    val excludeClass = generateSourceAnnotation.warnedTargetExcludeAnnotation ?: return
    if (annotationsOf(excludeClass).any()) {
        logger.warn("@Exclude on '$paramName' has no effect: not a matched property", this)
    }
}

/**
 * Warns when a source-side @Exclude (on a source property) never suppresses any auto-copy
 * default — i.e. none of the target parameters matched this source property, neither as a
 * type-compatible match nor as a value-class conversion ([findValueClassConversion], whose
 * default the Exclude also suppresses).
 *
 * Only annotations that name a [GenerateSourceAnnotation.warnedSourceExcludeAnnotation] are
 * checked; target-side and sealed-side annotations are warned elsewhere. [findMappedSourceProperty]
 * must be the same one the generator uses, so "matched" here means exactly what it means there.
 */
context(options: CreamOptions)
internal fun KSPropertyDeclaration.warnIfSourceExcludeHasNoEffect(
    targetParameters: List<KSValueParameter>,
    source: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation,
    findMappedSourceProperty: FindMappedSourceProperty,
    logger: KSPLogger,
) {
    val excludeClass = generateSourceAnnotation.warnedSourceExcludeAnnotation ?: return
    if (!isSourcePropertyExcluded(source, excludeClass)) return
    val propName = simpleName.asString()
    val isMatched =
        targetParameters.any { param ->
            // Mirrors the emission logic: the conversion only supplies a default when the
            // parameter has NO type-compatible match, so it only makes an Exclude effective then.
            val matched = param.findMatchedProperty(source, findMappedSourceProperty)
            when {
                matched != null -> matched.simpleName.asString() == propName
                else ->
                    param
                        .findValueClassConversion(source, findMappedSourceProperty)
                        ?.sourceProperty
                        ?.simpleName
                        ?.asString() == propName
            }
        }
    if (!isMatched) {
        logger.warn("@Exclude on '$propName' has no effect: not a matched property", this)
    }
}

/**
 * One generated direction of a `@CopyMapping` / `@CombineMapping`: the function(s) from [sources]
 * into [targetClass], generated under [generateSourceAnnotation] with [findMappedSourceProperty].
 * [excludeNames] holds this direction's (possibly Map-translated) spelling of each user-written
 * `excludes` entry, index-aligned with the original `excludes` list.
 */
internal class MappingExcludesDirection(
    val sources: List<KSClassDeclaration>,
    val targetClass: KSClassDeclaration,
    val generateSourceAnnotation: GenerateSourceAnnotation,
    val findMappedSourceProperty: FindMappedSourceProperty,
    val excludeNames: List<String>,
)

/**
 * Warns when a `@CopyMapping` / `@CombineMapping` `excludes` entry never suppresses any
 * auto-copy default — i.e. no generated parameter carrying that (target-side) name has a matched
 * source property. Mirrors the unmatched `@Exclude` warnings above; anchored at the annotated
 * mapping holder because the mapped external classes carry no annotation to point at.
 *
 * Evaluated once per mapping annotation (called from the mapping features, not per generated
 * function), so neither the `canReverse` reverse pass nor a sealed target's per-leaf fan-out can
 * duplicate the warning, and the message always names the entry as the user wrote it
 * ([originalExcludes]) — never its Map-translated reverse spelling. An entry is effective when
 * its direction-local spelling suppresses a default on any constructor parameter of any concrete
 * target in any direction (every transitive concrete leaf when the target is sealed; an `object`
 * target has no parameters, so its entries always warn).
 */
internal fun warnIfMappingExcludesHaveNoEffect(
    originalExcludes: List<String>,
    directions: List<MappingExcludesDirection>,
    logger: KSPLogger,
) {
    val anchor =
        directions.firstOrNull()?.generateSourceAnnotation?.annotatedDeclaration ?: return
    originalExcludes.forEachIndexed { index, entry ->
        val suppressesDefault =
            directions.any { direction ->
                val localName = direction.excludeNames.getOrNull(index) ?: entry
                direction.concreteTargets().any { concreteTarget ->
                    concreteTarget.getConstructors().any { constructor ->
                        constructor.parameters.any { parameter ->
                            parameter.name?.asString() == localName &&
                                direction.sources.any { source ->
                                    parameter.findMatchedProperty(source, direction.findMappedSourceProperty) != null
                                }
                        }
                    }
                }
            }
        if (!suppressesDefault) {
            logger.warn(
                "excludes entry '$entry' has no effect: not a matched property",
                anchor,
            )
        }
    }
}

private fun MappingExcludesDirection.concreteTargets(): Sequence<KSClassDeclaration> =
    if (targetClass.isSealed()) {
        targetClass.collectConcreteSubclasses()
    } else {
        sequenceOf(targetClass)
    }
