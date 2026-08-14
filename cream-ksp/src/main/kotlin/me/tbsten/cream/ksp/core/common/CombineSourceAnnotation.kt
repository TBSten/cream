package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSAnnotation
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
    val findMappedSourceProperty: FindMappedSourceProperty =
        { parameter, source, parameterName, accepts ->
            parameter.findSourcePropertyWithCombineToMapAnnotation(source, parameterName, accepts)
        }

    val isExcluded: IsExcluded =
        { _, matchedProperty, matchedSource ->
            matchedProperty?.isSourcePropertyExcluded(matchedSource, CombineTo.Exclude::class) == true
        }

    override val warnedSourceExcludeAnnotation: KClass<out Annotation>
        get() = CombineTo.Exclude::class

    /**
     * The combine family has no per-annotation object control, so the `cream.notCopyToObject`
     * option decides directly. (The copy family differs: only `@CopyToChildren` consults it, and
     * every other copy annotation names its possibly-`object` target explicitly.)
     */
    override fun skipsObjectTarget(notCopyToObjectOption: Boolean): Boolean = notCopyToObjectOption
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
    val findMappedSourceProperty: FindMappedSourceProperty =
        { parameter, source, parameterName, accepts ->
            parameter.findSourcePropertyWithCombineFromMapAnnotationOnTarget(source, accepts)
                ?: parameter.findSourcePropertyWithCombineFromMapAnnotationOnSource(source, parameterName, accepts)
        }

    val isExcluded: IsExcluded =
        { parameter, _, _ ->
            parameter.annotationsOf(CombineFrom.Exclude::class).any()
        }

    override val warnedTargetExcludeAnnotation: KClass<out Annotation>
        get() = CombineFrom.Exclude::class

    /**
     * The combine family has no per-annotation object control, so the `cream.notCopyToObject`
     * option decides directly. (The copy family differs: only `@CopyToChildren` consults it, and
     * every other copy annotation names its possibly-`object` target explicitly.)
     */
    override fun skipsObjectTarget(notCopyToObjectOption: Boolean): Boolean = notCopyToObjectOption
}
