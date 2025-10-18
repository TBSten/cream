package me.tbsten.cream.test.combineFrom.edgeCase

import me.tbsten.cream.CombineFrom

/**
 * Test property mapping with @CombineFrom.Map annotation
 */

// Test case 1: Simple property name mapping
data class MappingSourceA(
    @CombineFrom.Map("targetPropertyA")
    val sourcePropertyA: String,
)

data class MappingSourceB(
    @CombineFrom.Map("targetPropertyB")
    val sourcePropertyB: Int,
)

@CombineFrom(MappingSourceA::class, MappingSourceB::class)
data class MappedTarget(
    val targetPropertyA: String,
    val targetPropertyB: Int,
    val normalProperty: String,
)

// Test case 2: Multiple property names mapping
data class MultiMappingSource(
    @CombineFrom.Map("targetName1", "targetName2")
    val sourceName: String,
)

data class MultiMappingSourceB(
    val otherProp: Int,
)

@CombineFrom(MultiMappingSource::class, MultiMappingSourceB::class)
data class MultiMappedTarget(
    val targetName1: String,
    val targetName2: String,
    val otherProp: Int,
)

// Test case 3: Mixed mapping (both direct match and explicit mapping)
data class MixedMappingSourceA(
    val directMatch: String,
)

data class MixedMappingSourceB(
    @CombineFrom.Map("renamedProperty")
    val originalProperty: Int,
)

@CombineFrom(MixedMappingSourceA::class, MixedMappingSourceB::class)
data class MixedMappingTarget(
    val directMatch: String,
    val renamedProperty: Int,
    val extraProperty: Boolean,
)

// Test case 4: Merge mapping (multiple sources map to same target property)
data class MergeMappingSourceA(
    val sourcePropertyA: String,
)

data class MergeMappingSourceB(
    val sourcePropertyB: String,
)

@CombineFrom(MergeMappingSourceA::class, MergeMappingSourceB::class)
data class MergeMappingTarget(
    @CombineFrom.Map("sourcePropertyB")
    val sourcePropertyA: String,
)
