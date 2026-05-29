package me.tbsten.cream.ksp.util

import com.google.devtools.ksp.symbol.KSAnnotation

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
