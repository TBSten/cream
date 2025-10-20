package me.tbsten.cream.test.copyFrom

import kotlin.test.Test
import kotlin.test.assertEquals

class NestedTest {
    @Test
    fun nestedClasses() {
        val parent = ParentClass("parent", 100)
        val source = NestedSource(parent)
        val target: NestedTarget = source.copyToNestedTarget(newProperty = "new")

        assertEquals(NestedTarget(parent, "new"), target)
    }
}
