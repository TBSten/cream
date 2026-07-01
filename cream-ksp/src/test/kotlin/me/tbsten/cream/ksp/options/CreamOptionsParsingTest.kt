package me.tbsten.cream.ksp.options

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.NonCopyableStrategy
import me.tbsten.cream.ksp.InvalidCreamOptionException

/**
 * Pure parsing unit tests for [toCreamOptions] focused on the `cream.defaultVisibility` (issue #137) and
 * `cream.nonCopyableStrategy` (issue #163) options. These don't run a KSP compilation; they pin the
 * Map -> [CreamOptions] mapping directly. The build-failure (diagnostic) surface for an invalid value is
 * covered separately by [me.tbsten.cream.ksp.OptionsDiagnosticTest].
 */
internal class CreamOptionsParsingTest :
    FreeSpec({
        "defaultVisibility falls back to INHERIT when the option is absent" {
            emptyMap<String, String>().toCreamOptions().defaultVisibility shouldBe CopyVisibility.INHERIT
            CreamOptions.default.defaultVisibility shouldBe CopyVisibility.INHERIT
        }

        "defaultVisibility parses each CopyVisibility name" {
            mapOf("cream.defaultVisibility" to "INHERIT").toCreamOptions().defaultVisibility shouldBe
                CopyVisibility.INHERIT
            mapOf("cream.defaultVisibility" to "PUBLIC").toCreamOptions().defaultVisibility shouldBe
                CopyVisibility.PUBLIC
            mapOf("cream.defaultVisibility" to "INTERNAL").toCreamOptions().defaultVisibility shouldBe
                CopyVisibility.INTERNAL
        }

        "defaultVisibility parsing is case-insensitive" {
            mapOf("cream.defaultVisibility" to "internal").toCreamOptions().defaultVisibility shouldBe
                CopyVisibility.INTERNAL
            mapOf("cream.defaultVisibility" to "Public").toCreamOptions().defaultVisibility shouldBe
                CopyVisibility.PUBLIC
        }

        "an unknown defaultVisibility value throws InvalidCreamOptionException carrying the value" {
            val error =
                shouldThrow<InvalidCreamOptionException> {
                    mapOf("cream.defaultVisibility" to "package-private").toCreamOptions()
                }
            error.message.orEmpty() shouldContain "cream.defaultVisibility"
            error.message.orEmpty() shouldContain "package-private"
        }

        "defaultVisibility is independent of the other options" {
            val options =
                mapOf(
                    "cream.defaultVisibility" to "INTERNAL",
                    "cream.notCopyToObject" to "true",
                ).toCreamOptions()
            options.defaultVisibility shouldBe CopyVisibility.INTERNAL
            options.notCopyToObject shouldBe true
            // unrelated options keep their defaults
            options.copyFunNamePrefix shouldBe CreamOptions.default.copyFunNamePrefix
        }

        "nonCopyableStrategy falls back to INHERIT when the option is absent" {
            emptyMap<String, String>().toCreamOptions().nonCopyableStrategy shouldBe NonCopyableStrategy.INHERIT
            CreamOptions.default.nonCopyableStrategy shouldBe NonCopyableStrategy.INHERIT
        }

        "nonCopyableStrategy parses each NonCopyableStrategy name" {
            mapOf("cream.nonCopyableStrategy" to "INHERIT").toCreamOptions().nonCopyableStrategy shouldBe
                NonCopyableStrategy.INHERIT
            mapOf("cream.nonCopyableStrategy" to "ERROR").toCreamOptions().nonCopyableStrategy shouldBe
                NonCopyableStrategy.ERROR
            mapOf("cream.nonCopyableStrategy" to "RETURN_AS_IS").toCreamOptions().nonCopyableStrategy shouldBe
                NonCopyableStrategy.RETURN_AS_IS
            mapOf("cream.nonCopyableStrategy" to "RETURN_NULL").toCreamOptions().nonCopyableStrategy shouldBe
                NonCopyableStrategy.RETURN_NULL
        }

        "nonCopyableStrategy parsing is case-insensitive" {
            mapOf("cream.nonCopyableStrategy" to "return_as_is").toCreamOptions().nonCopyableStrategy shouldBe
                NonCopyableStrategy.RETURN_AS_IS
            mapOf("cream.nonCopyableStrategy" to "Return_Null").toCreamOptions().nonCopyableStrategy shouldBe
                NonCopyableStrategy.RETURN_NULL
        }

        "an unknown nonCopyableStrategy value throws InvalidCreamOptionException carrying the value" {
            val error =
                shouldThrow<InvalidCreamOptionException> {
                    mapOf("cream.nonCopyableStrategy" to "return-as-is").toCreamOptions()
                }
            error.message.orEmpty() shouldContain "cream.nonCopyableStrategy"
            error.message.orEmpty() shouldContain "return-as-is"
        }

        "nonCopyableStrategy is independent of the other options" {
            val options =
                mapOf(
                    "cream.nonCopyableStrategy" to "RETURN_AS_IS",
                    "cream.defaultVisibility" to "INTERNAL",
                ).toCreamOptions()
            options.nonCopyableStrategy shouldBe NonCopyableStrategy.RETURN_AS_IS
            options.defaultVisibility shouldBe CopyVisibility.INTERNAL
            options.copyFunNamePrefix shouldBe CreamOptions.default.copyFunNamePrefix
        }
    })
