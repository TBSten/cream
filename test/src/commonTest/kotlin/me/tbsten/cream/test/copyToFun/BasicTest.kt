package me.tbsten.cream.test.copyToFun

import kotlin.test.Test
import kotlin.test.assertEquals

class BasicTest {
    @Test
    fun basicCopyWithFactoryFunction() {
        val source = MySource(a = "test", b = 42)

        val target: MyTarget = source.copyToMyTarget(c = true)

        assertEquals(
            MyTarget(a = "test", b = 42, c = true),
            target,
        )
    }

    @Test
    fun basicCopyWithOverride() {
        val source = MySource(a = "test", b = 42)

        val target: MyTarget =
            source.copyToMyTarget(
                a = "overridden",
                b = 100,
                c = false,
            )

        assertEquals(
            MyTarget(a = "overridden", b = 100, c = false),
            target,
        )
    }
}
