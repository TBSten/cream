package me.tbsten.cream.test.copyMapping

import me.tbsten.cream.CopyMapping

/**
 * `excludes` is the annotation-level equivalent of `@Exclude` for library-to-library mapping: `shareProp`'s
 * auto-copy default is dropped, so the generated `copyToExcludesLibY` requires it explicitly.
 */
data class ExcludesLibX(
    val shareProp: String,
    val xProp: Int,
)

data class ExcludesLibY(
    val shareProp: String,
    val yProp: Int,
)

@CopyMapping(
    source = ExcludesLibX::class,
    target = ExcludesLibY::class,
    excludes = ["shareProp"],
)
private object ExcludesMapping
