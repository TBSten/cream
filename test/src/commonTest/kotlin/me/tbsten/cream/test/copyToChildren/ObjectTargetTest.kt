package me.tbsten.cream.test.copyToChildren

import kotlin.test.Test
import kotlin.test.assertEquals

class ObjectTargetTest {
    @Test
    fun dataObjectToDataObject() {
        val source = EmptyChild1
        val target: EmptyChild2 = source.copyToEmptyChild2()

        assertEquals(EmptyChild2, target)
    }
}
