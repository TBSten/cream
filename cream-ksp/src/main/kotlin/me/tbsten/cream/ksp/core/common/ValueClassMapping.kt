package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.util.escapeKotlinIdentifier

/**
 * An automatic `value class` wrap / unwrap between a target constructor parameter and a
 * name-resolved source property (issue #21). Only reached when [findMatchedProperty] found no
 * type-compatible match — a normal match always wins. Render the default with
 * [defaultValueExpression].
 */
internal sealed interface ValueClassConversion {
    /** The name-resolved source property supplying the default value. */
    val sourceProperty: KSPropertyDeclaration

    /** One constructor call per [layer][layers], outermost first: `V2(V1(this.x))`. */
    data class Wrap(
        override val sourceProperty: KSPropertyDeclaration,
        val layers: List<KSType>,
        /** The source is nullable while the constructors are not, so wrap inside `?.let { }`. */
        val useSafeCall: Boolean,
    ) : ValueClassConversion

    /** One underlying-property read per hop: `this.x.v1.value`. */
    data class Unwrap(
        override val sourceProperty: KSPropertyDeclaration,
        val accessors: List<UnderlyingAccess>,
    ) : ValueClassConversion

    /** One unwrap hop: `.name`, or `?.name` after a nullable type. */
    data class UnderlyingAccess(
        val propertyName: String,
        val viaSafeCall: Boolean,
    )
}

/** The result of probing a target constructor parameter for a value-class conversion. */
internal sealed interface ValueClassConversionOutcome {
    data class Applied(
        val conversion: ValueClassConversion,
    ) : ValueClassConversionOutcome

    /**
     * A conversion almost applied but was rejected for a reason the user plausibly did not intend
     * (nullability soundness, visibility). The parameter stays required and emission sites warn
     * with [message] — unless the candidate is `@Exclude`-marked, i.e. an explicit opt-out.
     */
    data class NearMiss(
        val candidate: KSPropertyDeclaration,
        val message: String,
    ) : ValueClassConversionOutcome

    /** Nothing value-class-shaped matched; the parameter stays required. */
    data object None : ValueClassConversionOutcome
}

/**
 * Renders the generated default for this conversion.
 *
 * @param sourceAccess the qualified (already escaped) property access, e.g. `this.id` or `sourceB.id`.
 * @param renderType the emission site's own type renderer, so constructor calls match the rendered
 *   parameter type. Its trailing `?` is stripped — a wrap constructs the non-null value class even
 *   for a nullable parameter, and [asString][me.tbsten.cream.ksp.util.ksp.asString] appends `?`
 *   only for outer nullability, so the strip cannot corrupt nullable type arguments.
 */
internal fun ValueClassConversion.defaultValueExpression(
    sourceAccess: String,
    renderType: (KSType) -> String,
): String =
    when (this) {
        is ValueClassConversion.Wrap -> {
            val open = layers.joinToString("") { "${renderType(it).removeSuffix("?")}(" }
            val close = ")".repeat(layers.size)
            if (useSafeCall) "$sourceAccess?.let { ${open}it$close }" else "$open$sourceAccess$close"
        }

        is ValueClassConversion.Unwrap ->
            sourceAccess +
                accessors.joinToString("") { access ->
                    (if (access.viaSafeCall) "?." else ".") + access.propertyName.escapeKotlinIdentifier()
                }
    }

/**
 * Shorthand for callers that only care about [ValueClassConversionOutcome.Applied]. Emission sites
 * should use [findValueClassConversionOutcome] instead, so near misses can be warned about.
 */
context(options: CreamOptions)
internal fun KSValueParameter.findValueClassConversion(
    source: KSClassDeclaration,
    findMappedSourceProperty: FindMappedSourceProperty,
): ValueClassConversion? = (findValueClassConversionOutcome(source, findMappedSourceProperty) as? ValueClassConversionOutcome.Applied)?.conversion

/**
 * Probes this target constructor parameter for a value-class conversion against [source]'s
 * properties, resolving the source property with the SAME name-resolution ladder as
 * [findMatchedProperty] — so `.Map` renames convert too — but accepting value-class convertibility
 * instead of type compatibility. The first sound conversion wins; otherwise the first near miss is
 * reported so the emission site can warn.
 *
 * `cream.autoValueClassMapping = false` short-circuits here, the single choke point every
 * generation path goes through.
 *
 * Both directions can apply at once only when the source is a value class whose underlying IS the
 * target's value class and that value class's own underlying is a supertype of the source. Unwrap
 * is probed first and wins: it extracts the value already inside, where the wrap would box the
 * whole holder. Other limits (`vararg`, generic value classes, mixed unwrap-then-rewrap) leave the
 * parameter required — see `doc/customization/value-class-mapping.md`.
 */
context(options: CreamOptions)
internal fun KSValueParameter.findValueClassConversionOutcome(
    source: KSClassDeclaration,
    findMappedSourceProperty: FindMappedSourceProperty,
): ValueClassConversionOutcome {
    if (!options.autoValueClassMapping) return ValueClassConversionOutcome.None
    if (isVararg) return ValueClassConversionOutcome.None
    val parameterName = name?.asString() ?: return ValueClassConversionOutcome.None

    val targetType = type.resolve()
    var applied: ValueClassConversionOutcome.Applied? = null
    var nearMiss: ValueClassConversionOutcome.NearMiss? = null
    findPropertyByNameResolution(source, findMappedSourceProperty) { candidate ->
        when (val outcome = conversionOutcomeFor(parameterName, targetType, candidate)) {
            is ValueClassConversionOutcome.Applied -> {
                applied = outcome
                true
            }

            is ValueClassConversionOutcome.NearMiss -> {
                if (nearMiss == null) nearMiss = outcome
                false
            }

            is ValueClassConversionOutcome.None -> false
        }
    }
    return applied ?: nearMiss ?: ValueClassConversionOutcome.None
}
