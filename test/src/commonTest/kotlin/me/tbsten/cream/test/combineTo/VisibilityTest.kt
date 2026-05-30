package me.tbsten.cream.test.combineTo

import kotlin.test.Test
import kotlin.test.assertEquals

class VisibilityTest {
    // The generated function being `internal` is verified at compile time: this test
    // (in the same module) can call it. A `public`-only caller would not compile.
    @Test
    fun internalVisibilityCombineFunctionIsCallable() {
        val source = InternalVisibilityCombineSource(shared = "value")
        val target: InternalVisibilityCombineTarget =
            source.copyToInternalVisibilityCombineTarget(extra = 42)

        assertEquals(InternalVisibilityCombineTarget("value", 42), target)
    }
}
