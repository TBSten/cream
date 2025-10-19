package me.tbsten.cream.test.combineTo.edgeCase

import kotlin.test.Test
import kotlin.test.assertSame

class ObjectTargetTest {
    @Test
    fun combineToObject() {
        val sourceA = ObjectSourceA(propertyA = "test")
        val sourceB = ObjectSourceB(propertyB = 42)

        val result =
            sourceA.copyToTargetObject(
                objectSourceB = sourceB,
            )

        // Objects are singletons, so the result should be the same instance
        assertSame(TargetObject, result)
    }
}
