package me.tbsten.cream.test.copyTo

import kotlin.test.Test
import kotlin.test.assertEquals

class ObjectTargetTest {
    @Test
    fun dataObjectToDataObject() {
        val source = EmptySource
        val target = source.copyToEmptyTarget()

        assertEquals(EmptyTarget, target)
    }
}
