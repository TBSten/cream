package me.tbsten.cream.test.copyTo

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class NullableTest :
    FreeSpec({
        "nonNullableToNullable" {
            val source =
                NonNullableSource(
                    str = "test string",
                    num = 42,
                    bool = true,
                    list = listOf("item1", "item2", "item3"),
                )

            val target: NullableTarget = source.copyToNullableTarget(newProperty = "new")

            target shouldBe
                NullableTarget(
                    str = "test string",
                    num = 42,
                    bool = true,
                    list = listOf("item1", "item2", "item3"),
                    newProperty = "new",
                )
        }

        "nonNullableToNullableWithEmptyValues" {
            val source =
                NonNullableSource(
                    str = "",
                    num = 0,
                    bool = false,
                    list = emptyList(),
                )

            val target: NullableTarget = source.copyToNullableTarget(newProperty = "new")

            target shouldBe
                NullableTarget(
                    str = "",
                    num = 0,
                    bool = false,
                    list = emptyList(),
                    newProperty = "new",
                )
        }
    })
