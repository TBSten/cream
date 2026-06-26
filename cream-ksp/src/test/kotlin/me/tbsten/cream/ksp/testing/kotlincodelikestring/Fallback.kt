package me.tbsten.cream.ksp.testing.kotlincodelikestring

/**
 * @see me.tbsten.cream.ksp.testing.kotlincodelikestring.buildKotlinCodeLikeString
 */
fun interface Fallback : (Any?) -> String {
    companion object {
        fun throwError(message: (Any?) -> String = { "Cannot serialize `$it` to string for snapshot" }): Fallback = Fallback { error(message(it)) }

        fun comment(comment: (Any?) -> String = { "unknown: $it" }): Fallback = Fallback { "/* ${comment(it)} */" }
    }
}
