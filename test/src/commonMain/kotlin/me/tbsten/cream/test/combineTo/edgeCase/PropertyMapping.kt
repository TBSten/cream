package me.tbsten.cream.test.combineTo.edgeCase

import me.tbsten.cream.CombineTo

// Test case 1: Simple property name mapping

/**
 * Test property mapping with @CombineTo.Map annotation
 */
@CombineTo(MappedTarget::class)
data class MappingSourceA(
    @CombineTo.Map("targetPropertyA")
    val sourcePropertyA: String,
)

@CombineTo(MappedTarget::class)
data class MappingSourceB(
    @CombineTo.Map("targetPropertyB")
    val sourcePropertyB: Int,
)

data class MappedTarget(
    val targetPropertyA: String,
    val targetPropertyB: Int,
    val normalProperty: String,
)

// Test case 2: Multiple property names mapping (same source property maps to multiple target properties)
@CombineTo(MultiMappedTarget::class)
data class MultiMappingSource(
    @CombineTo.Map("targetName1", "targetName2")
    val sourceName: String,
)

@CombineTo(MultiMappedTarget::class)
data class MultiMappingSourceB(
    val otherProp: Int,
)

data class MultiMappedTarget(
    val targetName1: String,
    val targetName2: String,
    val otherProp: Int,
)

// Test case 3: Overlapping property mapping (both name matching and explicit mapping)
@CombineTo(MixedMappingTarget::class)
data class MixedMappingSourceA(
    val directMatch: String,
)

@CombineTo(MixedMappingTarget::class)
data class MixedMappingSourceB(
    @CombineTo.Map("renamedProperty")
    val originalProperty: Int,
)

data class MixedMappingTarget(
    val directMatch: String,
    val renamedProperty: Int,
    val extraProperty: Boolean,
)

// Test case 3: Overlapping property mapping (both name matching and explicit mapping)
@CombineTo(MergeMappingTarget::class)
data class MergeMappingSourceA(
    val sourcePropertyA: String,
)

@CombineTo(MergeMappingTarget::class)
data class MergeMappingSourceB(
    @CombineTo.Map("sourcePropertyA")
    val sourcePropertyB: String,
)

data class MergeMappingTarget(
    val sourcePropertyA: String,
)
