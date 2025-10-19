package me.tbsten.cream.test.copyFrom

import me.tbsten.cream.CopyFrom

// Complex types
@CopyFrom(ComplexTypeSource::class)
data class ComplexTypeTarget(
    val stringList: List<String>,
    val intMap: Map<String, Int>,
    val nullableSet: Set<Double>?,
    val newProperty: String,
)

data class ComplexTypeSource(
    val stringList: List<String>,
    val intMap: Map<String, Int>,
    val nullableSet: Set<Double>?,
)
