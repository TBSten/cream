package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ObjectTargetSnapshotTest {
    private val combineToObjectSource: String =
        """
        package snap.objtarget

        import me.tbsten.cream.CombineTo

        @CombineTo(Singleton::class)
        data class Source(val prop: String)

        data object Singleton
        """.trimIndent()

    @Test
    fun `combineTo with object target generates expected source`() {
        val result = compileWithCream(combineToObjectSource)

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        assertMatchesSnapshot(
            "ObjectTargetSnapshotTest.combineToObject.default",
            result.generatedSourceText(),
        )
    }

    @Test
    fun `combineTo with object target and notCopyToObject=true generates expected source`() {
        val result =
            compileWithCream(
                combineToObjectSource,
                options = mapOf("cream.notCopyToObject" to "true"),
            )

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        assertMatchesSnapshot(
            "ObjectTargetSnapshotTest.combineToObject.notCopyToObject",
            result.generatedSourceText(),
        )
    }
}
