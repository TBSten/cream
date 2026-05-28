package me.tbsten.cream.ksp.testing

import com.tschuchort.compiletesting.KotlinCompilation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class CreamCompilationSmokeTest {
    @Test
    fun `compiles a minimal @CopyTo source and generates a copy function`() {
        val result =
            compileWithCream(
                """
                package smoke

                import me.tbsten.cream.CopyTo

                @CopyTo(Target::class)
                data class Source(val shared: String)

                data class Target(val shared: String, val extra: Int)
                """.trimIndent(),
            )

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)

        val generatedText = result.generatedSourceText()
        assertTrue(
            generatedText.contains("copyToTarget"),
            "Generated source should contain copyToTarget function. Generated:\n$generatedText",
        )
    }

    @Test
    fun `multi-source DSL compiles when source and target live in separate files`() {
        val result =
            compileWithCream {
                "Source.kt" source
                    """
                    package smoke.multi

                    import me.tbsten.cream.CopyTo

                    @CopyTo(Target::class)
                    data class Source(val shared: String)
                    """.trimIndent()
                "Target.kt" source
                    """
                    package smoke.multi

                    data class Target(val shared: String, val extra: Int)
                    """.trimIndent()
            }

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        assertTrue(
            result.generatedSourceText().contains("copyToTarget"),
            "Multi-source compilation should still produce copyToTarget. Generated:\n${result.generatedSourceText()}",
        )
    }
}
