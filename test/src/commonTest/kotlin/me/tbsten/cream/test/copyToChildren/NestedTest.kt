package me.tbsten.cream.test.copyToChildren

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class NestedTest :
    FreeSpec({
        "nestedClasses" {
            val parent = ParentClass("test", 100)
            val source = NestedChild1(parent, "test name")
            val target: NestedChild2 = source.copyToNestedChild2(value = 0)

            target shouldBe NestedChild2(parent, 0)
        }
    })
