package me.tbsten.cream.test.copyFrom

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FunNameTest :
    FunSpec({
        test("token override renames the copyFrom function") {
            FunNameSrc("x").buildFunNameDst(extra = 1) shouldBe FunNameDst("x", 1)
        }
    })
