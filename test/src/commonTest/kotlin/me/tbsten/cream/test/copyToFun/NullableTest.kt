package me.tbsten.cream.test.copyToFun

import kotlin.test.Test
import kotlin.test.assertEquals

class NullableTest {
    @Test
    fun nullablePropertiesWithValues() {
        val source =
            NullableSource(
                required = "required value",
                optional = "optional value",
            )

        val target: NullableTarget = source.copyToNullableTarget(extra = 42)

        assertEquals(
            NullableTarget(
                required = "required value",
                optional = "optional value",
                extra = 42,
            ),
            target,
        )
    }

    @Test
    fun nullablePropertiesWithNulls() {
        val source =
            NullableSource(
                required = "required value",
                optional = null,
            )

        val target: NullableTarget = source.copyToNullableTarget(extra = null)

        assertEquals(
            NullableTarget(
                required = "required value",
                optional = null,
                extra = null,
            ),
            target,
        )
    }
}
