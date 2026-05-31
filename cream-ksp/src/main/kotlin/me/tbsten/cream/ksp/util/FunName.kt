package me.tbsten.cream.ksp.util

import com.google.devtools.ksp.symbol.KSAnnotation
import me.tbsten.cream.DefaultCopyFunctionName

/**
 * Read a `funName` template from an annotation argument. KSP surfaces the (constant-folded)
 * string the user wrote, e.g. `"to" + CopyTargetSimpleName` becomes
 * `"to{{cream:CopyTargetSimpleName}}"`. Falls back to [DefaultCopyFunctionName] when the
 * argument is absent, so omitting `funName` keeps cream's derived name.
 */
internal fun KSAnnotation.funNameTemplate(name: String = "funName"): String {
    val value =
        arguments
            .firstOrNull { it.name?.asString() == name }
            ?.value
    return value as? String ?: DefaultCopyFunctionName
}
