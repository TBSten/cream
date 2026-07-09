package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSAnnotation
import me.tbsten.cream.DefaultCopyFunctionName
import me.tbsten.cream.ksp.util.ksp.getArgument

/**
 * Read a `funName` template from an annotation argument. KSP surfaces the (constant-folded)
 * string the user wrote, e.g. `"to" + CopyTargetSimpleName` becomes
 * `"to{{cream:CopyTargetSimpleName}}"`. Falls back to [default] when the argument is absent, so
 * omitting `funName` keeps cream's derived name. [default] is [DefaultCopyFunctionName] for the
 * copy/combine annotations; `@CallFrom` passes its own sentinel
 * ([me.tbsten.cream.DefaultCallFromFunctionName]).
 */
internal fun KSAnnotation.funNameTemplate(
    name: String = "funName",
    default: String = DefaultCopyFunctionName,
): String = getArgument<String>(name) ?: default
