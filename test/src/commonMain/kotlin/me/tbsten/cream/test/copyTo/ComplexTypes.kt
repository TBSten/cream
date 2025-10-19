package me.tbsten.cream.test.copyTo

import me.tbsten.cream.CopyTo

// Complex types
@CopyTo(ComplexTypeTarget::class)
data class ComplexTypeSource(
    val stringList: List<String>,
    val intMap: Map<String, Int>,
    val nullableSet: Set<Double>?,
)

data class ComplexTypeTarget(
    val stringList: List<String>,
    val intMap: Map<String, Int>,
    val nullableSet: Set<Double>?,
    val newProperty: String,
)
