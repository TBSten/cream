package me.tbsten.cream.test.copyToChildren

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class VisibilityTest :
    FreeSpec({
        "visibilityProperties" {
            val source = VisibilityChild1(publicProp = "public", internalProp = "internal")
            val target: VisibilityChild2 = source.copyToVisibilityChild2(newProperty = "new")

            target shouldBe
                VisibilityChild2(
                    publicProp = "public",
                    internalProp = "internal",
                    newProperty = "new",
                )
        }
    })
