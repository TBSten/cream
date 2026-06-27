package me.tbsten.cream.test.copyFrom

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class NestedTest :
    FreeSpec({
        "nestedClasses" {
            val parent = ParentClass("parent", 100)
            val source = NestedSource(parent)
            val target: NestedTarget = source.copyToNestedTarget(newProperty = "new")

            target shouldBe NestedTarget(parent, "new")
        }
    })
