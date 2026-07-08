package me.tbsten.cream.ksp.core.callFrom

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import me.tbsten.cream.DefaultCallFromFunctionName

/**
 * The (raw, un-escaped) name of the bridge generated for [targetFunction].
 *
 * The default sentinel [DefaultCallFromFunctionName] resolves to the target function's own simple
 * name — the same-name overload that has always been `@CallFrom`'s behaviour. Any other value is a
 * plain literal used verbatim: `@CallFrom` supports no `CopyTarget*` naming tokens, because it has
 * no target *class* to render (its target is a function plus an args class), so there is no
 * template to expand.
 *
 * The name is returned RAW so the feature-layer collision check can compare it against existing
 * declarations' simple names; the generator escapes it via `escapeKotlinIdentifier` before
 * emitting it into source.
 */
internal fun resolveCallFromFunName(
    funNameTemplate: String,
    targetFunction: KSFunctionDeclaration,
): String =
    if (funNameTemplate == DefaultCallFromFunctionName) {
        targetFunction.simpleName.asString()
    } else {
        funNameTemplate
    }
