package me.tbsten.cream.ksp.testing.generator.cream

import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.of
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.options.CopyFunNamingStrategy
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.options.EscapeDot
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.GeneratorBuilder
import me.tbsten.cream.ksp.testing.generator.generator
import me.tbsten.cream.ksp.testing.generator.util.combine
import me.tbsten.cream.ksp.testing.generator.util.mapLabel
import me.tbsten.cream.ksp.testing.generator.util.withRepresentativeValues

/**
 * A [CreamOptions] generator with two independent sides: its [arb][Generator.arb] samples the full
 * cartesian space of the four option axes (via [combine]), while its deterministic
 * [representativeValues][Generator.representativeValues] are a small hand-picked set (via
 * [withRepresentativeValues]) centered on [CreamOptions.default] — the full 2×3×2×2 = 24 product is
 * too many for snapshot / example use. Each axis defaults to its per-axis factory below.
 */
internal fun Generator.Companion.validCreamOptions(
    copyFunNamePrefix: Generator<String> = copyFunNamePrefix(),
    copyFunNamingStrategy: Generator<CopyFunNamingStrategy> = copyFunNamingStrategy(),
    escapeDot: Generator<EscapeDot> = escapeDot(),
    notCopyToObject: Generator<Boolean> = notCopyToObject(),
): Generator<CreamOptions> =
    combine(
        copyFunNamePrefix.mapLabel { "copyFunNamePrefix=$it" },
        copyFunNamingStrategy.mapLabel { "copyFunNamingStrategy=$it" },
        escapeDot.mapLabel { "escapeDot=$it" },
        notCopyToObject.mapLabel { "notCopyToObject=$it" },
    ) { prefix, strategy, escape, notCopyObject ->
        CreamOptions(
            copyFunNamePrefix = prefix,
            copyFunNamingStrategy = strategy,
            escapeDot = escape,
            notCopyToObject = notCopyObject,
            // The arb side pins `defaultVisibility` to its default; the representative set below adds a
            // single INTERNAL case so snapshots golden-pin the non-INHERIT modifier without multiplying
            // every axis. Full precedence behavior is also covered by DefaultVisibilityOptionTest.
            defaultVisibility = CreamOptions.default.defaultVisibility,
        )
    }.withRepresentativeValues {
        listOf(
            CreamOptions.default,
            CreamOptions.default.copy(
                copyFunNamePrefix = "to",
                copyFunNamingStrategy = CopyFunNamingStrategy.`under-package`,
                escapeDot = EscapeDot.`replace-to-underscore`,
            ),
            CreamOptions.default.copy(
                copyFunNamePrefix = "to",
                copyFunNamingStrategy = CopyFunNamingStrategy.`inner-name`,
                escapeDot = EscapeDot.`replace-to-underscore`,
            ),
            CreamOptions.default.copy(defaultVisibility = CopyVisibility.INTERNAL),
        ).forEach { options -> creamOptionsLabel(options) case options }
    }

/**
 * `copyFunNamePrefix` の軸 generator。代表は default の "copyTo" と短い "to"。arb は「Kotlin 識別子に
 * 使える文字の繰り返し」（先頭=英字/アンダースコア、以降=英数字/アンダースコア）で常に有効な識別子を作る。
 */
internal fun Generator.Companion.copyFunNamePrefix(
    representativeValues: List<Pair<String?, String>> =
        listOf(
            "Default" to "copyTo", // CreamOptions.default.copyFunNamePrefix
            "to" to "to",
        ),
) = generator {
    cases(representativeValues)

    val identifierStart = ('a'..'z') + ('A'..'Z') + '_'
    val identifierPart = identifierStart + ('0'..'9')
    Arb.bind(Arb.of(identifierStart), Arb.list(Arb.of(identifierPart), 0..11)) { first, rest ->
        (listOf(first) + rest).joinToString(separator = "")
    }
}

/** `copyFunNamingStrategy` の軸 generator。default の under-package ＋ 対照的な simple-name / full-name。 */
internal fun Generator.Companion.copyFunNamingStrategy(
    representativeValues: List<Pair<String?, CopyFunNamingStrategy>> =
        listOf(
            "Default" to CopyFunNamingStrategy.`under-package`, // CreamOptions.default.copyFunNamingStrategy
            "simple-name" to CopyFunNamingStrategy.`simple-name`,
            "full-name" to CopyFunNamingStrategy.`full-name`,
        ),
) = generator {
    cases(representativeValues)
    Arb.of(representativeValues.map { it.second })
}

/** `escapeDot` の軸 generator。default の lower-camel-case ＋ replace-to-underscore。 */
internal fun Generator.Companion.escapeDot(
    representativeValues: List<Pair<String?, EscapeDot>> =
        listOf(
            "Default" to EscapeDot.`lower-camel-case`, // CreamOptions.default.escapeDot
            "replace-to-underscore" to EscapeDot.`replace-to-underscore`,
        ),
) = generator {
    cases(representativeValues)
    Arb.of(representativeValues.map { it.second })
}

/** `notCopyToObject` の軸 generator。default の false ＋ true。 */
internal fun Generator.Companion.notCopyToObject(
    representativeValues: List<Pair<String?, Boolean>> =
        listOf(
            "Default" to false, // CreamOptions.default.notCopyToObject
            "true" to true,
        ),
) = generator {
    cases(representativeValues)
    Arb.boolean()
}

private fun creamOptionsLabel(options: CreamOptions): String {
    val default = CreamOptions.default
    val parts =
        buildList {
            if (options.copyFunNamePrefix != default.copyFunNamePrefix) add("prefix=${options.copyFunNamePrefix}")
            if (options.copyFunNamingStrategy != default.copyFunNamingStrategy) add("strategy=${options.copyFunNamingStrategy.name}")
            if (options.escapeDot != default.escapeDot) add("escapeDot=${options.escapeDot.name}")
            if (options.notCopyToObject != default.notCopyToObject) add("notCopyToObject=${options.notCopyToObject}")
            if (options.defaultVisibility != default.defaultVisibility) add("defaultVisibility=${options.defaultVisibility.name}")
        }
    return if (parts.isEmpty()) "Default" else parts.joinToString(separator = ", ", prefix = "(", postfix = ")")
}

private fun <T> GeneratorBuilder<T>.cases(representativeValues: List<Pair<String?, T>>) {
    representativeValues.forEach { (label, representativeValue) ->
        if (label == null) {
            case(representativeValue)
        } else {
            label case representativeValue
        }
    }
}
