package me.tbsten.cream.ksp.feature.childOptionals

import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.validate
import me.tbsten.cream.ChildOptionals
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.ProcessContext
import me.tbsten.cream.ksp.core.common.GenerateSourceAnnotation
import me.tbsten.cream.ksp.core.common.annotationsOf
import me.tbsten.cream.ksp.core.common.createNewKotlinFile
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.core.common.omitPackagesFor
import me.tbsten.cream.ksp.core.common.reportCreamError
import me.tbsten.cream.ksp.core.common.underPackageName
import me.tbsten.cream.ksp.core.parentOptional.ParentOptionalAccessorSpec
import me.tbsten.cream.ksp.core.parentOptional.ParentOptionalEntry
import me.tbsten.cream.ksp.core.parentOptional.appendParentOptionalAccessor
import me.tbsten.cream.ksp.core.parentOptional.correspondingConstructorParameter
import me.tbsten.cream.ksp.core.parentOptional.isAccessibleFromGeneratedAccessor
import me.tbsten.cream.ksp.core.parentOptional.isExtensionProperty
import me.tbsten.cream.ksp.core.parentOptional.parentOptionalAnnotationOrNull
import me.tbsten.cream.ksp.core.parentOptional.typeParameterNamesUnpinnedBy
import me.tbsten.cream.ksp.util.ksp.collectConcreteSubclasses
import me.tbsten.cream.ksp.util.ksp.collectIntermediateSealedSubclasses
import me.tbsten.cream.ksp.util.ksp.isSealed
import me.tbsten.cream.ksp.util.with

private val annotationName = ChildOptionals::class.simpleName!!

context(processContext: ProcessContext)
internal fun processChildOptionals(): List<KSAnnotated> =
    with(processContext.logger, processContext.options) {
        val (childOptionalsTargets, invalidChildOptionalsTargets) =
            processContext.resolver
                .getSymbolsWithAnnotation(
                    annotationName = ChildOptionals::class.fullName,
                ).partition { it.validate() }

        // Qualified names of properties whose @ChildOptionals.Exclude actually dropped a swept
        // accessor contribution — so the no-effect pass at the end stays quiet for them.
        val excludeTookEffect = mutableSetOf<String>()

        childOptionalsTargets.forEach { annotated ->
            if (annotated !is KSClassDeclaration) {
                processContext.logger.reportChildOptionalsNotADeclaration(annotated)
                return@forEach
            }
            if (!annotated.isSealed()) {
                processContext.logger.reportChildOptionalsNotSealed(annotated)
                return@forEach
            }
            val parent = annotated

            // Read the raw KSAnnotation (not a getAnnotationsByType proxy): see the
            // GenerateSourceAnnotation docs for why KSP2's typed proxy is avoided.
            val childOptionalsAnnotation =
                parent.annotationsOf(ChildOptionals::class).firstOrNull() ?: return@forEach
            val parentSourceAnnotation = GenerateSourceAnnotation.ChildOptionals(childOptionalsAnnotation)

            val parentVisiblePropertyNames =
                parent
                    .getAllProperties()
                    .map { it.simpleName.asString() }
                    .toSet()

            val entriesByAccessorName = LinkedHashMap<String, MutableList<ParentOptionalEntry>>()
            parent.collectConcreteSubclasses().forEach leaf@{ leaf ->
                // Blanket application skips leaves the generated `is` branch could not reference
                // (private / protected anywhere in the enclosing chain) silently — the same
                // asymmetry as inaccessible properties (explicit @ParentOptional errors instead).
                if (!leaf.isAccessibleFromGeneratedAccessor()) return@leaf
                leaf.getDeclaredProperties().forEach property@{ property ->
                    val propertyName = property.simpleName.asString()
                    // Blanket application skips inaccessible properties silently (unlike an
                    // explicit @ParentOptional, which reports an error).
                    if (!property.isAccessibleFromGeneratedAccessor()) return@property

                    // An explicit @ParentOptional on a swept-up property keeps its propertyName /
                    // kdoc / visibility (the ownership rule routes generation through this feature).
                    val parentOptionalAnnotation = property.parentOptionalAnnotationOrNull()

                    // Extension properties cannot be read by the accessor (see
                    // isExtensionProperty). Blanket application skips them silently — they are
                    // computed views, not state of the leaf. An explicit @ParentOptional on one
                    // is a misuse, but the @ParentOptional feature already reports it (it sees
                    // every annotated property before applying the ownership filter), so skipping
                    // here too avoids a duplicate diagnostic.
                    if (property.isExtensionProperty()) return@property

                    // A property type referencing a type parameter the annotated parent does not
                    // pin is not expressible on the parent receiver. The blanket sweep skips it
                    // with a warning (the hierarchy is otherwise fine); an explicitly
                    // @ParentOptional-annotated property flows on and gets the positioned error
                    // from the shared accessor validation instead.
                    if (parentOptionalAnnotation == null) {
                        val unpinnedNames = property.typeParameterNamesUnpinnedBy(leaf, parent)
                        if (unpinnedNames.isNotEmpty()) {
                            processContext.logger.warnChildOptionalsUnpinnedTypeParameters(property, unpinnedNames, parent)
                            return@property
                        }
                    }

                    // Already visible on the parent (overrides included): the member always wins
                    // over an extension, so an accessor would be dead code. An explicit
                    // @ParentOptional bypasses this original-name filter — its (possibly renamed)
                    // accessor is wanted, and a name still colliding with a parent member is
                    // reported by the shared parent-member validation instead of dropped silently.
                    if (parentOptionalAnnotation == null && propertyName in parentVisiblePropertyNames) return@property

                    // Sweep opt-out: @ChildOptionals.Exclude drops a swept property from generation
                    // entirely (no accessor from this contributor). Checked last, so it only fires
                    // for a property the sweep would OTHERWISE have picked up — an exclude on a
                    // property already skipped above is a no-op reported by the pass at the end.
                    // An explicit @ParentOptional (parentOptionalAnnotation != null) is opted in by
                    // hand and wins: its accessor is still generated, so the exclude is honoured
                    // only for sweep-discovered properties.
                    if (parentOptionalAnnotation == null && property.hasChildOptionalsExclude()) {
                        property.qualifiedName?.asString()?.let(excludeTookEffect::add)
                        return@property
                    }

                    val (accessorName, sourceAnnotation) =
                        if (parentOptionalAnnotation != null) {
                            val propertyGsa =
                                GenerateSourceAnnotation.ParentOptional(parentOptionalAnnotation, property)
                            (propertyGsa.propertyName ?: propertyName) to propertyGsa
                        } else {
                            propertyName to parentSourceAnnotation
                        }
                    entriesByAccessorName.getOrPut(accessorName) { mutableListOf() } +=
                        ParentOptionalEntry(
                            child = leaf,
                            property = property,
                            sourceAnnotation = sourceAnnotation,
                        )
                }
            }
            // A transitive *intermediate* sealed type's own @ParentOptional properties are not
            // visible on [parent] and are declared by no leaf, so the leaf sweep above misses
            // them — while the @ParentOptional feature cedes this ancestor's accessor to this
            // feature (ownership rule). Collect them here: a single `is Intermediate` branch
            // covers every leaf below it.
            parent.collectIntermediateSealedSubclasses().forEach intermediate@{ intermediate ->
                if (!intermediate.isAccessibleFromGeneratedAccessor()) return@intermediate
                intermediate.getDeclaredProperties().forEach property@{ property ->
                    val parentOptionalAnnotation = property.parentOptionalAnnotationOrNull() ?: return@property
                    if (!property.isAccessibleFromGeneratedAccessor()) return@property
                    // Misuse already reported by the @ParentOptional feature (see the leaf sweep).
                    if (property.isExtensionProperty()) return@property

                    val propertyGsa = GenerateSourceAnnotation.ParentOptional(parentOptionalAnnotation, property)
                    val accessorName = propertyGsa.propertyName ?: property.simpleName.asString()
                    entriesByAccessorName.getOrPut(accessorName) { mutableListOf() } +=
                        ParentOptionalEntry(
                            child = intermediate,
                            property = property,
                            sourceAnnotation = propertyGsa,
                        )
                }
            }

            val sourceFiles =
                (listOf(parent) + entriesByAccessorName.values.flatten().map { it.child })
                    .mapNotNull { it.containingFile }
                    .distinct()
            processContext.codeGenerator.createNewKotlinFile(
                dependencies = Dependencies(aggregating = true, *sourceFiles.toTypedArray()),
                packageName = parent.packageName,
                fileName = "ChildOptionals__${parent.underPackageName}",
            ) { appender ->
                entriesByAccessorName.forEach { (accessorName, entries) ->
                    appender.appendParentOptionalAccessor(
                        spec =
                            ParentOptionalAccessorSpec(
                                parent = parent,
                                accessorName = accessorName,
                                entries = entries,
                            ),
                        omitPackages = omitPackagesFor(parent.packageName),
                    )
                }
            }
        }

        // No-effect warning: an @ChildOptionals.Exclude that never removed a swept accessor — the
        // enclosing class is not under a @ChildOptionals parent, or the property would not have been
        // swept anyway (private / extension / already visible on the parent / unpinned type param).
        // Mirrors cream's other unmatched-@Exclude warnings. A property that is ALSO @ParentOptional
        // is generated regardless (explicit opt-in wins over the sweep opt-out), so its exclude is
        // deliberately ignored rather than a mistake — no warning there.
        processContext.resolver
            .getSymbolsWithAnnotation(ChildOptionals.Exclude::class.fullName)
            .mapNotNull { it.asChildOptionalsExcludedPropertyOrNull() }
            .distinctBy { it.qualifiedName?.asString() ?: it }
            .forEach { property ->
                if (property.parentOptionalAnnotationOrNull() != null) return@forEach
                if (property.qualifiedName?.asString() in excludeTookEffect) return@forEach
                processContext.logger.warnChildOptionalsExcludeHasNoEffect(property)
            }

        return invalidChildOptionalsTargets
    }

/** True if this property (or its primary-constructor `val`) carries `@ChildOptionals.Exclude`. */
private fun KSPropertyDeclaration.hasChildOptionalsExclude(): Boolean =
    annotationsOf(ChildOptionals.Exclude::class).any() ||
        correspondingConstructorParameter()?.annotationsOf(ChildOptionals.Exclude::class)?.any() == true

/**
 * Resolve an `@ChildOptionals.Exclude`-annotated symbol to the property it opts out. The annotation
 * targets `PROPERTY`, so KSP2 normally hands us the [KSPropertyDeclaration]; the [KSValueParameter]
 * branch covers a backend that surfaces a primary-constructor `val`'s syntactic site instead
 * (mirroring `@ParentOptional`'s resolution).
 */
private fun KSAnnotated.asChildOptionalsExcludedPropertyOrNull(): KSPropertyDeclaration? =
    when (this) {
        is KSPropertyDeclaration -> this
        is KSValueParameter -> {
            val constructor = parent as? KSFunctionDeclaration
            val enclosingClass = constructor?.parentDeclaration as? KSClassDeclaration
            enclosingClass
                ?.getDeclaredProperties()
                ?.firstOrNull { it.simpleName.asString() == name?.asString() }
        }
        else -> null
    }

// ---------------------------------------------------------------------------
// Diagnostic helpers
// ---------------------------------------------------------------------------

/**
 * The blanket sweep found a property whose type references type parameter(s) the annotated
 * parent does not pin. A warning (not an error): the user did not single this property out, and
 * the rest of the hierarchy generates fine — but silence would make the missing accessor look
 * like a bug.
 */
private fun KSPLogger.warnChildOptionalsUnpinnedTypeParameters(
    property: KSPropertyDeclaration,
    unpinnedNames: List<String>,
    parent: KSClassDeclaration,
) {
    val displayName = property.qualifiedName?.asString() ?: property.simpleName.asString()
    val parentName = parent.qualifiedName?.asString() ?: parent.simpleName.asString()
    warn(
        "@$annotationName skipped property $displayName: its type references type parameter(s) " +
            "${unpinnedNames.joinToString(", ")} not pinned by the sealed parent $parentName, so no accessor " +
            "can be generated for it. Pin the type parameter on $parentName (e.g. `Child<T> : Parent<T>`) " +
            "to include it.",
        property,
    )
}

/**
 * An `@ChildOptionals.Exclude` that removed nothing: the property is not swept into any generated
 * accessor (its class is outside a `@ChildOptionals` hierarchy, or it was already skipped for
 * another reason). A warning, not an error — the exclude is redundant but harmless. Mirrors the
 * `@CopyToChildren.Exclude` / `@CopyFrom.Exclude` no-effect warnings.
 */
private fun KSPLogger.warnChildOptionalsExcludeHasNoEffect(property: KSPropertyDeclaration) {
    val displayName = property.qualifiedName?.asString() ?: property.simpleName.asString()
    warn(
        "@Exclude on '$displayName' has no effect: it is not swept into a @$annotationName-generated accessor.",
        property,
    )
}

private fun KSPLogger.reportChildOptionalsNotADeclaration(annotated: KSAnnotated) {
    reportCreamError(
        InvalidCreamUsageException(
            message = "@$annotationName must be applied to a sealed class/interface.",
            solution = "Please apply @$annotationName to a `sealed class` or `sealed interface`.",
        ),
        annotated,
    )
}

private fun KSPLogger.reportChildOptionalsNotSealed(annotated: KSClassDeclaration) {
    // Avoid `fullName`, which throws UnknownCreamException when qualifiedName is null
    // (e.g. local/anonymous declarations) and would mask this InvalidCreamUsageException.
    val displayName = annotated.qualifiedName?.asString() ?: annotated.simpleName.asString()
    reportCreamError(
        InvalidCreamUsageException(
            message =
                "@$annotationName must be applied to a sealed class/interface, " +
                    "but $displayName is not sealed (classKind: ${annotated.classKind}).",
            solution = "Make $displayName a `sealed class` or `sealed interface`.",
        ),
        annotated,
    )
}
