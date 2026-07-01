package me.tbsten.cream.test.combineMapping

import me.tbsten.cream.CombineMapping

/**
 * `excludes` on `@CombineMapping`: `propA`'s auto-copy default is dropped, so the generated
 * `copyToExcludesCombinedModel` requires it explicitly (the other matched properties keep their defaults).
 */
data class ExcludesLibA(
    val propA: String,
    val valueA: Int,
)

data class ExcludesLibB(
    val propB: String,
    val valueB: Double,
)

data class ExcludesCombinedModel(
    val propA: String,
    val valueA: Int,
    val propB: String,
    val valueB: Double,
)

@CombineMapping(
    sources = [ExcludesLibA::class, ExcludesLibB::class],
    target = ExcludesCombinedModel::class,
    excludes = ["propA"],
)
private object ExcludesCombineMapping
