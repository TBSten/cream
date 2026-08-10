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
import me.tbsten.cream.ksp.core.common.ChildOptionalsSourceAnnotation
import me.tbsten.cream.ksp.core.common.ParentOptionalSourceAnnotation
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

        // Properties whose @ChildOptionals.Exclude actually dropped a contribution, so the
        // no-effect pass at the end stays quiet for them.
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

            // Raw KSAnnotation, not a getAnnotationsByType proxy — see GenerateSourceAnnotation.
            val childOptionalsAnnotation =
                parent.annotationsOf(ChildOptionals::class).firstOrNull() ?: return@forEach
            val parentSourceAnnotation = ChildOptionalsSourceAnnotation(childOptionalsAnnotation)

            val parentVisiblePropertyNames =
                parent
                    .getAllProperties()
                    .map { it.simpleName.asString() }
                    .toSet()

            val entriesByAccessorName = LinkedHashMap<String, MutableList<ParentOptionalEntry>>()
            parent.collectConcreteSubclasses().forEach leaf@{ leaf ->
                // The sweep skips what it cannot usefully lift rather than failing the whole
                // hierarchy; the same shape carrying an explicit @ParentOptional is an error,
                // reported by that feature.
                if (!leaf.isAccessibleFromGeneratedAccessor()) return@leaf
                leaf.getDeclaredProperties().forEach property@{ property ->
                    if (!property.isAccessibleFromGeneratedAccessor()) return@property

                    // Ownership routes generation here, so an explicit @ParentOptional layers its
                    // arguments over this annotation's.
                    val parentOptionalAnnotation = property.parentOptionalAnnotationOrNull()

                    // The @ParentOptional feature sees every annotated property before the
                    // ownership filter, so skipping here avoids a duplicate diagnostic.
                    if (property.isExtensionProperty()) return@property

                    // Not expressible on the parent receiver. Warned rather than silent because the
                    // property otherwise looks liftable; an annotated one gets a positioned error.
                    if (parentOptionalAnnotation == null) {
                        val unpinnedNames = property.typeParameterNamesUnpinnedBy(leaf, parent)
                        if (unpinnedNames.isNotEmpty()) {
                            processContext.logger.warnChildOptionalsUnpinnedTypeParameters(property, unpinnedNames, parent)
                            return@property
                        }
                    }

                    val (accessorName, sourceAnnotation) =
                        if (parentOptionalAnnotation != null) {
                            val propertyGsa =
                                ParentOptionalSourceAnnotation(
                                    annotation = parentOptionalAnnotation,
                                    annotatedDeclaration = property,
                                    sweep = parentSourceAnnotation,
                                )
                            propertyGsa.accessorName to propertyGsa
                        } else {
                            parentSourceAnnotation.accessorNameFor(property) to parentSourceAnnotation
                        }

                    // A member always beats an extension, so the accessor would be dead code.
                    // Compared against the RESOLVED name, so a renaming template keeps the property.
                    if (parentOptionalAnnotation == null && accessorName in parentVisiblePropertyNames) return@property

                    // Checked last, so it only fires for a property the sweep would otherwise
                    // have picked up; an exclude on one already skipped above is the no-op warned
                    // at the end. An explicit @ParentOptional is a hand opt-in and beats it.
                    if (parentOptionalAnnotation == null && property.hasChildOptionalsExclude()) {
                        property.qualifiedName?.asString()?.let(excludeTookEffect::add)
                        return@property
                    }

                    entriesByAccessorName.getOrPut(accessorName) { mutableListOf() } +=
                        ParentOptionalEntry(
                            child = leaf,
                            property = property,
                            sourceAnnotation = sourceAnnotation,
                        )
                }
            }
            // An intermediate sealed type's own @ParentOptional properties are declared by no
            // leaf, so the sweep above misses them, while ownership cedes this ancestor to this
            // feature. One `is Intermediate` branch covers every leaf below it.
            parent.collectIntermediateSealedSubclasses().forEach intermediate@{ intermediate ->
                if (!intermediate.isAccessibleFromGeneratedAccessor()) return@intermediate
                intermediate.getDeclaredProperties().forEach property@{ property ->
                    val parentOptionalAnnotation = property.parentOptionalAnnotationOrNull() ?: return@property
                    if (!property.isAccessibleFromGeneratedAccessor()) return@property
                    if (property.isExtensionProperty()) return@property

                    val propertyGsa =
                        ParentOptionalSourceAnnotation(
                            annotation = parentOptionalAnnotation,
                            annotatedDeclaration = property,
                            sweep = parentSourceAnnotation,
                        )
                    val accessorName = propertyGsa.accessorName
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

        // An @ChildOptionals.Exclude that never removed a contribution: not under a
        // @ChildOptionals parent, or on a property the sweep would have skipped anyway. One that is
        // ALSO @ParentOptional is generated regardless, so its exclude is ignored by design.
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
 * The property an `@ChildOptionals.Exclude` opts out. The [KSValueParameter] branch covers a backend
 * that surfaces a primary-constructor `val`'s syntactic site, as `@ParentOptional` resolution does.
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
