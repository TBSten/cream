package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSAnnotation
import me.tbsten.cream.DefaultCopyFunctionName
import me.tbsten.cream.ksp.util.ksp.getArgument

/**
 * Read a `funName` template from an annotation argument. KSP surfaces the (constant-folded)
 * string the user wrote, e.g. `"to" + CopyTargetSimpleName` becomes
 * `"to{{cream:CopyTargetSimpleName}}"`. Falls back to [DefaultCopyFunctionName] when the argument
 * is absent — that is every annotation's declared default, so omitting `funName` keeps cream's
 * default name whatever expansion the annotation gives the token.
 */
internal fun KSAnnotation.funNameTemplate(name: String = "funName"): String = getArgument<String>(name) ?: DefaultCopyFunctionName
