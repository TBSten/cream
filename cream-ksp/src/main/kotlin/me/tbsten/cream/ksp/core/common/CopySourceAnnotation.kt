package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import me.tbsten.cream.CopyFrom
import me.tbsten.cream.CopyTo
import me.tbsten.cream.CopyToChildren
import kotlin.reflect.KClass

/**
 * [GenerateSourceAnnotation] for `@CopyTo`.
 *
 * Source-side: both `@CopyTo.Map` and `@CopyTo.Exclude` are read from the SOURCE class, at the
 * property site first and then at the matching primary-constructor parameter (see
 * [findSourcePropertyWithCopyToMapAnnotation] for why the second site is needed).
 */
internal data class CopyToSourceAnnotation(
    override val annotation: KSAnnotation,
) : GenerateSourceAnnotation {
    override fun findMappedSourceProperty(
        parameter: KSValueParameter,
        source: KSClassDeclaration,
        parameterName: String,
    ): KSPropertyDeclaration? = parameter.findSourcePropertyWithCopyToMapAnnotation(source, parameterName)

    override fun isExcluded(
        parameter: KSValueParameter,
        matchedProperty: KSPropertyDeclaration?,
        matchedSource: KSClassDeclaration,
    ): Boolean = matchedProperty?.isSourcePropertyExcluded(matchedSource, CopyTo.Exclude::class) == true

    override val warnedSourceExcludeAnnotation: KClass<out Annotation>
        get() = CopyTo.Exclude::class
}

/**
 * [GenerateSourceAnnotation] for `@CopyFrom`.
 *
 * Target-side: `@CopyFrom.Map` and `@CopyFrom.Exclude` sit on the TARGET constructor parameter, so
 * the exclude check needs no matched source property at all.
 */
internal data class CopyFromSourceAnnotation(
    override val annotation: KSAnnotation,
) : GenerateSourceAnnotation {
    override fun findMappedSourceProperty(
        parameter: KSValueParameter,
        source: KSClassDeclaration,
        parameterName: String,
    ): KSPropertyDeclaration? = parameter.findSourcePropertyWithCopyFromMapAnnotation(source)

    override fun isExcluded(
        parameter: KSValueParameter,
        matchedProperty: KSPropertyDeclaration?,
        matchedSource: KSClassDeclaration,
    ): Boolean = parameter.annotationsOf(CopyFrom.Exclude::class).any()

    override val warnedTargetExcludeAnnotation: KClass<out Annotation>
        get() = CopyFrom.Exclude::class
}

/**
 * [GenerateSourceAnnotation] for `@CopyToChildren`.
 *
 * Source-side, but PROPERTY-site only: a sealed parent's abstract property has no constructor
 * parameter, so neither `@CopyToChildren.Map` nor `@CopyToChildren.Exclude` needs the second
 * lookup the `@CopyTo` pair does.
 *
 * `@CopyToChildren` is also the only cream annotation with a `notCopyToObject` argument, controlling
 * whether object subtypes of the sealed hierarchy get a copy function. The argument reads as `null`
 * when the user left it unset, and [skipsObjectTarget] then defers to the `cream.notCopyToObject`
 * option.
 *
 * Its unmatched `@Exclude` is warned elsewhere, so [warnedSourceExcludeAnnotation] stays `null`.
 */
internal data class CopyToChildrenSourceAnnotation(
    override val annotation: KSAnnotation,
) : GenerateSourceAnnotation {
    private val notCopyToObject: Boolean? get() = annotation.notCopyToObject()

    override fun findMappedSourceProperty(
        parameter: KSValueParameter,
        source: KSClassDeclaration,
        parameterName: String,
    ): KSPropertyDeclaration? = parameter.findSourcePropertyWithCopyToChildrenMapAnnotation(source, parameterName)

    override fun isExcluded(
        parameter: KSValueParameter,
        matchedProperty: KSPropertyDeclaration?,
        matchedSource: KSClassDeclaration,
    ): Boolean = matchedProperty?.annotationsOf(CopyToChildren.Exclude::class)?.any() == true

    override fun skipsObjectTarget(notCopyToObjectOption: Boolean): Boolean = notCopyToObject ?: notCopyToObjectOption
}

/**
 * [GenerateSourceAnnotation] for `@SealedCopy`.
 *
 * Every generation rule keeps its default: `@SealedCopy` never resolves defaults through
 * [findMatchedProperty] (its `Map` has Via-parameter semantics handled in `core/sealedCopy`), and
 * its `@Exclude` is applied in `appendSealedCopyHeader` rather than through [isExcluded].
 */
internal data class SealedCopySourceAnnotation(
    override val annotation: KSAnnotation,
) : GenerateSourceAnnotation
