package me.tbsten.cream.ksp.diagnostic

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.normalizedCompilerOutput

internal class FunNameDiagnosticTest :
    FunSpec({
        test("a plain-literal funName that is not a valid identifier fails compilation") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyTo

                @CopyTo(Target::class, funName = "to-target")
                data class Source(val shared: String)

                data class Target(val shared: String, val extra: Int)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("FunNameDiagnosticTest.invalidLiteral.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        test("a token that expands to an invalid identifier fails compilation") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyTo
                import me.tbsten.cream.CopyTargetSimpleName

                @CopyTo(Target::class, funName = "bad-" + CopyTargetSimpleName)
                data class Source(val shared: String)

                data class Target(val shared: String, val extra: Int)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("FunNameDiagnosticTest.invalidViaToken.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

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

        test("an empty funName fails compilation") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyTo

                @CopyTo(Target::class, funName = "")
                data class Source(val shared: String)

                data class Target(val shared: String, val extra: Int)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("FunNameDiagnosticTest.emptyName.output") {
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

        test("an invalid funName on @SealedCopy fails compilation") {
            val source =
                """
                package diag

                import me.tbsten.cream.SealedCopy

                @SealedCopy(funName = "bad-name")
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
            assertMatchesSnapshot("FunNameDiagnosticTest.sealedCopyInvalid.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        // The design left this combination as an open question; pin the chosen behaviour:
        // under escapeDot=backquote the derived default name is already backtick-quoted, so
        // appending a suffix yields an invalid name and cream rejects it with a clear error
        // (rather than silently emitting code that fails at the user's compiler).
        test("DefaultCopyFunctionName plus a suffix is rejected under escapeDot=backquote") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyTo
                import me.tbsten.cream.DefaultCopyFunctionName

                @CopyTo(Target::class, funName = DefaultCopyFunctionName + "OrNull")
                data class Source(val shared: String)

                data class Target(val shared: String, val extra: Int)
                """.trimIndent()
            val result = compileWithCream(source, options = mapOf("cream.escapeDot" to "backquote"))

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("FunNameDiagnosticTest.defaultPlusSuffixBackquote.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        // A plain-literal funName that is a Kotlin hard keyword would generate `fun X.is(...)`,
        // which fails at the user's compiler. cream rejects it with a clear error that points
        // the user at backtick-quoting.
        test("a funName that is a Kotlin keyword fails compilation with a clear error") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyTo

                @CopyTo(Target::class, funName = "is")
                data class Source(val shared: String)

                data class Target(val shared: String, val extra: Int)
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("FunNameDiagnosticTest.keywordName.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        // @CombineFrom is @Repeatable and merges all occurrences into one function, so stacked
        // occurrences with different explicit funName values are ambiguous and rejected (rather
        // than silently honouring only the first).
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

        // The invalid-funName error must name the *annotated* declaration (the object), not the
        // mapped source class (which the user does not own and never annotated).
        test("an invalid funName on @CopyMapping names the annotated object, not the source class") {
            val source =
                """
                package diag

                import me.tbsten.cream.CopyMapping

                data class LibX(val shared: String)
                data class LibY(val shared: String, val extra: Int)

                @CopyMapping(LibX::class, LibY::class, funName = "to-y")
                object Mapping
                """.trimIndent()
            val result = compileWithCream(source)

            withClue("Compilation should fail. Output:\n${result.normalizedCompilerOutput()}") {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
            assertMatchesSnapshot("FunNameDiagnosticTest.copyMappingInvalidAttribution.output") {
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
    })
