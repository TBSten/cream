package me.tbsten.cream.test.combineFrom

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FunNameTest :
    FreeSpec({
        "token override renames the combineFrom function" {
            FunNameA("x").buildFunNameDst(funNameB = FunNameB(1), extra = 2L) shouldBe FunNameDst("x", 1, 2L)
        }
    })
