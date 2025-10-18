package me.tbsten.cream.test.combineTo.edgeCase

import me.tbsten.cream.CombineTo

// Nullable properties test
@CombineTo(NullableTarget::class)
data class NullableSourceA(
    val nullableProperty: String?,
)

@CombineTo(NullableTarget::class)
data class NullableSourceB(
    val requiredProperty: String,
)

data class NullableTarget(
    val nullableProperty: String?,
    val requiredProperty: String,
)
