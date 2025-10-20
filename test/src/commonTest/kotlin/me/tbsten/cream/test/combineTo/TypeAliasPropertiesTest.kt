package me.tbsten.cream.test.combineTo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TypeAliasPropertiesTest {

    @Test
    fun testCombineToWithTypeAliasSource() {
        // Create source instances through typealiases
        val sourceA: SourceAWithAlias = SourceA(propA = "value-a")
        val sourceB: SourceBWithAlias = SourceB(propB = 42)

        // Call the generated combine function
        // Note: Function name and parameter names are based on the actual type (CombinedTarget, SourceB), not the alias
        val combined: CombinedTargetAlias = sourceA.copyToCombinedTarget(
            sourceB = sourceB,
            extra = "extra-value"
        )

        // Verify all properties are copied correctly
        assertEquals("value-a", combined.propA)
        assertEquals(42, combined.propB)
        assertEquals("extra-value", combined.extra)

        // Verify the result is actually a CombinedTarget instance
        assertIs<CombinedTarget>(combined)
    }

    @Test
    fun testCombineToPreservesAllProperties() {
        val sourceA: SourceAWithAlias = SourceA(propA = "test-prop-a")
        val sourceB: SourceBWithAlias = SourceB(propB = 100)

        val combined: CombinedTargetAlias = sourceA.copyToCombinedTarget(
            sourceB = sourceB,
            extra = "test-extra"
        )

        // Verify that all source properties are preserved
        assertEquals(sourceA.propA, combined.propA)
        assertEquals(sourceB.propB, combined.propB)
    }
}
