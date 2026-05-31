package me.tbsten.cream.ksp.diagnostic

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput

// cream does not validate the Kotlin-legality of a funName (keywords, illegal characters, …) —
// such names simply fail to compile at the use site. These diagnostics cover the cases cream
// *does* reject up front: fan-out / repeatable collisions, where a fixed name would produce more
// than one function with the same signature.
internal class FunNameDiagnosticTest :
    FunSpec({
        test("a plain-literal funName with multiple targets fails compilation") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyTo

                @CopyTo(A::class, B::class, funName = "toThem")
                data class Source(val shared: String)

                data class A(val shared: String, val a: Int)
                data class B(val shared: String, val b: Int)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("FunNameDiagnosticTest.literalFanoutMultiTarget.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("a plain-literal funName with a sealed target fails compilation") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyTo

                @CopyTo(State::class, funName = "toState")
                data class Source(val id: String)

                sealed interface State {
                    val id: String

                    data class Loading(override val id: String) : State
                    data class Loaded(override val id: String, val payload: Int) : State
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("FunNameDiagnosticTest.literalFanoutSealed.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("a plain-literal funName on @CopyFrom with multiple sources fails compilation") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyFrom

                data class A(val shared: String)
                data class B(val shared: String)

                @CopyFrom(A::class, B::class, funName = "fromThem")
                data class Target(val shared: String)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("FunNameDiagnosticTest.copyFromLiteralFanout.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("a plain-literal funName on @CombineTo with multiple targets fails compilation") {
            val source =
                """
                package diag

                import me.tbsten.cream.CombineTo

                @CombineTo(A::class, B::class, funName = "toThem")
                data class Source(val shared: String)

                data class A(val shared: String, val x: Int)
                data class B(val shared: String, val y: Int)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("FunNameDiagnosticTest.combineToLiteralFanout.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("a plain-literal funName on a reversible @CopyMapping fails compilation") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyMapping

                data class A(val shared: String)
                data class B(val shared: String)

                @CopyMapping(A::class, B::class, canReverse = true, funName = "convert")
                object Mapping
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("FunNameDiagnosticTest.copyMappingCanReverseLiteral.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        // @CombineFrom is @Repeatable and merges all occurrences into one function, so stacked
        // occurrences with different explicit funName values are ambiguous and rejected.
        test("repeated @CombineFrom with conflicting funName fails compilation") {
            val source =
                """
                package diag

                import me.tbsten.cream.CombineFrom

                data class A(val a: String)
                data class B(val b: Int)

                @CombineFrom(A::class, funName = "buildX")
                @CombineFrom(B::class, funName = "buildY")
                data class Target(val a: String, val b: Int)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("FunNameDiagnosticTest.combineFromConflictingFunName.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        // @SealedCopy is @Repeatable and all variants land in one file; two annotations that
        // resolve to the same name (here, the same token → the same sealed type name) would emit
        // conflicting overloads, so cream rejects the duplicate.
        test("stacked @SealedCopy resolving to the same name fails compilation") {
            val source =
                """
                package diag

                import me.tbsten.cream.SealedCopy
                import me.tbsten.cream.CopyTargetSimpleName

                @SealedCopy(funName = "to" + CopyTargetSimpleName)
                @SealedCopy(funName = "to" + CopyTargetSimpleName)
                sealed interface State {
                    val id: String

                    data class Loading(override val id: String) : State
                    data class Loaded(override val id: String, val payload: Int) : State
                }
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("FunNameDiagnosticTest.sealedCopyDuplicateName.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        // Stacked @CopyMapping occurrences that resolve to an identical function (same source +
        // target + name) collide; cream rejects them. (Different targets = overloads are allowed —
        // see the snapshot test.)
        test("duplicate @CopyMapping occurrences fail compilation") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyMapping

                data class A(val shared: String)
                data class B(val shared: String, val extra: Int)

                @CopyMapping(A::class, B::class, funName = "conv")
                @CopyMapping(A::class, B::class, funName = "conv")
                object Mapping
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("FunNameDiagnosticTest.copyMappingDuplicate.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("duplicate @CombineMapping occurrences fail compilation") {
            val source =
                """
                package diag

                import me.tbsten.cream.CombineMapping

                data class A(val a: String)
                data class B(val b: Int)
                data class C(val a: String, val b: Int, val extra: Long)

                @CombineMapping(sources = [A::class, B::class], target = C::class, funName = "combine")
                @CombineMapping(sources = [A::class, B::class], target = C::class, funName = "combine")
                object Mapping
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("FunNameDiagnosticTest.combineMappingDuplicate.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }
    })
