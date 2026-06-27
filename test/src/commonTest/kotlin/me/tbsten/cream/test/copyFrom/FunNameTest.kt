package me.tbsten.cream.test.copyFrom

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FunNameTest :
    FreeSpec({
        "token override renames the copyFrom function" {
            FunNameSrc("x").buildFunNameDst(extra = 1) shouldBe FunNameDst("x", 1)
        }
    })
