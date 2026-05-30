package me.tbsten.cream.test.copyToChildren

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class VisibilityTest :
    FunSpec({
        test("visibilityProperties") {
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
