package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSAnnotation
import me.tbsten.cream.ksp.util.safeCast

/**
 * Read a `notCopyToObject` boolean from an annotation argument. Returns `null` when the argument is
 * absent (so callers can fall back to the `cream.notCopyToObject` option). KSP2's AA backend omits
 * arguments left at their default from [KSAnnotation.arguments], which is exactly the "unset" case.
 */
internal fun KSAnnotation.notCopyToObject(name: String = "notCopyToObject"): Boolean? =
    arguments
        .firstOrNull { it.name?.asString() == name }
        ?.value
        ?.safeCast<Boolean>()
