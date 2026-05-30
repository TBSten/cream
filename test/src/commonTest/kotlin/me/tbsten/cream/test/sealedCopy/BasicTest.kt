package me.tbsten.cream.test.sealedCopy

import kotlin.test.Test
import kotlin.test.assertEquals

class BasicTest {
    @Test
    fun copy_preservesSubtype_andUpdatesSharedProperty() {
        val loading: BasicState = BasicState.Loading(sessionId = "abc", attempt = 1)
        val updated = loading.copy(attempt = 2)

        assertEquals(BasicState.Loading(sessionId = "abc", attempt = 2), updated)
    }

    @Test
    fun copy_withNoArguments_returnsEquivalentInstance() {
        val success: BasicState =
            BasicState.Success(sessionId = "abc", attempt = 1, payload = "hi")

        val updated = success.copy()

        assertEquals(success, updated)
    }

    @Test
    fun copy_dispatchesPerSubtype() {
        val states: List<BasicState> =
            listOf(
                BasicState.Loading(sessionId = "a", attempt = 1),
                BasicState.Success(sessionId = "b", attempt = 2, payload = "x"),
            )

        val updated = states.map { it.copy(sessionId = "z") }

        // Each branch must round-trip through *its own* data class .copy(), not
        // collapse to a single supertype instance.
        assertEquals("z", (updated[0] as BasicState.Loading).sessionId)
        assertEquals(1, (updated[0] as BasicState.Loading).attempt)
        assertEquals("z", (updated[1] as BasicState.Success).sessionId)
        assertEquals("x", (updated[1] as BasicState.Success).payload)
    }

    @Test
    fun copy_doesNotShareIdentityWithReceiver() {
        val loading = BasicState.Loading(sessionId = "abc", attempt = 1)

        val updated = loading.copy()

        // data class .copy() always allocates — sanity-check that we're delegating
        // and not accidentally returning `this`.
        assertEquals(loading, updated)
        check(loading !== updated) { "copy() must not return the same instance" }
    }
}
