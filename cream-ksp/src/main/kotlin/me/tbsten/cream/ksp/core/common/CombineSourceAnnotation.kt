package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import me.tbsten.cream.CombineFrom
import me.tbsten.cream.CombineTo
import kotlin.reflect.KClass

/**
 * [GenerateSourceAnnotation] for `@CombineTo`.
 *
 * The combine-side mirror of [CopyToSourceAnnotation]: `@CombineTo.Map` and `@CombineTo.Exclude`
 * are read from the SOURCE class at the property site first, then at the matching
 * primary-constructor parameter.
 */
internal data class CombineToSourceAnnotation(
    override val annotation: KSAnnotation,
) : GenerateSourceAnnotation {
    override fun findMappedSourceProperty(
        parameter: KSValueParameter,
        source: KSClassDeclaration,
        parameterName: String,
    ): KSPropertyDeclaration? = parameter.findSourcePropertyWithCombineToMapAnnotation(source, parameterName)

    override fun isExcluded(
        parameter: KSValueParameter,
        matchedProperty: KSPropertyDeclaration?,
        matchedSource: KSClassDeclaration,
    ): Boolean = matchedProperty?.isSourcePropertyExcluded(matchedSource, CombineTo.Exclude::class) == true

    override val warnedSourceExcludeAnnotation: KClass<out Annotation>
        get() = CombineTo.Exclude::class
}

/**
 * [GenerateSourceAnnotation] for `@CombineFrom`.
 *
 * `@CombineFrom.Map` accepts BOTH placements — on the target parameter naming source properties,
 * or on the source side naming target parameters — so [findMappedSourceProperty] tries the target
 * site first and falls back to the source site. `@CombineFrom.Exclude` is target-side only.
 *
 * `@CombineFrom` is `@Repeatable`; each occurrence generates its own combine function, so KDoc /
 * visibility / funName all derive from *that* occurrence's raw [annotation] (no cross-occurrence
 * merge).
 */
internal data class CombineFromSourceAnnotation(
    override val annotation: KSAnnotation,
) : GenerateSourceAnnotation {
    override fun findMappedSourceProperty(
        parameter: KSValueParameter,
        source: KSClassDeclaration,
        parameterName: String,
    ): KSPropertyDeclaration? =
        parameter.findSourcePropertyWithCombineFromMapAnnotationOnTarget(source)
            ?: parameter.findSourcePropertyWithCombineFromMapAnnotationOnSource(source, parameterName)

    override fun isExcluded(
        parameter: KSValueParameter,
        matchedProperty: KSPropertyDeclaration?,
        matchedSource: KSClassDeclaration,
    ): Boolean = parameter.annotationsOf(CombineFrom.Exclude::class).any()

    override val warnedTargetExcludeAnnotation: KClass<out Annotation>
        get() = CombineFrom.Exclude::class
}
