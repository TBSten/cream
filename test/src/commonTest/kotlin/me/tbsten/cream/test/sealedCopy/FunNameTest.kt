package me.tbsten.cream.test.sealedCopy

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FunNameTest :
    FunSpec({
        test("DefaultCopyFunctionName resolves to copy and the prefix applies") {
            val state: FunNameSealed = FunNameSealed.A("i")
            state.mycopy(id = "j") shouldBe FunNameSealed.A("j")
        }

        test("a CopyTarget token renders the sealed type name") {
            val state: FunNameSealed = FunNameSealed.B("i", 5)
            state.toFunNameSealed(id = "k") shouldBe FunNameSealed.B("k", 5)
        }
    })
