package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.util.escapeKotlinIdentifier

/**
 * An automatic `value class` wrap / unwrap between a target constructor parameter and a
 * name-resolved source property (issue #21). Applies to every generation path that resolves
 * auto-copy defaults (copy AND combine functions), and only when [findMatchedProperty] found no
 * type-compatible match — a normal match always wins. `@SealedCopy` is N/A by construction: its
 * generated `copy()` parameters ARE the parent's abstract properties (same types on both sides),
 * so it never reaches this conversion.
 *
 * Use [defaultValueExpression] to render the generated default; the emission site only supplies
 * the qualified property access (`this.id`, `sourceB.id`, ...) and its own type renderer so the
 * expression matches the surrounding generated code (omitted packages, resolved type parameters).
 */
internal sealed interface ValueClassConversion {
    /** The name-resolved source property supplying the default value. */
    val sourceProperty: KSPropertyDeclaration

    /**
     * The target parameter type is a (possibly nested) value class: the generated default wraps
     * the source property in one constructor call per [layer][layers], outermost first —
     * `V2(V1(this.x))`. When [useSafeCall] is set the source property is nullable while the
     * constructors take non-null, so the wrap happens inside `?.let { ... }` (only sound because
     * the parameter type itself is nullable).
     */
    data class Wrap(
        override val sourceProperty: KSPropertyDeclaration,
        val layers: List<KSType>,
        val useSafeCall: Boolean,
    ) : ValueClassConversion

    /**
     * The source property is a (possibly nested) value class: the generated default reads one
     * underlying property per hop — `this.x.v1.value`. Each hop after a nullable type uses `?.`,
     * making the whole expression's type nullable (only sound because the parameter type then
     * accepts it).
     */
    data class Unwrap(
        override val sourceProperty: KSPropertyDeclaration,
        val accessors: List<UnderlyingAccess>,
    ) : ValueClassConversion

    /** One unwrap hop: `.name` or `?.name` (after a nullable type). */
    data class UnderlyingAccess(
        val propertyName: String,
        val viaSafeCall: Boolean,
    )
}

/**
 * The result of probing a target constructor parameter for a value-class conversion:
 *
 * - [Applied] — a sound conversion exists; emit its default.
 * - [NearMiss] — a conversion ALMOST applied but was rejected for a reason the user plausibly did
 *   not intend (nullability soundness, constructor/property visibility). The parameter stays
 *   required; emission sites should surface [NearMiss.message] as a positioned warning — unless
 *   the candidate property is `@Exclude`-marked, in which case the user explicitly opted out and
 *   the miss is silent.
 * - [None] — nothing value-class-shaped matched; the parameter stays required, exactly as before
 *   this feature existed.
 */
internal sealed interface ValueClassConversionOutcome {
    data class Applied(
        val conversion: ValueClassConversion,
    ) : ValueClassConversionOutcome

    data class NearMiss(
        val candidate: KSPropertyDeclaration,
        val message: String,
    ) : ValueClassConversionOutcome

    data object None : ValueClassConversionOutcome
}

/**
 * Renders the generated default for this conversion.
 *
 * @param sourceAccess the qualified (already escaped) property access, e.g. `this.id` or `sourceB.id`.
 * @param renderType the emission site's own type renderer (omitted packages, resolved type
 *   parameters) so constructor calls are rendered exactly like the parameter type. A possible
 *   trailing `?` is stripped: a wrap always constructs the non-null value class even when the
 *   parameter type is nullable ([asString][me.tbsten.cream.ksp.util.ksp.asString] appends `?`
 *   only for outer nullability, so the suffix strip cannot corrupt nullable type arguments).
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
 * The [ValueClassConversion] for this target constructor parameter against [source]'s properties,
 * or `null`. Shorthand for callers that only care about [ValueClassConversionOutcome.Applied]
 * (required-parameter computation, exclude-effectiveness checks); emission sites should use
 * [findValueClassConversionOutcome] so near misses can be surfaced as warnings.
 */
context(options: CreamOptions)
internal fun KSValueParameter.findValueClassConversion(
    source: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation,
): ValueClassConversion? = (findValueClassConversionOutcome(source, generateSourceAnnotation) as? ValueClassConversionOutcome.Applied)?.conversion

/**
 * Probes this target constructor parameter for an automatic value-class conversion against
 * [source]'s properties.
 *
 * Callers consult this only when [findMatchedProperty] found nothing (a type-compatible match
 * always wins), so plain matching behavior is unchanged. The source property is resolved with the
 * SAME name-resolution ladder as [findMatchedProperty] ([findPropertyByNameResolution]) — `.Map`
 * annotations and mapping-annotation `properties` remappings are honored — but with
 * value-class convertibility instead of type compatibility as the acceptance test. The first
 * name-resolved property with a sound conversion wins; if none applies, the first near miss is
 * reported for warning purposes.
 *
 * The `cream.autoValueClassMapping` option (default `true`) is the module-wide escape hatch:
 * when set to `false` this always returns [ValueClassConversionOutcome.None] — the single choke
 * point every generation path goes through.
 *
 * Deliberate limits (each leaves the parameter required, the pre-feature behavior; see
 * `doc/customization/value-class-mapping.md`):
 * - `vararg` parameters — the property counterpart is an ARRAY; element-wise conversion is out of
 *   scope.
 * - generic value classes (`V<T>`) — an experimental Kotlin language feature
 *   (`-XXLanguage:+GenericInlineClasses`); skipped entirely.
 * - mixed conversions — unwrapping one value class and re-wrapping into another is never done;
 *   a conversion either only wraps or only unwraps.
 * - wrap and unwrap can both apply only when the source property is a value class whose
 *   underlying type IS the target parameter's value class (`W(val u: V)` into `V` — value
 *   classes are final, so no other shape exists) and `V`'s own underlying is a supertype of `W`
 *   (e.g. `V(val u: Any)`). Unwrap is probed first and wins, deterministically: it extracts the
 *   exact `V` already inside `W`, whereas the wrap would box the whole `W` into `V`'s loose
 *   underlying.
 */
context(options: CreamOptions)
internal fun KSValueParameter.findValueClassConversionOutcome(
    source: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation,
): ValueClassConversionOutcome {
    if (!options.autoValueClassMapping) return ValueClassConversionOutcome.None
    if (isVararg) return ValueClassConversionOutcome.None
    val parameterName = name?.asString() ?: return ValueClassConversionOutcome.None

    val targetType = type.resolve()
    var applied: ValueClassConversionOutcome.Applied? = null
    var nearMiss: ValueClassConversionOutcome.NearMiss? = null
    findPropertyByNameResolution(source, generateSourceAnnotation) { candidate ->
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
