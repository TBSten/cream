package me.tbsten.cream.test.copyTo

import kotlin.test.Test
import kotlin.test.assertEquals

class NullableTest {
    @Test
    fun nonNullableToNullable() {
        val source =
            NonNullableSource(
                str = "test string",
                num = 42,
                bool = true,
                list = listOf("item1", "item2", "item3"),
            )

        val target = source.copyToNullableTarget(newProperty = "new")

        assertEquals(
            NullableTarget(
                str = "test string",
                num = 42,
                bool = true,
                list = listOf("item1", "item2", "item3"),
                newProperty = "new",
            ),
            target,
        )
    }

    @Test
    fun nonNullableToNullableWithEmptyValues() {
        val source =
            NonNullableSource(
                str = "",
                num = 0,
                bool = false,
                list = emptyList(),
            )

        val target = source.copyToNullableTarget(newProperty = "new")

        assertEquals(
            NullableTarget(
                str = "",
                num = 0,
                bool = false,
                list = emptyList(),
                newProperty = "new",
            ),
            target,
        )
    }
}
