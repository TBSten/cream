package me.tbsten.cream.test.copyTo

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FunNameTest :
    FreeSpec({
        "token override renames the copy function" {
            FunNameSource("x").toFunNameTarget(extra = 1) shouldBe FunNameTarget("x", 1)
        }

        "backtick-quoted funName is callable" {
            FunNameBacktickSource("x").`to backtick`() shouldBe FunNameBacktickTarget("x")
        }

        "sealed target names each child function from its own child" {
            val source = FunNameStateSource("i")
            source.intoA() shouldBe FunNameState.A("i")
            source.intoB(x = 5) shouldBe FunNameState.B("i", 5)
        }
    })
