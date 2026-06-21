package me.tbsten.cream.ksp.testing.kotlincodelikestring.cream

import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.testing.kotlincodelikestring.buildKotlinCodeLikeString
import me.tbsten.cream.ksp.testing.kotlincodelikestring.withPrefixEachLines

fun CreamOptions.toKspConfigString() =
    """
    ksp {
${
        CreamOptions.properties
            .joinToString("\n") { optionProperty ->
                val optionValue = optionProperty.get(this)
                val optionValueString =
                    when (optionValue) {
                        is String -> buildKotlinCodeLikeString(optionValue)
                        is Boolean -> "\"${buildKotlinCodeLikeString(optionValue)}\""
                        is Enum<*> -> buildKotlinCodeLikeString(optionValue.name)
                        else -> error("Unknown CreamOptions property type: ${optionValue?.let { it::class.simpleName }} ($optionValue)")
                    } + if (optionValue == optionProperty.get(CreamOptions.default)) " /* default */" else ""

                "arg(${buildKotlinCodeLikeString(optionProperty.name)}, $optionValueString)"
            }
            .withPrefixEachLines("    ".repeat(2))
    }
    }
    """.trimIndent()
