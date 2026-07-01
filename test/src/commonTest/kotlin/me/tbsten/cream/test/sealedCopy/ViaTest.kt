package me.tbsten.cream.test.sealedCopy

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class ViaTest :
    FreeSpec({
        "copy_viaDelegate_doesNotRecurse_andUpdatesAbstractProperty" {
            val error: ViaState = ViaState.Error(sessionId = "abc", attempt = 1)

            // Before the #162 fix this recursed infinitely (StackOverflowError).
            val updated = error.copy(attempt = 2)

            val custom = updated.shouldBeInstanceOf<ViaState.Error>()
            custom.sessionId shouldBe "abc"
            custom.attempt shouldBe 2
        }

        "copy_viaDelegate_withNoArguments_roundTrips" {
            val error: ViaState = ViaState.Error(sessionId = "x", attempt = 5)

            val same = error.copy()

            val custom = same.shouldBeInstanceOf<ViaState.Error>()
            custom.sessionId shouldBe "x"
            custom.attempt shouldBe 5
        }

        "copy_stillDispatchesDataClassBranch" {
            val loading: ViaState = ViaState.Loading(sessionId = "a", attempt = 1)

            loading.copy(sessionId = "z") shouldBe ViaState.Loading(sessionId = "z", attempt = 1)
        }
    })
