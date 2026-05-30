package me.tbsten.cream.ksp.diagnostic

import com.tschuchort.compiletesting.KotlinCompilation
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class SealedCopyDiagnosticTest {
    @Test
    fun `object subtype under default ERROR strategy fails compilation`() {
        val source =
            """
            package diag.sealedCopy

            import me.tbsten.cream.SealedCopy

            @SealedCopy
            sealed interface MyState {
                val name: String

                data class Loading(override val name: String) : MyState
                data object Empty : MyState { override val name: String = "" }
            }
            """.trimIndent()
        val result = compileWithCream(source)

        assertNotEquals(
            KotlinCompilation.ExitCode.OK,
            result.exitCode,
            "Compilation should fail. Output:\n${result.normalizedCompilerOutput()}",
        )
        assertMatchesSnapshot("SealedCopyDiagnosticTest.objectErrorDefault.output") {
            "Compiler output" facetOf result.normalizedCompilerOutput()
            "Input" facetOf source
        }
    }

    @Test
    fun `non-data class without compatible copy under default ERROR strategy fails compilation`() {
        val source =
            """
            package diag.sealedCopy

            import me.tbsten.cream.SealedCopy

            @SealedCopy
            sealed interface MyState {
                val name: String

                data class Loading(override val name: String) : MyState
                class Frozen(override val name: String) : MyState
            }
            """.trimIndent()
        val result = compileWithCream(source)

        assertNotEquals(
            KotlinCompilation.ExitCode.OK,
            result.exitCode,
            "Compilation should fail. Output:\n${result.normalizedCompilerOutput()}",
        )
        assertMatchesSnapshot("SealedCopyDiagnosticTest.missingCopy.output") {
            "Compiler output" facetOf result.normalizedCompilerOutput()
            "Input" facetOf source
        }
    }

    @Test
    fun `missing-copy diagnostic names 'copy' even when funName is customized`() {
        val source =
            """
            package diag.sealedCopy

            import me.tbsten.cream.SealedCopy

            @SealedCopy(funName = "updated")
            sealed interface MyState {
                val name: String

                data class Loading(override val name: String) : MyState
                class Frozen(override val name: String) : MyState
            }
            """.trimIndent()
        val result = compileWithCream(source)

        assertNotEquals(
            KotlinCompilation.ExitCode.OK,
            result.exitCode,
            "Compilation should fail. Output:\n${result.normalizedCompilerOutput()}",
        )
        assertMatchesSnapshot("SealedCopyDiagnosticTest.missingCopyCustomFunName.output") {
            "Compiler output" facetOf result.normalizedCompilerOutput()
            "Input" facetOf source
        }
    }

    @Test
    fun `non-sealed class with @SealedCopy fails compilation`() {
        val source =
            """
            package diag.sealedCopy

            import me.tbsten.cream.SealedCopy

            @SealedCopy
            class NotSealed(val name: String)
            """.trimIndent()
        val result = compileWithCream(source)

        assertNotEquals(
            KotlinCompilation.ExitCode.OK,
            result.exitCode,
            "Compilation should fail. Output:\n${result.normalizedCompilerOutput()}",
        )
        assertMatchesSnapshot("SealedCopyDiagnosticTest.nonSealedClass.output") {
            "Compiler output" facetOf result.normalizedCompilerOutput()
            "Input" facetOf source
        }
    }
}
