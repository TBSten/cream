package me.tbsten.cream.test.copyFrom

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class VisibilityTest :
    FunSpec({
        test("visibilityProperties") {
            val source = VisibilitySource("public", 42, true)
            val target: VisibilityTarget = source.copyToVisibilityTarget(newProperty = "new")

            target shouldBe VisibilityTarget("public", "new")
        }
    })
