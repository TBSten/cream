package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import me.tbsten.cream.DefaultAccessorPropertyName
import me.tbsten.cream.ParentOptional
import me.tbsten.cream.ksp.util.ksp.getArgument

/**
 * [GenerateSourceAnnotation] for `@ParentOptional`.
 *
 * Accessor generation, not copy generation: the generation rules keep their defaults, and it
 * supplies no [FindMappedSourceProperty] / [IsExcluded] at all — the generated accessor reads a
 * single child property, so there is no property matching and no `@Exclude` concept.
 *
 * `@ParentOptional` is attached to a **property**. [annotatedDeclaration] is overridden with the
 * resolved [KSPropertyDeclaration] explicitly: for a primary-constructor `val`, KSP2 may surface
 * the raw annotation on the value parameter, whose `parent` is not a `KSDeclaration` — the
 * default getter would then fail even though the property is well known to the caller.
 */
internal data class ParentOptionalSourceAnnotation(
    override val annotation: KSAnnotation,
    override val annotatedDeclaration: KSPropertyDeclaration,
) : GenerateSourceAnnotation {
    /**
     * The generated accessor's name: the `propertyName` template with
     * [DefaultAccessorPropertyName] replaced by the annotated property's own name.
     *
     * "Unset" is the token as a *value*, never an absent argument: KSP2 materializes defaults
     * into `KSAnnotation.arguments`, which is what leaves `notCopyToObject`'s fallback dead in
     * issue #184.
     */
    val accessorName: String
        get() = propertyNameTemplate.resolveAccessorName(annotatedDeclaration)

    private val propertyNameTemplate: String
        get() = annotation.parentOptionalPropertyNameOrNull() ?: DefaultAccessorPropertyName
}

/** This `@ParentOptional`'s own `propertyName` template, or `null` when left at the token. */
private fun KSAnnotation.parentOptionalPropertyNameOrNull(): String? = getArgument(ParentOptional::propertyName)?.takeIf { it != DefaultAccessorPropertyName }

/** Substitute [DefaultAccessorPropertyName] in this template with [property]'s own name. */
private fun String.resolveAccessorName(property: KSPropertyDeclaration): String = replace(DefaultAccessorPropertyName, property.simpleName.asString())
