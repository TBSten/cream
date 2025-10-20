package me.tbsten.cream.test.copyToChildren

import kotlin.test.Test
import kotlin.test.assertEquals

class NestedTest {
    @Test
    fun nestedClasses() {
        val parent = ParentClass("test", 100)
        val source = NestedChild1(parent, "test name")
        val target: NestedChild2 = source.copyToNestedChild2(value = 0)

        assertEquals(NestedChild2(parent, 0), target)
    }
}
