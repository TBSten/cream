package me.tbsten.cream.test.sealedCopy

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

class BasicTest :
    FreeSpec({
        "copy_preservesSubtype_andUpdatesSharedProperty" {
            val loading: BasicState = BasicState.Loading(sessionId = "abc", attempt = 1)
            val updated = loading.copy(attempt = 2)

            updated shouldBe BasicState.Loading(sessionId = "abc", attempt = 2)
        }

        "copy_withNoArguments_returnsEquivalentInstance" {
            val success: BasicState =
                BasicState.Success(sessionId = "abc", attempt = 1, payload = "hi")

            val updated = success.copy()

            updated shouldBe success
        }

        "copy_dispatchesPerSubtype" {
            val states: List<BasicState> =
                listOf(
                    BasicState.Loading(sessionId = "a", attempt = 1),
                    BasicState.Success(sessionId = "b", attempt = 2, payload = "x"),
                )

            val updated = states.map { it.copy(sessionId = "z") }

            // Each branch must round-trip through *its own* data class .copy(), not
            // collapse to a single supertype instance.
            (updated[0] as BasicState.Loading).sessionId shouldBe "z"
            (updated[0] as BasicState.Loading).attempt shouldBe 1
            (updated[1] as BasicState.Success).sessionId shouldBe "z"
            (updated[1] as BasicState.Success).payload shouldBe "x"
        }

        "copy_doesNotShareIdentityWithReceiver" {
            val loading = BasicState.Loading(sessionId = "abc", attempt = 1)

            val updated = loading.copy()

            // data class .copy() always allocates — sanity-check that we're delegating
            // and not accidentally returning `this`.
            updated shouldBe loading
            withClue("copy() must not return the same instance") {
                loading shouldNotBeSameInstanceAs updated
            }
        }
    })
