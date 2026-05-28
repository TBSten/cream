package me.tbsten.cream.ksp.options

import com.tschuchort.compiletesting.KotlinCompilation
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class NotCopyToObjectOptionTest {
    // @CombineTo is the path that actually consults options.notCopyToObject (Transform.kt:94),
    // so we exercise it here. @CopyToChildren has its own annotation parameter for the same
    // intent, which is covered by the existing test/ module.
    private val combineToSource: String =
        """
        package opts.notobj

        import me.tbsten.cream.CombineTo

        @CombineTo(Singleton::class)
        data class Source(val prop: String)

        data object Singleton
        """.trimIndent()

    @Test
    fun `default behavior generates combine function to object target`() {
        val result = compileWithCream(combineToSource)

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        val generated = result.generatedSourceText()
        assertTrue(
            generated.contains("copyToSingleton"),
            "Default behavior should include 'copyToSingleton' for the data object target. Actual:\n$generated",
        )
    }

    @Test
    fun `cream notCopyToObject=true suppresses combine function to data object`() {
        val result =
            compileWithCream(
                combineToSource,
                options = mapOf("cream.notCopyToObject" to "true"),
            )

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        val generated = result.generatedSourceText()
        assertFalse(
            generated.contains("copyToSingleton"),
            "'copyToSingleton' should be suppressed when cream.notCopyToObject=true. Actual:\n$generated",
        )
    }
}
