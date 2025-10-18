package me.tbsten.cream.test.combineFrom.edgeCase

import kotlin.test.Test
import kotlin.test.assertEquals

class PropertyMappingTest {
    @Test
    fun simplePropertyNameMapping() {
        val sourceA = MappingSourceA(sourcePropertyA = "Hello")
        val sourceB = MappingSourceB(sourcePropertyB = 42)

        val result: MappedTarget = sourceA.copyToMappedTarget(
            mappingSourceB = sourceB,
            normalProperty = "World",
        )

        assertEquals("Hello", result.targetPropertyA)
        assertEquals(42, result.targetPropertyB)
        assertEquals("World", result.normalProperty)
    }

    @Test
    fun multiplePropertyNamesMapping() {
        val source = MultiMappingSource(sourceName = "SharedValue")
        val sourceB = MultiMappingSourceB(otherProp = 100)

        val result: MultiMappedTarget = source.copyToMultiMappedTarget(
            multiMappingSourceB = sourceB,
        )

        assertEquals("SharedValue", result.targetName1)
        assertEquals("SharedValue", result.targetName2)
        assertEquals(100, result.otherProp)
    }

    @Test
    fun mixedMappingWithDirectMatch() {
        val sourceA = MixedMappingSourceA(directMatch = "Direct")
        val sourceB = MixedMappingSourceB(originalProperty = 999)

        val result: MixedMappingTarget = sourceA.copyToMixedMappingTarget(
            mixedMappingSourceB = sourceB,
            extraProperty = true,
        )

        assertEquals("Direct", result.directMatch)
        assertEquals(999, result.renamedProperty)
        assertEquals(true, result.extraProperty)
    }

    @Test
    fun mergeMappingWithDirectMatch() {
        val sourceA = MergeMappingSourceA(sourcePropertyA = "SourceA")
        val sourceB = MergeMappingSourceB(sourcePropertyB = "SourceB")

        val result: MergeMappingTarget = sourceA.copyToMergeMappingTarget(
            mergeMappingSourceB = sourceB,
        )

        // When both sources map to the same property,
        // the last source class (SourceB) should take precedence
        assertEquals("SourceB", result.sourcePropertyA)
    }
}
