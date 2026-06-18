package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSAnnotation
import me.tbsten.cream.CopyToChildren
import me.tbsten.cream.ksp.util.ksp.getArgument

/**
 * Read the `@CopyToChildren.notCopyToObject` boolean. Returns `null` when the argument is absent (so
 * callers can fall back to the `cream.notCopyToObject` option). KSP2's AA backend omits arguments
 * left at their default from [KSAnnotation.arguments], which is exactly the "unset" case.
 */
internal fun KSAnnotation.notCopyToObject(): Boolean? = getArgument(CopyToChildren::notCopyToObject)
