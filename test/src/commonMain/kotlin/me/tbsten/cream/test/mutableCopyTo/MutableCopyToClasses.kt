package me.tbsten.cream.test.mutableCopyTo

import me.tbsten.cream.MutableCopyTo

@MutableCopyTo(MutableTarget::class)
data class MutableSource(
    val sourceProp1: String,
    val sourceProp2: Int,
    val sharedProp: String,
)

data class MutableTarget(
    var sourceProp1: String,
    var sourceProp2: Int,
    var sharedProp: String,
    var targetOnlyProp: String,
)

// Edge case: different property types
@MutableCopyTo(ComplexMutableTarget::class)
data class ComplexMutableSource(
    val name: String,
    val count: Int,
    val enabled: Boolean,
)

data class ComplexMutableTarget(
    var name: String,
    var count: Int,
    var enabled: Boolean,
    var description: String,
    var metadata: Map<String, Any>,
)
