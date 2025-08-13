package me.tbsten.cream.test.mutableCopyTo.edgeCase

import me.tbsten.cream.MutableCopyTo

// Edge case: Nullable properties
@MutableCopyTo(NullableMutableTarget::class)
data class NullableMutableSource(
    val nullableProp: String?,
    val nonNullProp: String,
)

data class NullableMutableTarget(
    var nullableProp: String?,
    var nonNullProp: String,
    var additionalProp: Int?,
)

// Edge case: Only some properties match
@MutableCopyTo(PartialMatchTarget::class)
data class PartialMatchSource(
    val matchingProp1: String,
    val matchingProp2: Int,
    val sourceOnlyProp: Boolean,
)

data class PartialMatchTarget(
    var matchingProp1: String,
    var matchingProp2: Int,
    var targetOnlyProp: Double,
)
