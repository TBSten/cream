package me.tbsten.cream.ksp.feature.callFrom

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.ksp.testing.compile.compileWithCream
import me.tbsten.cream.ksp.testing.compile.normalizedCompilerOutput
import me.tbsten.cream.ksp.testing.snapshot.assertMatchesSnapshot
import org.intellij.lang.annotations.Language

/**
 * Diagnostics for invalid `@CallFrom` usage. Each unsupported shape from
 * `doc/internal/design/10-call-from.md` must fail with a clean positioned `COMPILATION_ERROR`,
 * never a partial or broken generated file:
 *
 * - unsupported function kinds: private / protected / local / abstract / expect /
 *   member extension (double receiver) / member of a generic class (including via a generic
 *   `inner` chain / a local class) / `reified` type parameter / `ERROR`-or-`HIDDEN`-deprecated,
 * - annotation-shape errors: empty sources, duplicated sources, bridge-parameter name collision,
 * - visibility violations: a `private` source class, an explicit `visibility = PUBLIC` above the
 *   `internal` cap imposed by a source class,
 * - overload collisions: two annotated functions whose bridges share a signature, and a bridge
 *   whose signature already exists as a user-written function.
 *
 * Note: a member function of an anonymous object is NOT testable here — KSP never surfaces it
 * via getSymbolsWithAnnotation (even with inDepth = true), so the annotation is silently ignored
 * and no diagnostic is reachable. The `qualifiedName == null` guard in the local-scope check
 * stays as a defensive path only.
 */
internal class CallFromInvalidUsageTest :
    FreeSpec({
        /**
         * Compile [source], assert a non-OK exit with [expectedMessagePart] in the output, and
         * freeze the compiler output as the `<snapshotName>.output` golden.
         */
        fun assertInvalidUsage(
            snapshotName: String,
            expectedMessagePart: String,
            @Language("kotlin") source: String,
            kotlincArguments: List<String> = emptyList(),
        ) {
            val result = compileWithCream(source, kotlincArguments = kotlincArguments)
            withClue(result.messages) {
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
                result.normalizedCompilerOutput() shouldContain expectedMessagePart
            }
            assertMatchesSnapshot(name = "CallFromInvalidUsageTest.$snapshotName.output") {
                facet("Compiler output", result.normalizedCompilerOutput(), lang = "text")
                "Input" facetOf source
            }
        }

        "privateFunction" {
            assertInvalidUsage(
                "privateFunction",
                "private/protected",
                """
                package callfrom.diag

                import me.tbsten.cream.CallFrom

                data class Args(val value: String)

                @CallFrom(Args::class)
                private fun handle(value: String) { }
                """.trimIndent(),
            )
        }

        // A top-level extension function is a SUPPORTED shape (snapshot family `12--extension`);
        // only the member extension — which would need both a dispatch and an extension
        // receiver — is rejected.
        "memberExtensionFunction" {
            assertInvalidUsage(
                "memberExtensionFunction",
                "member extension function",
                """
                package callfrom.diag

                import me.tbsten.cream.CallFrom

                data class Args(val value: String)

                class Handler {
                    @CallFrom(Args::class)
                    fun String.handle(value: String) { }
                }
                """.trimIndent(),
            )
        }

        "protectedFunction" {
            assertInvalidUsage(
                "protectedFunction",
                "private/protected",
                """
                package callfrom.diag

                import me.tbsten.cream.CallFrom

                data class Args(val value: String)

                open class Handler {
                    @CallFrom(Args::class)
                    protected fun handle(value: String) { }
                }
                """.trimIndent(),
            )
        }

        "localFunction" {
            assertInvalidUsage(
                "localFunction",
                "local function",
                """
                package callfrom.diag

                import me.tbsten.cream.CallFrom

                data class Args(val value: String)

                fun outer() {
                    @CallFrom(Args::class)
                    fun local(value: String) { }
                    local("")
                }
                """.trimIndent(),
            )
        }

        "memberFunctionInLocalClass" {
            assertInvalidUsage(
                "memberFunctionInLocalClass",
                "local function",
                """
                package callfrom.diag

                import me.tbsten.cream.CallFrom

                data class Args(val value: String)

                fun outer() {
                    class Local {
                        @CallFrom(Args::class)
                        fun handle(value: String) { }
                    }
                    Local().handle("")
                }
                """.trimIndent(),
            )
        }

        "abstractFunction" {
            assertInvalidUsage(
                "abstractFunction",
                "abstract function",
                """
                package callfrom.diag

                import me.tbsten.cream.CallFrom

                data class Args(val value: String)

                abstract class Handler {
                    @CallFrom(Args::class)
                    abstract fun handle(value: String)
                }
                """.trimIndent(),
            )
        }

        "memberFunctionInGenericClass" {
            assertInvalidUsage(
                "memberFunctionInGenericClass",
                "generic class",
                """
                package callfrom.diag

                import me.tbsten.cream.CallFrom

                data class Args(val value: String)

                class Container<T>(val item: T) {
                    @CallFrom(Args::class)
                    fun handle(value: String) { }
                }
                """.trimIndent(),
            )
        }

        "memberFunctionInInnerClassOfGenericClass" {
            assertInvalidUsage(
                "memberFunctionInInnerClassOfGenericClass",
                "generic class",
                """
                package callfrom.diag

                import me.tbsten.cream.CallFrom

                data class Args(val value: String)

                class Outer<T>(val item: T) {
                    inner class Inner {
                        @CallFrom(Args::class)
                        fun handle(value: String) { }
                    }
                }
                """.trimIndent(),
            )
        }

        "reifiedTypeParameter" {
            assertInvalidUsage(
                "reifiedTypeParameter",
                "reified",
                """
                package callfrom.diag

                import me.tbsten.cream.CallFrom

                data class Args(val json: String)

                @CallFrom(Args::class)
                inline fun <reified T> parse(json: String): T = TODO()
                """.trimIndent(),
            )
        }

        "sourceParamNameCollision" {
            assertInvalidUsage(
                "sourceParamNameCollision",
                "collides",
                """
                package callfrom.diag

                import me.tbsten.cream.CallFrom

                data class Args(val value: String)

                @CallFrom(Args::class)
                fun handle(args: String, value: String) { }
                """.trimIndent(),
            )
        }

        "duplicatedSources" {
            assertInvalidUsage(
                "duplicatedSources",
                "more than once",
                """
                package callfrom.diag

                import me.tbsten.cream.CallFrom

                data class Args(val value: String)

                @CallFrom(Args::class, Args::class)
                fun handle(value: String) { }
                """.trimIndent(),
            )
        }

        "emptySources" {
            assertInvalidUsage(
                "emptySources",
                "no source classes",
                """
                package callfrom.diag

                import me.tbsten.cream.CallFrom

                @CallFrom
                fun handle(value: String) { }
                """.trimIndent(),
            )
        }

        "expectFunction" {
            assertInvalidUsage(
                "expectFunction",
                "expect function",
                """
                package callfrom.diag

                import me.tbsten.cream.CallFrom

                data class Args(val value: String)

                @CallFrom(Args::class)
                expect fun handle(value: String)
                """.trimIndent(),
                kotlincArguments = listOf("-Xmulti-platform"),
            )
        }

        "deprecatedErrorFunction" {
            assertInvalidUsage(
                "deprecatedErrorFunction",
                "deprecated with level ERROR",
                """
                package callfrom.diag

                import me.tbsten.cream.CallFrom

                data class Args(val value: String)

                @Deprecated("Use handleV2 instead.", level = DeprecationLevel.ERROR)
                @CallFrom(Args::class)
                fun handle(value: String) { }
                """.trimIndent(),
            )
        }

        "deprecatedErrorSourceClass" {
            assertInvalidUsage(
                "deprecatedErrorSourceClass",
                "deprecated with level",
                """
                package callfrom.diag

                import me.tbsten.cream.CallFrom

                @Deprecated("Use ArgsV2 instead.", level = DeprecationLevel.ERROR)
                data class Args(val value: String)

                @CallFrom(Args::class)
                fun handle(value: String) { }
                """.trimIndent(),
            )
        }

        "privateSourceClass" {
            assertInvalidUsage(
                "privateSourceClass",
                "private",
                """
                package callfrom.diag

                import me.tbsten.cream.CallFrom

                private data class Args(val value: String)

                @CallFrom(Args::class)
                fun handle(value: String) { }
                """.trimIndent(),
            )
        }

        "internalSourceExplicitPublicVisibility" {
            assertInvalidUsage(
                "internalSourceExplicitPublicVisibility",
                "must not expose an internal type",
                """
                package callfrom.diag

                import me.tbsten.cream.CallFrom
                import me.tbsten.cream.CopyVisibility

                internal data class Args(val value: String)

                @CallFrom(Args::class, visibility = CopyVisibility.PUBLIC)
                fun handle(value: String) { }
                """.trimIndent(),
            )
        }

        "duplicateBridgeSignatures" {
            assertInvalidUsage(
                "duplicateBridgeSignatures",
                "same signature as the bridge",
                """
                package callfrom.diag

                import me.tbsten.cream.CallFrom

                data class Args(val value: String)

                @CallFrom(Args::class)
                fun handle(first: String) { }

                @CallFrom(Args::class)
                fun handle(second: String) { }
                """.trimIndent(),
            )
        }

        "existingOverloadClash" {
            assertInvalidUsage(
                "existingOverloadClash",
                "same signature as the existing function",
                """
                package callfrom.diag

                import me.tbsten.cream.CallFrom

                data class Args(val value: String)

                fun handle(args: Args, value: String) { }

                @CallFrom(Args::class)
                fun handle(value: String) { }
                """.trimIndent(),
            )
        }

        // Two differently-named functions given the SAME custom funName produce two same-named
        // bridges; with identical parameter types they collide, so cream fails fast (before any
        // file is generated) instead of emitting conflicting overloads. Distinct funNames — or
        // distinct sources — would avoid it (see snapshot family `17--funName`).
        "customFunNameBridgeClash" {
            assertInvalidUsage(
                "customFunNameBridgeClash",
                "same signature as the bridge",
                """
                package callfrom.diag

                import me.tbsten.cream.CallFrom

                data class Args(val value: String)

                @CallFrom(Args::class, funName = "create")
                fun alpha(value: String) { }

                @CallFrom(Args::class, funName = "create")
                fun beta(value: String) { }
                """.trimIndent(),
            )
        }
    })
