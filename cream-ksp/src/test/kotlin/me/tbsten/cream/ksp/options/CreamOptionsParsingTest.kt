package me.tbsten.cream.ksp.options

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.InvalidCreamOptionException

/**
 * Pure parsing unit tests for [toCreamOptions] focused on the `cream.defaultVisibility` option
 * (issue #137). These don't run a KSP compilation; they pin the Map -> [CreamOptions] mapping
 * directly. The build-failure (diagnostic) surface for an invalid value is covered separately by
 * [me.tbsten.cream.ksp.OptionsDiagnosticTest].
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

        "autoValueClassMapping defaults to true when the option is absent" {
            emptyMap<String, String>().toCreamOptions().autoValueClassMapping shouldBe true
            CreamOptions.default.autoValueClassMapping shouldBe true
        }

        "autoValueClassMapping is disabled only by an explicit false (case-insensitive)" {
            mapOf("cream.autoValueClassMapping" to "false").toCreamOptions().autoValueClassMapping shouldBe false
            mapOf("cream.autoValueClassMapping" to "FALSE").toCreamOptions().autoValueClassMapping shouldBe false
            mapOf("cream.autoValueClassMapping" to "true").toCreamOptions().autoValueClassMapping shouldBe true
            // lenient boolean parsing, mirroring notCopyToObject: any non-"false" value keeps it on
            mapOf("cream.autoValueClassMapping" to "yes").toCreamOptions().autoValueClassMapping shouldBe true
        }
    })
