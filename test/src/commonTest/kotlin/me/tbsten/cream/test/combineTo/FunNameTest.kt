package me.tbsten.cream.test.combineTo

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FunNameTest :
    FreeSpec({
        "token override renames the combineTo function" {
            FunNameSrc("x").toFunNameDst(extra = 1) shouldBe FunNameDst("x", 1)
        }
    })
