package me.tbsten.cream.test.copyTo

import kotlin.test.Test
import kotlin.test.assertEquals

class VisibilityTest {
    @Test
    fun visibilityProperties() {
        val source = VisibilitySource("public", 42, true)
        val target = source.copyToVisibilityTarget(newProperty = "new")

        assertEquals(VisibilityTarget("public", "new"), target)
    }
}
