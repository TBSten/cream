package me.tbsten.cream.test.combineTo

import kotlin.test.Test
import kotlin.test.assertEquals

class BasicTest {
    @Test
    fun combineToTarget() {
        val sourceA = SourceStateA(propertyA = "sourceA")
        val sourceB = SourceStateB(propertyB = 42)

        val result: TargetState =
            sourceA.copyToTargetState(
                sourceStateB = sourceB,
                propertyC = true,
            )

        // Verify
        val expected =
            TargetState(
                propertyA = "sourceA",
                propertyB = 42,
                propertyC = true,
            )

        assertEquals(expected, result)
    }

    @Test
    fun combineToTargetWithOverride() {
        val sourceA = SourceStateA(propertyA = "sourceA")
        val sourceB = SourceStateB(propertyB = 42)

        val result: TargetState =
            sourceA.copyToTargetState(
                sourceStateB = sourceB,
                propertyA = "overridden",
                propertyB = 100,
                propertyC = true,
            )

        // Verify
        val expected =
            TargetState(
                propertyA = "overridden",
                propertyB = 100,
                propertyC = true,
            )

        assertEquals(expected, result)
    }
}
