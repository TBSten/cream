package me.tbsten.cream.test.copyToChildren

import kotlin.test.Test
import kotlin.test.assertEquals

class VisibilityTest {
    @Test
    fun visibilityProperties() {
        val source = VisibilityChild1(publicProp = "public", internalProp = "internal")
        val target = source.copyToVisibilityChild2(newProperty = "new")

        assertEquals(
            VisibilityChild2(
                publicProp = "public",
                internalProp = "internal",
                newProperty = "new",
            ),
            target,
        )
    }
}
