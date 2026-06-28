package me.tbsten.cream.test.copyFrom

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class SealedTargetTest :
    FreeSpec({
        "sealedTargetSourceToDone" {
            val source = SealedTargetSource(name = "name")

            source.copyToSealedTargetStateFirstSecondDone() shouldBe
                SealedTargetState.First.Second.Done(name = "name")
        }

        "sealedTargetSourceToRefreshing" {
            val source = SealedTargetSource(name = "name")

            source.copyToSealedTargetStateFirstSecondRefreshing(extra = 42) shouldBe
                SealedTargetState.First.Second.Refreshing(name = "name", extra = 42)
        }
    })
