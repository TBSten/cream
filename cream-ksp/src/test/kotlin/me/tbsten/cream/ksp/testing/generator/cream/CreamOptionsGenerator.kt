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
 * cartesian space of the five option axes (via [combine]), while its deterministic
 * [representativeValues][Generator.representativeValues] are a small hand-picked set (via
 * [withRepresentativeValues]) centered on [CreamOptions.default] — the full 2×3×2×2×2 = 48 product is
 * too many for snapshot / example use. Each axis defaults to its per-axis factory below, and any axis
 * can be overridden by passing a custom [Generator] (e.g. to pin or widen a single option).
 *
 * Pass `namingOptionsApply = false` for a feature whose generated name ignores the project naming
 * options — `@SealedCopy`'s `copy`, which is a fixed default name rather than a derived one. Its
 * representative sets then collapse to the ones that can actually change its output.
 */
internal fun Generator.Companion.validCreamOptions(
    copyFunNamePrefix: Generator<String> = copyFunNamePrefix(),
    copyFunNamingStrategy: Generator<CopyFunNamingStrategy> = copyFunNamingStrategy(),
    escapeDot: Generator<EscapeDot> = escapeDot(),
    notCopyToObject: Generator<Boolean> = notCopyToObject(),
    defaultVisibility: Generator<CopyVisibility> = defaultVisibility(),
    namingOptionsApply: Boolean = true,
): Generator<CreamOptions> =
    combine(
        copyFunNamePrefix.mapLabel { "copyFunNamePrefix=$it" },
        copyFunNamingStrategy.mapLabel { "copyFunNamingStrategy=$it" },
        escapeDot.mapLabel { "escapeDot=$it" },
        notCopyToObject.mapLabel { "notCopyToObject=$it" },
        defaultVisibility.mapLabel { "defaultVisibility=$it" },
    ) { prefix, strategy, escape, notCopyObject, visibility ->
        CreamOptions(
            copyFunNamePrefix = prefix,
            copyFunNamingStrategy = strategy,
            escapeDot = escape,
            notCopyToObject = notCopyObject,
            defaultVisibility = visibility,
            // Pinned to the default (true): varying it here would multiply every snapshot family's
            // compile count. The option is covered by the targeted ValueClassMappingOptionTest.
            autoValueClassMapping = CreamOptions.default.autoValueClassMapping,
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
                notCopyToObject = true,
                copyFunNamingStrategy = CopyFunNamingStrategy.`inner-name`,
                defaultVisibility = CopyVisibility.INTERNAL,
            ),
        ).map { options -> if (namingOptionsApply) options else options.withDefaultNamingOptions() }
            .distinct()
            .forEach { options -> creamOptionsLabel(options) case options }
    }

/**
 * The single `Default` representative, for a snapshot **family** no project option can move.
 *
 * [validCreamOptions]`(namingOptionsApply = false)` narrows a whole suite to the option sets that can
 * still change its output; this narrows one family inside a suite that legitimately needs the full
 * matrix elsewhere. Reach for it only when the extra sets would re-prove what the suite's `funName` /
 * `visibility` families already pin — i.e. the family's goldens would differ from `Default` in
 * nothing but the echoed options, the generated function's name and its visibility modifier.
 */
internal fun Generator.Companion.defaultCreamOptionsOnly(): Generator<CreamOptions> =
    validCreamOptions().withRepresentativeValues {
        creamOptionsLabel(CreamOptions.default) case CreamOptions.default
    }

/**
 * Reset the naming axes (`copyFunNamePrefix` / `copyFunNamingStrategy` / `escapeDot`) to their
 * defaults, for a feature whose generated name ignores them.
 *
 * Their representative values would otherwise multiply the golden matrix without changing a byte
 * of generated code, and the surviving sets' labels would advertise options that had no effect.
 * After the reset, sets that differ only in those axes collapse into one.
 */
private fun CreamOptions.withDefaultNamingOptions(): CreamOptions =
    copy(
        copyFunNamePrefix = CreamOptions.default.copyFunNamePrefix,
        copyFunNamingStrategy = CreamOptions.default.copyFunNamingStrategy,
        escapeDot = CreamOptions.default.escapeDot,
    )

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

/** `defaultVisibility` の軸 generator。default の INHERIT ＋ 非 INHERIT な INTERNAL。 */
internal fun Generator.Companion.defaultVisibility(
    representativeValues: List<Pair<String?, CopyVisibility>> =
        listOf(
            "Default" to CopyVisibility.INHERIT, // CreamOptions.default.defaultVisibility
            "INTERNAL" to CopyVisibility.INTERNAL,
        ),
) = generator {
    cases(representativeValues)
    Arb.of(representativeValues.map { it.second })
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
            if (options.autoValueClassMapping != default.autoValueClassMapping) add("autoValueClassMapping=${options.autoValueClassMapping}")
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
