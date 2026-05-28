package me.tbsten.cream.test.copyTo

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Smoke test for the `kdoc = KDoc(...)` parameter on `@CopyTo`.
 *
 * The actual KDoc content is verified by snapshot tests in the `cream-ksp` module —
 * here we just confirm that supplying a [me.tbsten.cream.KDoc] argument does not
 * break code generation or the runtime behavior of the generated copy function.
 */
class KDocTest {
    @Test
    fun copyToKDocTarget_behavesNormally_evenWhenKDocIsProvided() {
        val source = KDocSource(shared = "value", onlyOnSource = 1)

        val target = source.copyToKDocTarget(extra = 42)

        assertEquals(KDocTarget(shared = "value", extra = 42), target)
    }
}
