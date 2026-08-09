package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSAnnotation
import me.tbsten.cream.CallFrom
import kotlin.reflect.KClass

/**
 * [GenerateSourceAnnotation] for `@CallFrom`.
 *
 * `@CallFrom` is attached to a **function** (not a class); [annotatedDeclaration] is the
 * annotated [com.google.devtools.ksp.symbol.KSFunctionDeclaration]. Its [funNameTemplate] is
 * expanded by `resolveCallFromFunName` in `core/callFrom`.
 *
 * Target-side, like `@CopyFrom`: `@CallFrom.Map` and `@CallFrom.Exclude` sit on the annotated
 * function's VALUE_PARAMETER, so the exclude check needs no matched source property at all.
 */
internal data class CallFromSourceAnnotation(
    override val annotation: KSAnnotation,
) : GenerateSourceAnnotation {
    val findMappedSourceProperty: FindMappedSourceProperty =
        { parameter, source, _ ->
            parameter.findSourcePropertyWithCallFromMapAnnotation(source)
        }

    val isExcluded: IsExcluded =
        { parameter, _, _ ->
            parameter.annotationsOf(CallFrom.Exclude::class).any()
        }

    override val warnedTargetExcludeAnnotation: KClass<out Annotation>
        get() = CallFrom.Exclude::class
}
