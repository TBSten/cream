package me.tbsten.cream.test.combineFrom

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FunNameTest :
    FunSpec({
        test("token override renames the combineFrom function") {
            FunNameA("x").buildFunNameDst(funNameB = FunNameB(1), extra = 2L) shouldBe FunNameDst("x", 1, 2L)
        }
    })
