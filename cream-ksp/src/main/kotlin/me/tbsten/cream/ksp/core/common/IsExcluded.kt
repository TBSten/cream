package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter

/**
 * Decides whether the auto-copy default matched for a TARGET constructor parameter is suppressed —
 * cream's `@Exclude` annotations, or whatever a non-annotation caller decides. A suppressed
 * parameter keeps its type but loses its `= ...` default, so it becomes required at the call site.
 *
 * Keeping this per-generation is what stops e.g. `@SealedCopy.Exclude` and `@CopyToChildren.Exclude`
 * from suppressing each other's parameters when both annotations coexist on the same sealed parent.
 *
 * A standalone type rather than a [GenerateSourceAnnotation] member so the `core` generators take
 * it as an ordinary parameter: a caller driving generation from something other than an annotation
 * supplies a lambda instead of implementing an interface.
 *
 * Parameters, in order:
 *  - `parameter` — the target constructor parameter being emitted,
 *  - `matchedProperty` — the source property [findMatchedProperty] resolved, `null` when nothing
 *    matched,
 *  - `matchedSource` — the source class `matchedProperty` came from.
 */
internal typealias IsExcluded = (
    parameter: KSValueParameter,
    matchedProperty: KSPropertyDeclaration?,
    matchedSource: KSClassDeclaration,
) -> Boolean
