package me.tbsten.cream.test.copyFrom

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class VisibilityTest :
    FreeSpec({
        "visibilityProperties" {
            val source = VisibilitySource("public", 42, true)
            val target: VisibilityTarget = source.copyToVisibilityTarget(newProperty = "new")

            target shouldBe VisibilityTarget("public", "new")
        }
    })
