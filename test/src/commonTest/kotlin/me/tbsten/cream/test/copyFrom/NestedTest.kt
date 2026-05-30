package me.tbsten.cream.test.copyFrom

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NestedTest :
    FunSpec({
        test("nestedClasses") {
            val parent = ParentClass("parent", 100)
            val source = NestedSource(parent)
            val target: NestedTarget = source.copyToNestedTarget(newProperty = "new")

            target shouldBe NestedTarget(parent, "new")
        }
    })
