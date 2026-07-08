package me.tbsten.cream.ksp.core.parentOptional

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.core.common.GenerateSourceAnnotation
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.core.common.reportCreamError
import me.tbsten.cream.ksp.util.ksp.asString

/**
 * One `(child property -> sealed parent)` contribution to a generated accessor.
 *
 * [sourceAnnotation] is the annotation occurrence that opted this property in —
 * [GenerateSourceAnnotation.ParentOptional] for an explicitly annotated property, or
 * [GenerateSourceAnnotation.ChildOptionals] when the property was swept up by the parent-side
 * blanket annotation. It drives the generated KDoc attribution and the visibility resolution.
 */
internal data class ParentOptionalEntry(
    val child: KSClassDeclaration,
    val property: KSPropertyDeclaration,
    val sourceAnnotation: GenerateSourceAnnotation,
)

/**
 * Everything needed to emit one nullable extension property `val <parent>.<accessorName>: T?`
 * on the sealed [parent]. [entries] lists every child property merged into this accessor
 * (one `is` branch each), in discovery order so generation stays deterministic.
 */
internal data class ParentOptionalAccessorSpec(
    val parent: KSClassDeclaration,
    val accessorName: String,
    val entries: List<ParentOptionalEntry>,
)

/**
 * Validate this accessor and render its property type text (WITHOUT the trailing `?`), or
 * return `null` after reporting a positioned `COMPILATION_ERROR` when the accessor cannot be
 * generated. On `null` the caller must emit nothing for this accessor (other accessors of the
 * same parent keep generating).
 *
 * Checks, in order:
 * 1. the same child contributes at most one property (two would emit shadowed `is` branches),
 * 2. every property type only references type parameters pinned by the sealed [parent]
 *    (child-specific type parameters are not expressible on the parent receiver — v1 limit),
 * 3. all merged property types render identically (no least-upper-bound merging — v1 limit),
 * 4. the [parent] does not already expose a member property named [accessorName]
 *    (a member always wins over an extension, so the accessor would be dead code).
 */
context(logger: KSPLogger)
internal fun ParentOptionalAccessorSpec.validatedPropertyTypeTextOrNull(omitPackages: List<String>): String? {
    val annotationName = entries.firstOrNull()?.sourceAnnotation?.annotationSimpleName ?: return null

    val duplicatedChild =
        entries
            .groupBy { it.child.fullName }
            .values
            .firstOrNull { it.size > 1 }
    if (duplicatedChild != null) {
        logger.reportCreamError(
            InvalidCreamUsageException(
                message =
                    "@$annotationName accessor \"${parent.fullName}.$accessorName\" merges " +
                        "multiple properties of the same child ${duplicatedChild.first().child.fullName} " +
                        "(${duplicatedChild.joinToString(", ") { it.property.simpleName.asString() }}).",
                solution = "Give each property a distinct accessor name via @ParentOptional(propertyName = ...).",
            ),
            duplicatedChild.last().property,
        )
        return null
    }

    // Render with full package names for the mismatch comparison, and with the caller's
    // omitPackages for the emitted code. Both renderings share the child->parent type-parameter
    // mapping, so an unmapped (child-specific) type parameter is caught here.
    val canonicalTexts =
        entries.map { entry ->
            entry.renderPropertyTypeOrNull(parent, omitPackages = emptyList(), annotationName) ?: return null
        }
    val mismatched = canonicalTexts.zip(entries).firstOrNull { (text, _) -> text != canonicalTexts.first() }
    if (mismatched != null) {
        logger.reportCreamError(
            InvalidCreamUsageException(
                message =
                    "@$annotationName accessor \"${parent.fullName}.$accessorName\" merges properties " +
                        "with mismatched types: " +
                        entries.zip(canonicalTexts).joinToString(", ") { (entry, text) ->
                            "${entry.child.fullName}.${entry.property.simpleName.asString()}: $text"
                        } + ".",
                solution =
                    "Align the property types, or give each property a distinct accessor name " +
                        "via @ParentOptional(propertyName = ...).",
            ),
            mismatched.second.property,
        )
        return null
    }

    val parentMember = parent.getAllProperties().firstOrNull { it.simpleName.asString() == accessorName }
    if (parentMember != null) {
        logger.reportCreamError(
            InvalidCreamUsageException(
                message =
                    "@$annotationName cannot generate accessor \"$accessorName\" on ${parent.fullName}: " +
                        "a member property with the same name is already visible on ${parent.fullName}, " +
                        "and a member always wins over the generated extension.",
                solution =
                    "Rename the generated accessor via @ParentOptional(propertyName = ...), " +
                        "or remove/rename the member property on ${parent.fullName}.",
            ),
            entries.first().property,
        )
        return null
    }

    return entries
        .first()
        .renderPropertyTypeOrNull(parent, omitPackages = omitPackages, annotationName = annotationName)
}

/** `true` when the first (representative) merged property's type is already nullable. */
internal val ParentOptionalAccessorSpec.isPropertyTypeNullable: Boolean
    get() =
        entries
            .first()
            .property.type
            .resolve()
            .isMarkedNullable

/**
 * Render this entry's property type as seen from the sealed [parent] receiver: type parameters
 * of the child are renamed to the parent parameter that pins them. A type parameter the parent
 * does NOT pin cannot appear on the parent receiver, so it is reported and `null` is returned.
 */
context(logger: KSPLogger)
private fun ParentOptionalEntry.renderPropertyTypeOrNull(
    parent: KSClassDeclaration,
    omitPackages: List<String>,
    annotationName: String,
): String? {
    val mapping = childTypeParamToParentName(child, parent)
    val unmappedNames = mutableListOf<String>()
    val text =
        property.type.resolve().asString(omitPackages = omitPackages) { typeParamType ->
            val name = typeParamType.declaration.simpleName.asString()
            mapping[name] ?: name.also { unmappedNames += it }
        }
    if (unmappedNames.isNotEmpty()) {
        logger.reportCreamError(
            InvalidCreamUsageException(
                message =
                    "@$annotationName property ${child.fullName}.${property.simpleName.asString()} references " +
                        "type parameter(s) ${unmappedNames.distinct().joinToString(", ")} that are not pinned " +
                        "by the sealed parent ${parent.fullName}, so its type cannot be expressed on the " +
                        "parent receiver.",
                solution =
                    "Remove the annotation from this property, or pin the type parameter on " +
                        "${parent.fullName} (e.g. `Child<T> : Parent<T>`).",
            ),
            property,
        )
        return null
    }
    return text
}

/**
 * Map each of [child]'s type-parameter names to the [parent] type parameter that pins it, based
 * on [child]'s direct supertype reference to [parent] (e.g. `Filled<E> : Box<E>` maps `E` to the
 * parent's `T`). Empty when [parent] is not a direct supertype (deeper chains keep names as-is
 * and are caught by the unmapped check when a type parameter is actually referenced).
 */
private fun childTypeParamToParentName(
    child: KSClassDeclaration,
    parent: KSClassDeclaration,
): Map<String, String> {
    val parentSuperType =
        child.superTypes
            .map { it.resolve() }
            .firstOrNull { it.declaration.fullName == parent.fullName }
            ?: return emptyMap()
    val parentParamNames = parent.typeParameters.map { it.name.asString() }
    return buildMap {
        parentSuperType.arguments.forEachIndexed { index, argument ->
            val childParamName =
                (argument.type?.resolve()?.declaration as? KSTypeParameter)?.name?.asString()
            if (childParamName != null && index < parentParamNames.size) {
                put(childParamName, parentParamNames[index])
            }
        }
    }
}

/**
 * The names of type parameters referenced by this property's type that the sealed [parent] does
 * NOT pin (see [childTypeParamToParentName]); empty when the type is fully expressible on the
 * parent receiver. The `@ChildOptionals` blanket sweep uses this pre-check to *skip* such
 * properties with a warning, where an explicit `@ParentOptional` reports an error instead
 * (via [validatedPropertyTypeTextOrNull]) — both walk the type the same way, so they always
 * agree on what is expressible.
 */
internal fun KSPropertyDeclaration.typeParameterNamesUnpinnedBy(
    child: KSClassDeclaration,
    parent: KSClassDeclaration,
): List<String> {
    val mapping = childTypeParamToParentName(child, parent)
    val unmappedNames = mutableListOf<String>()
    type.resolve().asString { typeParamType ->
        val name = typeParamType.declaration.simpleName.asString()
        (mapping[name] ?: name.also { unmappedNames += it })
    }
    return unmappedNames.distinct()
}
