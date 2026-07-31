package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSAnnotation

/**
 * [GenerateSourceAnnotation] for `@CopyMapping`.
 *
 * Library-to-library, so neither the source nor the target class can carry a cream annotation: the
 * remappings arrive as this annotation's own `properties = [Map(source, target)]` arguments, and
 * the annotation-level `excludes` names the generated (target-side) parameter instead of a
 * property to annotate. That also means there is no `@Exclude` placement to warn about, so both
 * `warned…ExcludeAnnotation` properties keep their `null` default; ineffective `excludes` entries
 * are reported by [warnIfMappingExcludesHaveNoEffect] instead.
 *
 * [reversed] swaps each `(source -> target)` pair for the `canReverse` reverse-direction function,
 * which shares this same [annotation].
 */
internal data class CopyMappingSourceAnnotation(
    override val annotation: KSAnnotation,
    val reversed: Boolean = false,
) : GenerateSourceAnnotation {
    val propertyMappings: List<Pair<String, String>>
        get() =
            annotation.extractPropertyMappings().let { pairs ->
                if (reversed) pairs.map { (source, target) -> target to source } else pairs
            }

    /**
     * Generated (target-side) parameter names whose auto-copy default is dropped. For the
     * [reversed] function each entry is translated through the property mappings: an entry naming
     * the `target` of a `Map(source, target)` pair excludes the reverse function's source-side
     * parameter; entries without a mapping (same-named shared properties) apply as-is.
     */
    val excludes: List<String>
        get() {
            val names = annotation.extractExcludes()
            if (!reversed) return names
            val mappings = annotation.extractPropertyMappings()
            return names.map { name ->
                mappings.firstOrNull { (_, target) -> target == name }?.first ?: name
            }
        }

    val findMappedSourceProperty: FindMappedSourceProperty =
        { parameter, source, parameterName ->
            parameter.findSourcePropertyWithPropertyMappings(source, parameterName, propertyMappings)
        }

    val isExcluded: IsExcluded =
        { parameter, _, _ ->
            parameter.name?.asString() in excludes
        }
}

/**
 * [GenerateSourceAnnotation] for `@CombineMapping`.
 *
 * The combine-side mirror of [CopyMappingSourceAnnotation], minus the reverse direction:
 * `@CombineMapping` combines N sources into one target and has no `canReverse`.
 */
internal data class CombineMappingSourceAnnotation(
    override val annotation: KSAnnotation,
) : GenerateSourceAnnotation {
    val propertyMappings: List<Pair<String, String>>
        get() = annotation.extractPropertyMappings()

    /** Generated (target-side) parameter names whose auto-copy default is dropped. */
    val excludes: List<String>
        get() = annotation.extractExcludes()

    val findMappedSourceProperty: FindMappedSourceProperty =
        { parameter, source, parameterName ->
            parameter.findSourcePropertyWithPropertyMappings(source, parameterName, propertyMappings)
        }

    val isExcluded: IsExcluded =
        { parameter, _, _ ->
            parameter.name?.asString() in excludes
        }

    /**
     * The combine family has no per-annotation object control, so the `cream.notCopyToObject`
     * option decides directly. (The copy family differs: only `@CopyToChildren` consults it, and
     * every other copy annotation names its possibly-`object` target explicitly.)
     */
    override fun skipsObjectTarget(notCopyToObjectOption: Boolean): Boolean = notCopyToObjectOption
}
