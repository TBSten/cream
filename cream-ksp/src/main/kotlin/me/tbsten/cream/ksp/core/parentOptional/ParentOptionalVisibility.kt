package me.tbsten.cream.ksp.core.parentOptional

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.Visibility
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.core.common.reportCreamError
import me.tbsten.cream.ksp.options.CreamOptions

/**
 * Resolve the visibility modifier of the generated accessor, or return `null` after reporting a
 * positioned `COMPILATION_ERROR` when the resolved visibility would not compile.
 *
 * Resolution: the *narrowest* explicit `visibility` across the merged entries wins
 * (`INTERNAL` < `PUBLIC`); all-[CopyVisibility.INHERIT] falls through to the
 * `cream.defaultVisibility` option, then to the narrowest declaration visibility among the
 * sealed parent, each child (enclosing chains included), and each property (`internal` when any
 * of them is internal). Only `public` / `internal` are supported for the generated top-level
 * extension.
 *
 * Validation: a `public` accessor *forced* by `visibility = CopyVisibility.PUBLIC` or by
 * `cream.defaultVisibility=PUBLIC` is rejected when its signature would expose an internal
 * symbol — the receiver (the sealed parent or an enclosing class of it) or the accessor's
 * property type — because Kotlin rejects `'public' member exposes its 'internal' ... type` in
 * the generated file. The inherited path needs no such check: with valid user code the
 * narrowest-visibility inheritance already lands on `internal` whenever an internal symbol is
 * involved (a public property of a public class cannot have an internal type to begin with).
 * Internal *children* are fine either way: they only appear in the getter body (`is Child`),
 * which exposure rules do not constrain.
 */
context(options: CreamOptions, logger: KSPLogger)
internal fun ParentOptionalAccessorSpec.validatedVisibilityModifierOrNull(): String? {
    val annotationVisibilities = entries.map { it.sourceAnnotation.visibility }
    val fromAnnotation =
        when {
            annotationVisibilities.any { it == CopyVisibility.INTERNAL } -> CopyVisibility.INTERNAL
            annotationVisibilities.any { it == CopyVisibility.PUBLIC } -> CopyVisibility.PUBLIC
            else -> CopyVisibility.INHERIT
        }
    val effective = if (fromAnnotation == CopyVisibility.INHERIT) options.defaultVisibility else fromAnnotation
    return when (effective) {
        CopyVisibility.INHERIT -> inheritedNarrowestModifier()
        CopyVisibility.INTERNAL -> "internal"
        CopyVisibility.PUBLIC -> {
            val exposedInternal = firstExposedInternalDeclarationOrNull()
            if (exposedInternal == null) {
                "public"
            } else {
                reportPublicAccessorExposesInternal(exposedInternal, forcedByAnnotation = fromAnnotation == CopyVisibility.PUBLIC)
                null
            }
        }
    }
}

private fun ParentOptionalAccessorSpec.inheritedNarrowestModifier(): String {
    // Class chains (not just the classes themselves): a declaration nested inside an internal
    // one is effectively internal even when its own modifier is public.
    val declarations: List<KSDeclaration> =
        (parent.classChain() + entries.asSequence().flatMap { it.child.classChain() + sequenceOf(it.property) })
            .toList()
    val anyInternal = declarations.any { it.getVisibility() == Visibility.INTERNAL }
    return if (anyInternal) "internal" else "public"
}

/**
 * The first internal declaration a `public` accessor's *signature* would expose, or `null` when
 * the signature is exposure-safe: the sealed parent's enclosing chain (the receiver type), then
 * every declaration referenced by the representative property type (type arguments included,
 * typealiases expanded — an alias whose expansion is internal cannot appear in a valid public
 * signature either).
 */
private fun ParentOptionalAccessorSpec.firstExposedInternalDeclarationOrNull(): KSDeclaration? =
    parent.classChain().firstOrNull { it.getVisibility() == Visibility.INTERNAL }
        ?: entries
            .firstOrNull()
            ?.property
            ?.type
            ?.resolve()
            ?.referencedClassDeclarations()
            ?.firstOrNull { it.getVisibility() == Visibility.INTERNAL }

/**
 * Every class declaration referenced by this type as rendered in a signature: the type's own
 * declaration (with its enclosing classes), recursively its type arguments, and — for a
 * typealias — the declarations of its expansion. Type parameters are skipped (their bounds are
 * declared and exposure-checked on the parent declaration itself).
 */
private fun KSType.referencedClassDeclarations(): Sequence<KSClassDeclaration> =
    sequence {
        when (val declaration = declaration) {
            is KSClassDeclaration -> yieldAll(declaration.classChain())
            is KSTypeAlias -> yieldAll(declaration.type.resolve().referencedClassDeclarations())
            else -> {} // KSTypeParameter etc.
        }
        arguments.forEach { argument ->
            val argumentType = argument.type?.resolve() ?: return@forEach
            if (argumentType.declaration !is KSTypeParameter) {
                yieldAll(argumentType.referencedClassDeclarations())
            }
        }
    }

context(logger: KSPLogger)
private fun ParentOptionalAccessorSpec.reportPublicAccessorExposesInternal(
    exposedInternal: KSDeclaration,
    forcedByAnnotation: Boolean,
) {
    val annotationName = entries.firstOrNull()?.sourceAnnotation?.annotationSimpleName ?: return
    // Not `fullName`, which throws UnknownCreamException when qualifiedName is null and would
    // mask this InvalidCreamUsageException.
    val exposedName = exposedInternal.qualifiedName?.asString() ?: exposedInternal.simpleName.asString()
    val forcedBy =
        if (forcedByAnnotation) {
            "visibility = CopyVisibility.PUBLIC on @$annotationName"
        } else {
            "the cream.defaultVisibility=PUBLIC option"
        }
    logger.reportCreamError(
        InvalidCreamUsageException(
            message =
                "@$annotationName accessor \"${parent.fullName}.$accessorName\" is forced public " +
                    "(via $forcedBy) but its signature would expose the internal declaration " +
                    "$exposedName, which Kotlin rejects " +
                    "('public' member exposes its 'internal' type).",
            solution =
                "Use CopyVisibility.INTERNAL (or INHERIT) for this accessor, " +
                    "or make $exposedName public.",
        ),
        entries.firstOrNull { it.sourceAnnotation.visibility == CopyVisibility.PUBLIC }?.property
            ?: entries.first().property,
    )
}
