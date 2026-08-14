package me.tbsten.cream.ksp.core.parentOptional

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Visibility
import me.tbsten.cream.ParentOptional
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.core.common.annotationsOf
import me.tbsten.cream.ksp.core.common.reportCreamError

/**
 * Find the `@ParentOptional` occurrence for this property, whether KSP surfaced it on the
 * property itself or on the corresponding primary-constructor parameter. `@ParentOptional`
 * targets `PROPERTY` only, but for a primary-constructor `val` the *syntactic* site is the
 * value parameter, and KSP2's AA backend may expose the raw [KSAnnotation] there. Shared by
 * both the `parentOptional` and `childOptionals` features (which must not depend on each
 * other), hence its home in `core/`.
 */
internal fun KSPropertyDeclaration.parentOptionalAnnotationOrNull(): KSAnnotation? =
    annotationsOf(ParentOptional::class).firstOrNull()
        ?: correspondingConstructorParameter()
            ?.annotationsOf(ParentOptional::class)
            ?.firstOrNull()

/**
 * The primary-constructor parameter that declares this property (`class C(val x: Int)`), or
 * `null` for a body-declared property.
 */
internal fun KSPropertyDeclaration.correspondingConstructorParameter(): KSValueParameter? =
    (parentDeclaration as? KSClassDeclaration)
        ?.primaryConstructor
        ?.parameters
        ?.firstOrNull { it.name?.asString() == simpleName.asString() }

/**
 * Whether a generated top-level accessor can read this property: only `public` / `internal`
 * properties qualify (a `private` / `protected` one is invisible from the generated file).
 */
internal fun KSPropertyDeclaration.isAccessibleFromGeneratedAccessor(): Boolean = getVisibility() in setOf(Visibility.PUBLIC, Visibility.INTERNAL)

/**
 * Whether this is a (member) *extension* property (`val String.ext: Int get() = ...`). The
 * generated accessor's `is` branch reads the property with the child as its only receiver; an
 * extension property additionally needs an extension receiver the accessor cannot supply — a
 * bare read would instead resolve to the generated accessor itself (infinite recursion at
 * runtime), so both features must reject/skip these.
 */
internal fun KSPropertyDeclaration.isExtensionProperty(): Boolean = extensionReceiver != null

/**
 * Report the extension-property misuse (see [isExtensionProperty]) as a positioned
 * `COMPILATION_ERROR`. The `@ParentOptional` feature reports it for every annotated extension
 * property (before the `@ChildOptionals` ownership filter, so nothing slips through); the
 * `@ChildOptionals` sweep skips extension properties silently to avoid duplicating that report.
 */
context(logger: KSPLogger)
internal fun reportParentOptionalExtensionProperty(property: KSPropertyDeclaration) {
    val displayName = property.qualifiedName?.asString() ?: property.simpleName.asString()
    logger.reportCreamError(
        InvalidCreamUsageException(
            message =
                "@${ParentOptional::class.simpleName} cannot be applied to extension property $displayName: " +
                    "the generated accessor reads the property on the child instance alone and cannot " +
                    "supply the extension receiver.",
            solution = "Remove @${ParentOptional::class.simpleName} from $displayName, or convert it to a member property.",
        ),
        property,
    )
}

/**
 * Whether the generated accessor file can reference this class in an `is` branch: the class
 * itself and every enclosing class must be `public` or `internal`. A `private` (file-private)
 * or `protected` declaration anywhere in the chain would make the generated `is` check a
 * visibility violation. See [firstInaccessibleClassOrNull] for the offending declaration.
 */
internal fun KSClassDeclaration.isAccessibleFromGeneratedAccessor(): Boolean = firstInaccessibleClassOrNull() == null

/** The innermost class in [classChain] the generated file cannot reference, or `null` when all are visible. */
internal fun KSClassDeclaration.firstInaccessibleClassOrNull(): KSClassDeclaration? =
    classChain().firstOrNull { it.getVisibility() !in setOf(Visibility.PUBLIC, Visibility.INTERNAL) }

/** This class followed by its enclosing classes, innermost first. */
internal fun KSClassDeclaration.classChain(): Sequence<KSClassDeclaration> = generateSequence<KSDeclaration>(this) { it.parentDeclaration }.filterIsInstance<KSClassDeclaration>()
