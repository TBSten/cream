package me.tbsten.cream.test.copyToChildren

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ObjectTargetTest :
    FreeSpec({
        "dataObjectToDataObject" {
            val source = EmptyChild1
            val target: EmptyChild2 = source.copyToEmptyChild2()

            target shouldBe EmptyChild2
        }
    })
