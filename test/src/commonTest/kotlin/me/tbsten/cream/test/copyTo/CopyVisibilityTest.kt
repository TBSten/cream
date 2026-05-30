package me.tbsten.cream.test.copyTo

import kotlin.test.Test
import kotlin.test.assertEquals

class CopyVisibilityTest {
    // The generated function being `internal` is verified at compile time: this test
    // (in the same module) can call it. A `public`-only caller would not compile.
    @Test
    fun internalVisibilityCopyFunctionIsCallable() {
        val source = InternalVisibilitySource(shared = "value")
        val target: InternalVisibilityTarget = source.copyToInternalVisibilityTarget(extra = 42)

        assertEquals(InternalVisibilityTarget("value", 42), target)
    }
}
