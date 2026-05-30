package me.tbsten.cream.test.sealedCopy

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class MultipleTest :
    FunSpec({
        test("withUpdated_andWithUpdatedOrNull_areBothGenerated") {
            val loading: MultiSealedCopyState =
                MultiSealedCopyState.Loading(sessionId = "abc")

            // Both extensions exist independently. Distinct names → distinct
            // overload-resolution results; no shadowing.
            val viaAsIs: MultiSealedCopyState = loading.withUpdated(sessionId = "x")
            val viaNullable: MultiSealedCopyState? = loading.withUpdatedOrNull(sessionId = "y")

            viaAsIs shouldBe MultiSealedCopyState.Loading(sessionId = "x")
            viaNullable shouldBe MultiSealedCopyState.Loading(sessionId = "y")
        }

        test("withUpdated_returnsEmptyAsIs_forObjectBranch") {
            val empty: MultiSealedCopyState = MultiSealedCopyState.Empty

            val result = empty.withUpdated(sessionId = "ignored")

            // RETURN_AS_IS: object branch collapses to `this`. The supplied sessionId
            // never reaches the singleton.
            result shouldBe MultiSealedCopyState.Empty
            withClue("RETURN_AS_IS on an object branch must return the singleton itself") {
                result shouldBeSameInstanceAs MultiSealedCopyState.Empty
            }
        }

        test("withUpdatedOrNull_returnsNull_forObjectBranch") {
            val empty: MultiSealedCopyState = MultiSealedCopyState.Empty

            val result: MultiSealedCopyState? = empty.withUpdatedOrNull(sessionId = "ignored")

            // RETURN_NULL: object branch collapses to null. Caller must handle it.
            result shouldBe null
        }

        test("bothStrategies_delegateToCopy_forDataClassBranch") {
            val loading: MultiSealedCopyState = MultiSealedCopyState.Loading(sessionId = "abc")

            val viaAsIs = loading.withUpdated(sessionId = "x")
            val viaNullable = loading.withUpdatedOrNull(sessionId = "x").shouldNotBeNull()

            // Data class branches behave identically across both strategies — the
            // strategy only affects how non-copyable branches are emitted.
            viaNullable shouldBe viaAsIs
        }
    })
