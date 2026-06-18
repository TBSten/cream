package me.tbsten.cream.ksp.util

/**
 * Safe cast to [T], returning `null` when the receiver is not a [T] (including when it is `null`).
 *
 * A named alternative to the `as?` operator that reads well in call chains, e.g.
 * `value.safeCast<Boolean>()` or `node.safeCast<GenerateSourceAnnotation.CopyToChildren>()?.let { ... }`.
 */
internal inline fun <reified T> Any?.safeCast(): T? = this as? T
