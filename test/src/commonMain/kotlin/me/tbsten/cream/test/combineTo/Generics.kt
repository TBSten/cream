package me.tbsten.cream.test.combineTo

import me.tbsten.cream.CombineTo

// Generics test
@CombineTo(GenericTarget::class)
data class GenericSourceA<T>(
    val genericProperty: T,
)

@CombineTo(GenericTarget::class)
data class GenericSourceB(
    val normalProperty: String,
)

data class GenericTarget<T>(
    val genericProperty: T,
    val normalProperty: String,
)
