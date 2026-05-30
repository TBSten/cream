package me.tbsten.cream.test.sealedCopy

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class MultipleTest {
    @Test
    fun withUpdated_andWithUpdatedOrNull_areBothGenerated() {
        val loading: MultiSealedCopyState =
            MultiSealedCopyState.Loading(sessionId = "abc")

        // Both extensions exist independently. Distinct names → distinct
        // overload-resolution results; no shadowing.
        val viaAsIs: MultiSealedCopyState = loading.withUpdated(sessionId = "x")
        val viaNullable: MultiSealedCopyState? = loading.withUpdatedOrNull(sessionId = "y")

        assertEquals(MultiSealedCopyState.Loading(sessionId = "x"), viaAsIs)
        assertEquals(MultiSealedCopyState.Loading(sessionId = "y"), viaNullable)
    }

    @Test
    fun withUpdated_returnsEmptyAsIs_forObjectBranch() {
        val empty: MultiSealedCopyState = MultiSealedCopyState.Empty

        val result = empty.withUpdated(sessionId = "ignored")

        // RETURN_AS_IS: object branch collapses to `this`. The supplied sessionId
        // never reaches the singleton.
        assertEquals(MultiSealedCopyState.Empty, result)
        check(result === MultiSealedCopyState.Empty) {
            "RETURN_AS_IS on an object branch must return the singleton itself"
        }
    }

    @Test
    fun withUpdatedOrNull_returnsNull_forObjectBranch() {
        val empty: MultiSealedCopyState = MultiSealedCopyState.Empty

        val result: MultiSealedCopyState? = empty.withUpdatedOrNull(sessionId = "ignored")

        // RETURN_NULL: object branch collapses to null. Caller must handle it.
        assertNull(result)
    }

    @Test
    fun bothStrategies_delegateToCopy_forDataClassBranch() {
        val loading: MultiSealedCopyState = MultiSealedCopyState.Loading(sessionId = "abc")

        val viaAsIs = loading.withUpdated(sessionId = "x")
        val viaNullable = assertNotNull(loading.withUpdatedOrNull(sessionId = "x"))

        // Data class branches behave identically across both strategies — the
        // strategy only affects how non-copyable branches are emitted.
        assertEquals(viaAsIs, viaNullable)
    }
}
