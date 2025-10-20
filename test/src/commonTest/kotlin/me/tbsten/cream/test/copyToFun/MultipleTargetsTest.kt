package me.tbsten.cream.test.copyToFun

import kotlin.test.Test
import kotlin.test.assertEquals

class MultipleTargetsTest {
    @Test
    fun multipleTargetsWithDifferentFactoryFunctions() {
        val source = MultiSource(value = "shared")

        val target1: Target1 = source.copyToTarget1(extra1 = 10)
        val target2: Target2 = source.copyToTarget2(extra2 = true)

        assertEquals(
            Target1(value = "shared", extra1 = 10),
            target1,
        )
        assertEquals(
            Target2(value = "shared", extra2 = true),
            target2,
        )
    }
}
