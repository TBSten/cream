package me.tbsten.cream.ksp.feature.parentOptional

import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.validate
import me.tbsten.cream.ChildOptionals
import me.tbsten.cream.ParentOptional
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
import me.tbsten.cream.ksp.core.parentOptional.firstInaccessibleClassOrNull
import me.tbsten.cream.ksp.core.parentOptional.isAccessibleFromGeneratedAccessor
import me.tbsten.cream.ksp.core.parentOptional.isExtensionProperty
import me.tbsten.cream.ksp.core.parentOptional.parentOptionalAnnotationOrNull
import me.tbsten.cream.ksp.core.parentOptional.reportParentOptionalExtensionProperty
import me.tbsten.cream.ksp.util.ksp.collectSealedAncestors
import me.tbsten.cream.ksp.util.with

private val annotationName = ParentOptional::class.simpleName!!

/** Accessor contributions for one sealed parent, keyed by accessor name in discovery order. */
private class ParentAccessorGroup(
    val parent: KSClassDeclaration,
) {
    val entriesByAccessorName = LinkedHashMap<String, MutableList<ParentOptionalEntry>>()
}

context(processContext: ProcessContext)
internal fun processParentOptional(): List<KSAnnotated> =
    with(processContext.logger, processContext.options) {
        val (annotatedSymbols, invalidSymbols) =
            processContext.resolver
                .getSymbolsWithAnnotation(
                    annotationName = ParentOptional::class.fullName,
                ).partition { it.validate() }

        // Group every (sealed ancestor, accessor name) pair across ALL annotated properties first,
        // then emit one file per sealed parent — merged accessors need to see every contribution.
        val groups = LinkedHashMap<String, ParentAccessorGroup>()
        // Defensive dedupe: should KSP surface both the property and its constructor parameter for
        // one source annotation, process the property once (twice would emit shadowed branches).
        val processedPropertyNames = mutableSetOf<String>()

        annotatedSymbols.forEach { annotated ->
            val property =
                annotated.asAnnotatedPropertyOrNull() ?: run {
                    processContext.logger.reportParentOptionalNotAProperty(annotated)
                    return@forEach
                }
            // qualifiedName can be null (e.g. a property of a local class); such a property never
            // reaches generation anyway (a local class has no sealed ancestors), so only skip the
            // *dedupe* — the sealed-parent diagnostic below still fires for it.
            val propertyKey = property.qualifiedName?.asString()
            if (propertyKey != null && !processedPropertyNames.add(propertyKey)) return@forEach

            // Read the raw KSAnnotation (not a getAnnotationsByType proxy): see the
            // GenerateSourceAnnotation docs for why KSP2's typed proxy is avoided.
            val annotation = property.parentOptionalAnnotationOrNull() ?: return@forEach
            val generateSourceAnnotation = GenerateSourceAnnotation.ParentOptional(annotation, property)

            if (property.isExtensionProperty()) {
                reportParentOptionalExtensionProperty(property)
                return@forEach
            }

            if (!property.isAccessibleFromGeneratedAccessor()) {
                processContext.logger.reportParentOptionalInaccessibleProperty(property)
                return@forEach
            }

            val child = property.parentDeclaration as? KSClassDeclaration
            val sealedAncestors = child?.collectSealedAncestors().orEmpty()
            if (child == null || sealedAncestors.isEmpty()) {
                processContext.logger.reportParentOptionalNoSealedParent(property)
                return@forEach
            }

            val inaccessibleClass = child.firstInaccessibleClassOrNull()
            if (inaccessibleClass != null) {
                processContext.logger.reportParentOptionalInaccessibleChild(property, inaccessibleClass)
                return@forEach
            }

            val accessorName = generateSourceAnnotation.propertyName ?: property.simpleName.asString()
            sealedAncestors
                // Ownership rule: an ancestor annotated with @ChildOptionals generates (and merges)
                // its own accessors, so the @ParentOptional feature must not emit a duplicate.
                .filterNot { ancestor -> ancestor.annotationsOf(ChildOptionals::class).any() }
                .forEach { parent ->
                    groups
                        .getOrPut(parent.fullName) { ParentAccessorGroup(parent) }
                        .entriesByAccessorName
                        .getOrPut(accessorName) { mutableListOf() }
                        .add(
                            ParentOptionalEntry(
                                child = child,
                                property = property,
                                sourceAnnotation = generateSourceAnnotation,
                            ),
                        )
                }
        }

        groups.values.forEach { group ->
            val parent = group.parent
            val sourceFiles =
                (
                    listOf(parent) +
                        group.entriesByAccessorName.values
                            .flatten()
                            .map { it.child }
                ).mapNotNull { it.containingFile }
                    .distinct()
            processContext.codeGenerator.createNewKotlinFile(
                dependencies = Dependencies(aggregating = true, *sourceFiles.toTypedArray()),
                packageName = parent.packageName,
                fileName = "ParentOptional__${parent.underPackageName}",
            ) { appender ->
                group.entriesByAccessorName.forEach { (accessorName, entries) ->
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

        return invalidSymbols
    }

/**
 * Resolve the annotated symbol to the property it opts in. `@ParentOptional` targets `PROPERTY`
 * only, so KSP2 normally hands us the [KSPropertyDeclaration]; the [KSValueParameter] branch
 * covers a backend that surfaces the syntactic site (a primary-constructor `val`) instead.
 */
private fun KSAnnotated.asAnnotatedPropertyOrNull(): KSPropertyDeclaration? =
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
// Diagnostic helpers — each encapsulates one user-misuse error so call sites
// stay concise and the message text lives in exactly one place.
// ---------------------------------------------------------------------------

private fun KSPLogger.reportParentOptionalNotAProperty(annotated: KSAnnotated) {
    reportCreamError(
        InvalidCreamUsageException(
            message = "@$annotationName must be applied to a property of a class in a sealed hierarchy.",
            solution = "Apply @$annotationName to a `val` / `var` declared by a sealed type's subclass.",
        ),
        annotated,
    )
}

private fun KSPLogger.reportParentOptionalInaccessibleProperty(property: KSPropertyDeclaration) {
    val displayName = property.qualifiedName?.asString() ?: property.simpleName.asString()
    reportCreamError(
        InvalidCreamUsageException(
            message =
                "@$annotationName property $displayName must be public or internal: the generated " +
                    "top-level accessor cannot read a ${property.getVisibility().name.lowercase()} property.",
            solution = "Make $displayName public or internal, or remove @$annotationName from it.",
        ),
        property,
    )
}

private fun KSPLogger.reportParentOptionalInaccessibleChild(
    property: KSPropertyDeclaration,
    inaccessibleClass: KSClassDeclaration,
) {
    val displayName = property.qualifiedName?.asString() ?: property.simpleName.asString()
    val className = inaccessibleClass.qualifiedName?.asString() ?: inaccessibleClass.simpleName.asString()
    reportCreamError(
        InvalidCreamUsageException(
            message =
                "@$annotationName property $displayName is declared by a class the generated " +
                    "accessor cannot reference: $className is " +
                    "${inaccessibleClass.getVisibility().name.lowercase()}, so the generated " +
                    "`is` check would not compile.",
            solution = "Make $className public or internal, or remove @$annotationName from $displayName.",
        ),
        property,
    )
}

private fun KSPLogger.reportParentOptionalNoSealedParent(property: KSPropertyDeclaration) {
    val displayName = property.qualifiedName?.asString() ?: property.simpleName.asString()
    reportCreamError(
        InvalidCreamUsageException(
            message =
                "@$annotationName property $displayName has no sealed parent type: its enclosing class " +
                    "does not (transitively) implement any sealed class/interface, so there is no " +
                    "receiver to generate the accessor on.",
            solution = "Make the enclosing class part of a sealed hierarchy, or remove @$annotationName.",
        ),
        property,
    )
}
