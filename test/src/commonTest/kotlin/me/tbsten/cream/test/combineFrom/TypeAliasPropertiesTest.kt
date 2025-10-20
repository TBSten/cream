package me.tbsten.cream.test.combineFrom

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TypeAliasPropertiesTest {

    @Test
    fun testCombineFromWithTypeAliasTarget() {
        // Create source instances through typealiases
        val sourceX: SourceXAlias = SourceX(propX = "x-value")
        val sourceY: SourceYAlias = SourceY(propY = 99)

        // Call the generated combine function
        // Note: Function name and parameter names are based on the actual type (CombinedResult, SourceY), not the alias
        val result: CombinedResultAlias = sourceX.copyToCombinedResult(
            sourceY = sourceY,
            extraProp = "extra"
        )

        // Verify all properties are copied correctly
        assertEquals("x-value", result.propX)
        assertEquals(99, result.propY)
        assertEquals("extra", result.extraProp)

        // Verify the result is actually a CombinedResult instance
        assertIs<CombinedResult>(result)
    }

    @Test
    fun testCombineFromPreservesAllSourceProperties() {
        val sourceX: SourceXAlias = SourceX(propX = "test-x")
        val sourceY: SourceYAlias = SourceY(propY = 200)

        val result: CombinedResultAlias = sourceX.copyToCombinedResult(
            sourceY = sourceY,
            extraProp = "test-extra"
        )

        // Verify that all source properties are preserved in the result
        assertEquals(sourceX.propX, result.propX)
        assertEquals(sourceY.propY, result.propY)
    }
}
