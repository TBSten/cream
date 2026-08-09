package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import me.tbsten.cream.ChildOptionals
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.DefaultAccessorPropertyName
import me.tbsten.cream.ParentOptional
import me.tbsten.cream.ksp.util.ksp.getArgument

/**
 * [GenerateSourceAnnotation] for `@ParentOptional`.
 *
 * Accessor generation, not copy generation: the generation rules keep their defaults, and it
 * supplies no [FindMappedSourceProperty] / [IsExcluded] at all — the generated accessor reads a
 * single child property, so there is no property matching and no `@Exclude` concept
 * (`@ChildOptionals.Exclude` is a sweep opt-out resolved in the feature, not a per-parameter rule).
 *
 * `@ParentOptional` is attached to a **property**. [annotatedDeclaration] is overridden with the
 * resolved [KSPropertyDeclaration] explicitly: for a primary-constructor `val`, KSP2 may surface
 * the raw annotation on the value parameter, whose `parent` is not a `KSDeclaration` — the
 * default getter would then fail even though the property is well known to the caller.
 *
 * When the property was picked up by a `@ChildOptionals` sweep, [sweep] carries that annotation
 * and its arguments act as the outer layer: see [accessorName] / [visibility] / [kdocDescription].
 */
internal data class ParentOptionalSourceAnnotation(
    override val annotation: KSAnnotation,
    override val annotatedDeclaration: KSPropertyDeclaration,
    /**
     * The `@ChildOptionals` annotation whose sweep routed this property's generation, or `null`
     * when the `@ParentOptional` feature generates directly (no enclosing sweep).
     */
    val sweep: ChildOptionalsSourceAnnotation? = null,
) : GenerateSourceAnnotation {
    /**
     * The generated accessor's name: the winning `propertyName` template — this annotation's own
     * value, else the [sweep]'s, else the bare token — with [DefaultAccessorPropertyName]
     * replaced by the annotated property's own name.
     *
     * "Unset" is the token as a *value*, never an absent argument: KSP2 materializes defaults
     * into `KSAnnotation.arguments`, which is what leaves `notCopyToObject`'s fallback dead in
     * issue #184.
     */
    val accessorName: String
        get() = propertyNameTemplate.resolveAccessorName(annotatedDeclaration)

    private val propertyNameTemplate: String
        get() =
            annotation.parentOptionalPropertyNameOrNull()
                ?: sweep?.propertyNameTemplate
                ?: DefaultAccessorPropertyName

    override val visibility: CopyVisibility
        get() =
            super.visibility.takeIf { it != CopyVisibility.INHERIT }
                ?: sweep?.visibility
                ?: CopyVisibility.INHERIT

    override val kdocDescription: String
        get() = super.kdocDescription.ifEmpty { sweep?.kdocDescription.orEmpty() }

    override val kdocExamples: List<String>
        get() = super.kdocExamples.ifEmpty { sweep?.kdocExamples.orEmpty() }
}

/**
 * [GenerateSourceAnnotation] for `@ChildOptionals`.
 *
 * Accessor generation on the annotated sealed parent; like [ParentOptionalSourceAnnotation] the
 * generation rules keep their defaults and no [FindMappedSourceProperty] / [IsExcluded] is
 * supplied.
 */
internal data class ChildOptionalsSourceAnnotation(
    override val annotation: KSAnnotation,
) : GenerateSourceAnnotation {
    /**
     * The sweep-wide `propertyName` template; [DefaultAccessorPropertyName] (i.e. "each accessor
     * keeps its base property's name") when unset.
     */
    val propertyNameTemplate: String
        get() =
            annotation.getArgument(ChildOptionals::propertyName)
                ?: DefaultAccessorPropertyName

    /** The accessor name for a swept [property] that carries no `@ParentOptional` of its own. */
    fun accessorNameFor(property: KSPropertyDeclaration): String = propertyNameTemplate.resolveAccessorName(property)
}

/** This `@ParentOptional`'s own `propertyName` template, or `null` when left at the token. */
private fun KSAnnotation.parentOptionalPropertyNameOrNull(): String? = getArgument(ParentOptional::propertyName)?.takeIf { it != DefaultAccessorPropertyName }

/** Substitute [DefaultAccessorPropertyName] in this template with [property]'s own name. */
private fun String.resolveAccessorName(property: KSPropertyDeclaration): String = replace(DefaultAccessorPropertyName, property.simpleName.asString())
