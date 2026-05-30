package me.tbsten.cream.test.copyToChildren

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ObjectTargetTest :
    FunSpec({
        test("dataObjectToDataObject") {
            val source = EmptyChild1
            val target: EmptyChild2 = source.copyToEmptyChild2()

            target shouldBe EmptyChild2
        }
    })
