package me.tbsten.cream.ksp.util

/**
 * Calls [block] with [a] and [b] as context receivers, avoiding nested `with` calls.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <A, B, R> with(
    a: A,
    b: B,
    block: context(A, B) () -> R,
): R = with(a) { with(b) { block() } }
