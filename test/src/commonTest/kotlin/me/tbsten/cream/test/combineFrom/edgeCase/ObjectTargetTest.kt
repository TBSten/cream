package me.tbsten.cream.test.combineFrom.edgeCase

import kotlin.test.Test
import kotlin.test.assertSame

class ObjectTargetTest {
    @Test
    fun combineFromToObject() {
        val sourceA = ObjectSourceA(propertyA = "test")
        val sourceB = ObjectSourceB(propertyB = 42)

        val result: TargetObject =
            sourceA.copyToTargetObject(
                objectSourceB = sourceB,
            )

        // Objects are singletons, so the result should be the same instance
        assertSame(TargetObject, result)
    }
}
