package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSType
import kotlin.reflect.KClass

/**
 * Annotations on this symbol whose type resolves to [annotationClass]. Keeps every
 * occurrence, so `@Repeatable` annotations are all returned.
 */
internal fun KSAnnotated.annotationsOf(annotationClass: KClass<*>): Sequence<KSAnnotation> =
    annotations.filter {
        it.annotationType
            .resolve()
            .declaration.fullName == annotationClass.qualifiedName
    }

/**
 * Flatten a `List<KClass<*>>` annotation argument (e.g. `targets` / `sources`) across
 * all of these annotations into the referenced [KSType]s.
 */
internal fun Sequence<KSAnnotation>.classListArgument(name: String): Sequence<KSType> =
    flatMap { annotation ->
        annotation.arguments
            .filter { it.name?.asString() == name }
            .map { it.value }
            .filterIsInstance<List<KSType>>()
            .flatten()
    }

/**
 * Extract the `properties` argument (a list of `@...Map(source =, target =)` instances)
 * into (sourceProperty -> targetProperty) pairs. Entries missing either end are skipped;
 * returns empty when the argument is absent.
 */
internal fun KSAnnotation.extractPropertyMappings(): List<Pair<String, String>> {
    val properties =
        arguments
            .firstOrNull { it.name?.asString() == "properties" }
            ?.value as? List<*>
            ?: return emptyList()
    return properties.mapNotNull { mapping ->
        val mapAnnotation = mapping as? KSAnnotation ?: return@mapNotNull null
        val sourceProperty =
            mapAnnotation.arguments
                .firstOrNull { it.name?.asString() == "source" }
                ?.value as? String
        val targetProperty =
            mapAnnotation.arguments
                .firstOrNull { it.name?.asString() == "target" }
                ?.value as? String
        if (sourceProperty != null && targetProperty != null) {
            sourceProperty to targetProperty
        } else {
            null
        }
    }
}

/**
 * Extract `kdoc` argument (a [me.tbsten.cream.KDoc] annotation instance) into a
 * (description, examples) pair. Returns empty when the argument is absent or empty.
 */
internal fun KSAnnotation.extractKDoc(): Pair<String, List<String>> {
    val kdocAnnotation =
        arguments
            .firstOrNull { it.name?.asString() == "kdoc" }
            ?.value as? KSAnnotation
            ?: return "" to emptyList()
    val description =
        kdocAnnotation.arguments
            .firstOrNull { it.name?.asString() == "description" }
            ?.value as? String
            ?: ""
    val examples =
        (
            kdocAnnotation.arguments
                .firstOrNull { it.name?.asString() == "examples" }
                ?.value as? List<*>
        )?.filterIsInstance<String>()
            ?: emptyList()
    return description to examples
}
