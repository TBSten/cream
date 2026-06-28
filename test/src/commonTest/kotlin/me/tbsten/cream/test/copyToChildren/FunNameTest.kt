package me.tbsten.cream.test.copyToChildren

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FunNameTest :
    FreeSpec({
        "token funName names each per-leaf function from its own leaf" {
            val state: FunNameState = FunNameState.Loading("s")
            state.intoLoading() shouldBe FunNameState.Loading("s")
            state.intoSuccess(data = "d") shouldBe FunNameState.Success("s", "d")
        }

        "DefaultCopyFunctionName + suffix keeps each derived per-leaf name unique" {
            val state: FunNameSuffixState = FunNameSuffixState.A("s")
            state.copyToFunNameSuffixStateAOrNull() shouldBe FunNameSuffixState.A("s")
            state.copyToFunNameSuffixStateBOrNull(x = 7) shouldBe FunNameSuffixState.B("s", 7)
        }
    })
