package me.tbsten.cream.ksp.options

import com.tschuchort.compiletesting.KotlinCompilation
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class CopyFunNamingStrategyOptionTest {
    private val sampleSource: String =
        """
        package opts.strategy

        import me.tbsten.cream.CopyTo

        @CopyTo(Target::class)
        data class Source(val prop: String)

        data class Target(val prop: String)
        """.trimIndent()

    private fun runWithStrategy(strategy: String) =
        compileWithCream(
            sampleSource,
            options = mapOf("cream.copyFunNamingStrategy" to strategy),
        )

    @Test
    fun `under-package strategy uses target name relative to package`() {
        val result = runWithStrategy("under-package")
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        // under-package: "opts.strategy.Target" minus "opts.strategy." = "Target"
        // escape lower-camel-case: "Target" -> "target"
        // prefix "copyTo" capitalizes -> "copyToTarget"
        assertTrue(
            result.generatedSourceText().contains("copyToTarget"),
            "Expected 'copyToTarget'. Actual:\n${result.generatedSourceText()}",
        )
    }

    @Test
    fun `simple-name strategy uses target simpleName`() {
        val result = runWithStrategy("simple-name")
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        assertTrue(
            result.generatedSourceText().contains("copyToTarget"),
            "Expected 'copyToTarget'. Actual:\n${result.generatedSourceText()}",
        )
    }

    @Test
    fun `full-name strategy includes the package in the function name`() {
        val result = runWithStrategy("full-name")
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        // full-name: "opts.strategy.Target"
        // escape lower-camel-case: -> "optsStrategyTarget"
        // prefix capitalizes -> "copyToOptsStrategyTarget"
        assertTrue(
            result.generatedSourceText().contains("copyToOptsStrategyTarget"),
            "Expected 'copyToOptsStrategyTarget'. Actual:\n${result.generatedSourceText()}",
        )
    }

    @Test
    fun `diff strategy uses difference between source and target`() {
        val result = runWithStrategy("diff")
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        // common prefix "opts.strategy." then S vs T differ -> diff = "Target"
        // escape lower-camel-case "Target" -> "target"
        // prefix capitalize -> "copyToTarget"
        assertTrue(
            result.generatedSourceText().contains("copyToTarget"),
            "Expected 'copyToTarget'. Actual:\n${result.generatedSourceText()}",
        )
    }

    @Test
    fun `inner-name strategy drops the leading segment of underPackageName`() {
        val result =
            compileWithCream(
                """
                package opts.strategy

                import me.tbsten.cream.CopyTo

                data class Outer(val prop: String) {
                    @CopyTo(Inner::class)
                    data class Source(val prop: String)

                    data class Inner(val prop: String)
                }
                """.trimIndent(),
                options = mapOf("cream.copyFunNamingStrategy" to "inner-name"),
            )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        // inner-name for Outer.Inner: underPackageName "Outer.Inner".split(".") -> ["Outer","Inner"], size>1
        // subList(1, 2) -> ["Inner"], joinToString(".") -> "Inner"
        // escape lower-camel-case "Inner" -> "inner"
        // prefix capitalize -> "copyToInner"
        assertTrue(
            result.generatedSourceText().contains("copyToInner"),
            "Expected 'copyToInner'. Actual:\n${result.generatedSourceText()}",
        )
    }
}
