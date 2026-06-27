package me.tbsten.cream.test.combineMapping

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FunNameTest :
    FreeSpec({
        "token override renames the combineMapping function" {
            FunNameCmA("x").toFunNameCmDst(funNameCmB = FunNameCmB(1), extra = 2L) shouldBe
                FunNameCmDst("x", 1, 2L)
        }
    })
