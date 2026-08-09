package me.tbsten.cream.ksp.core.callFrom

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import me.tbsten.cream.DefaultCopyFunctionName

/**
 * The (raw, un-escaped) name of the bridge generated for [targetFunction]: the `funName`
 * template with [DefaultCopyFunctionName] expanded to the function's own simple name.
 *
 * `CopyTarget*` tokens are left unexpanded here and rejected by `validateNoUnsupportedFunNameToken`.
 *
 * The name is returned RAW so the feature-layer collision check can compare it against existing
 * declarations' simple names; the generator escapes it before emitting it into source.
 */
internal fun resolveCallFromFunName(
    funNameTemplate: String,
    targetFunction: KSFunctionDeclaration,
): String = funNameTemplate.replace(DefaultCopyFunctionName, targetFunction.simpleName.asString())
