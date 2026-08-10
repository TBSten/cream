package me.tbsten.cream.test.combineTo

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ValueClassMappingTest :
    FreeSpec({
        "combineWrapsReceiverPropertyAndUnwrapsSecondarySourceProperty" {
            val sourceA = CombineSourceA(id = "id-1")
            val sourceB = CombineSourceB(serial = CombinedId("serial-1"))

            // `id` (wrap from receiver) and `serial` (unwrap from sourceB) both default.
            val target =
                sourceA.copyToCombineValueClassTarget(
                    combineSourceB = sourceB,
                    extra = true,
                )

            target shouldBe
                CombineValueClassTarget(
                    id = CombinedId("id-1"),
                    serial = "serial-1",
                    extra = true,
                )
        }
    })
