package me.tbsten.cream.test.copyMapping

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FunNameTest :
    FunSpec({
        test("token override renames the copyMapping function") {
            FunNameMapA("x").toFunNameMapB(extra = 1) shouldBe FunNameMapB("x", 1)
        }

        test("canReverse with a token names both directions from their own targets") {
            FunNameRevA("x").toFunNameRevB() shouldBe FunNameRevB("x")
            FunNameRevB("y").toFunNameRevA() shouldBe FunNameRevA("y")
        }
    })
