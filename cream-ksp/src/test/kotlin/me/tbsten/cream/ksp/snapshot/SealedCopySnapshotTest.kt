package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Consolidated success-path snapshots for `@SealedCopy`. Pins one scenario per axis of
 * the feature: shape of the hierarchy (flat / nested / generic), function-name and
 * delegate overrides (default `copy` / custom `funName` / `@SealedCopy.Map` / manual
 * copy), how non-copyable branches are handled (`RETURN_AS_IS` / `RETURN_NULL`), and
 * stacking multiple `@SealedCopy` annotations on the same sealed type.
 *
 * Diagnostic / error paths live in `SealedCopyDiagnosticTest`.
 */
internal class SealedCopySnapshotTest {
    private fun runSnapshot(
        snapshotName: String,
        source: String,
    ) {
        val result = compileWithCream(source)

        assertEquals(
            KotlinCompilation.ExitCode.OK,
            result.exitCode,
            "Compilation should succeed. Output:\n${result.normalizedCompilerOutput()}",
        )
        assertMatchesSnapshot(snapshotName) {
            "Generated" facetOf result.generatedSourceText()
            "Input" facetOf source
        }
    }

    @Test
    fun `injects user KDoc supplied via the kdoc parameter`() {
        val q = "\"\"\""
        runSnapshot(
            "SealedCopySnapshotTest.kdoc",
            """
            package snap.sealedCopy.kdoc

            import me.tbsten.cream.KDoc
            import me.tbsten.cream.SealedCopy

            @SealedCopy(
                kdoc = KDoc(
                    description = "Prefer this over a hand-written when().",
                    examples = [
                        $q
                        # Note

                        ```kt
                        val next = state.copy(name = "x")
                        ```
                        $q,
                    ],
                ),
            )
            sealed interface MyState {
                val name: String

                data class Loading(override val name: String) : MyState
                data class Success(override val name: String, val data: String) : MyState
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `data class only flat hierarchy`() {
        runSnapshot(
            "SealedCopySnapshotTest.dataClassOnly",
            """
            package snap.sealedCopy

            import me.tbsten.cream.SealedCopy

            @SealedCopy
            sealed interface MyState {
                val name: String
                val count: Int

                data class Loading(override val name: String, override val count: Int) : MyState
                data class Success(override val name: String, override val count: Int, val data: String) : MyState
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `funName overrides the generated extension name`() {
        runSnapshot(
            "SealedCopySnapshotTest.customExtensionName",
            """
            package snap.sealedCopy.funName

            import me.tbsten.cream.SealedCopy

            @SealedCopy(funName = "updated")
            sealed interface MyState {
                val name: String

                data class Loading(override val name: String) : MyState
                data class Success(override val name: String, val data: String) : MyState
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `dispatches to a non-data class via a SealedCopy_Map annotated function`() {
        runSnapshot(
            "SealedCopySnapshotTest.customFunName",
            """
            package snap.sealedCopy.custom

            import me.tbsten.cream.SealedCopy

            @SealedCopy
            sealed interface MyState {
                val name: String

                data class Loading(override val name: String) : MyState

                class Custom(override val name: String) : MyState {
                    @SealedCopy.Map
                    fun cloneWith(name: String = this.name): Custom = Custom(name)
                }
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `dispatches to a manually written copy on a non-data class`() {
        runSnapshot(
            "SealedCopySnapshotTest.manualCopy",
            """
            package snap.sealedCopy.custom

            import me.tbsten.cream.SealedCopy

            @SealedCopy
            sealed interface MyState {
                val name: String

                data class Loading(override val name: String) : MyState

                class Manual(override val name: String, val cache: String) : MyState {
                    fun copy(name: String = this.name): Manual = Manual(name, cache)
                }
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `forwards type parameters across the generated copy`() {
        runSnapshot(
            "SealedCopySnapshotTest.edgeCase/typeParameterPropagation",
            """
            package snap.sealedCopy.generic

            import me.tbsten.cream.SealedCopy

            @SealedCopy
            sealed interface Result<T> {
                val timestamp: Long

                data class Success<T>(override val timestamp: Long, val value: T) : Result<T>
                data class Failure<T>(override val timestamp: Long, val error: Throwable) : Result<T>
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `keeps subtype type arguments when copying a property of the type parameter`() {
        runSnapshot(
            "SealedCopySnapshotTest.edgeCase/genericProperty",
            """
            package snap.sealedCopy.genericProp

            import me.tbsten.cream.SealedCopy

            @SealedCopy
            sealed interface Box<T> {
                val value: T

                data class Filled<T>(override val value: T, val label: String) : Box<T>
                data class Plain<T>(override val value: T) : Box<T>
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `forwards multiple type parameters into each subtype branch`() {
        runSnapshot(
            "SealedCopySnapshotTest.edgeCase/multipleTypeParameters",
            """
            package snap.sealedCopy.multiGeneric

            import me.tbsten.cream.SealedCopy

            @SealedCopy
            sealed interface Entry<K, V> {
                val key: K
                val value: V

                data class Impl<K, V>(override val key: K, override val value: V, val tag: String) : Entry<K, V>
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `covariant out type parameter is preserved`() {
        runSnapshot(
            "SealedCopySnapshotTest.edgeCase/covariantOutTypeParameter",
            """
            package snap.sealedCopy.cov

            import me.tbsten.cream.SealedCopy

            @SealedCopy
            sealed interface Box<out T> {
                val value: T

                data class Filled<T>(override val value: T) : Box<T>
                data class Plain<T>(override val value: T) : Box<T>
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `subtype with an extra type parameter is star-projected`() {
        runSnapshot(
            "SealedCopySnapshotTest.edgeCase/subtypeExtraTypeParameter",
            """
            package snap.sealedCopy.extra

            import me.tbsten.cream.SealedCopy

            @SealedCopy
            sealed interface Box<T> {
                val value: T

                data class Tagged<T, M>(override val value: T, val meta: M) : Box<T>
                data class Plain<T>(override val value: T) : Box<T>
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `flattens a nested generic sealed hierarchy`() {
        runSnapshot(
            "SealedCopySnapshotTest.edgeCase/nestedGenericSealed",
            """
            package snap.sealedCopy.nestedGen

            import me.tbsten.cream.SealedCopy

            @SealedCopy
            sealed interface Outer<T> {
                val value: T
            }

            sealed interface Mid<T> : Outer<T>

            data class Leaf<T>(override val value: T) : Mid<T>
            data class Other<T>(override val value: T) : Outer<T>
            """.trimIndent(),
        )
    }

    @Test
    fun `renders a where clause for multiple upper bounds`() {
        runSnapshot(
            "SealedCopySnapshotTest.edgeCase/multipleUpperBounds",
            """
            package snap.sealedCopy.bounds

            import me.tbsten.cream.SealedCopy

            @SealedCopy
            sealed interface Box<T> where T : Comparable<T>, T : Any {
                val value: T

                data class Filled<T>(override val value: T) : Box<T> where T : Comparable<T>, T : Any
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `supports a subtype that fixes the type parameter to a concrete type`() {
        runSnapshot(
            "SealedCopySnapshotTest.edgeCase/subtypeFixesTypeParameter",
            """
            package snap.sealedCopy.fixed

            import me.tbsten.cream.SealedCopy

            @SealedCopy
            sealed interface Box<T> {
                val value: T

                data class Filled<T>(override val value: T) : Box<T>
                data class IntBox(override val value: Int) : Box<Int>
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `recursively flattens nested sealed into concrete subclasses`() {
        runSnapshot(
            "SealedCopySnapshotTest.edgeCase/flattensIntermediateSealed",
            """
            package snap.sealedCopy.nested

            import me.tbsten.cream.SealedCopy

            @SealedCopy
            sealed interface Animal {
                val name: String
            }

            sealed interface Human : Animal

            data class Man(override val name: String, val beard: Boolean) : Human
            data class Woman(override val name: String, val hairLength: Int) : Human
            data class Dog(override val name: String, val breed: String) : Animal
            """.trimIndent(),
        )
    }

    @Test
    fun `RETURN_AS_IS collapses non-copyable branches to this`() {
        runSnapshot(
            "SealedCopySnapshotTest.returnAsIs",
            """
            package snap.sealedCopy.asIs

            import me.tbsten.cream.NonCopyableStrategy
            import me.tbsten.cream.SealedCopy

            @SealedCopy(nonCopyableStrategy = NonCopyableStrategy.RETURN_AS_IS)
            sealed interface MyState {
                val name: String

                data class Loading(override val name: String) : MyState
                data object Empty : MyState { override val name: String = "" }
                class Frozen(override val name: String) : MyState
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `RETURN_NULL widens the return type to nullable`() {
        runSnapshot(
            "SealedCopySnapshotTest.returnNull",
            """
            package snap.sealedCopy.returnNull

            import me.tbsten.cream.NonCopyableStrategy
            import me.tbsten.cream.SealedCopy

            @SealedCopy(nonCopyableStrategy = NonCopyableStrategy.RETURN_NULL)
            sealed interface MyState {
                val name: String

                data class Loading(override val name: String) : MyState
                data object Empty : MyState { override val name: String = "" }
                class Frozen(override val name: String) : MyState
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `stacking multiple SealedCopy emits one function per annotation`() {
        runSnapshot(
            "SealedCopySnapshotTest.repeatableMultipleVariants",
            """
            package snap.sealedCopy.repeatable

            import me.tbsten.cream.NonCopyableStrategy
            import me.tbsten.cream.SealedCopy

            @SealedCopy(funName = "withUpdated", nonCopyableStrategy = NonCopyableStrategy.RETURN_AS_IS)
            @SealedCopy(funName = "withUpdatedOrNull", nonCopyableStrategy = NonCopyableStrategy.RETURN_NULL)
            sealed interface MyState {
                val name: String

                data class Loading(override val name: String) : MyState
                data object Empty : MyState { override val name: String = "" }
            }
            """.trimIndent(),
        )
    }
}
