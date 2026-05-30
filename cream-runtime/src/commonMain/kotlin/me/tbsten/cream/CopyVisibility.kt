package me.tbsten.cream

/**
 * Visibility modifier applied to the copy function cream generates.
 *
 * Generated copy functions are **top-level extension functions**. Kotlin only allows
 * `public` / `internal` / `private` on top-level declarations (`protected` is not valid
 * for them), so only those three concrete modifiers are exposed here.
 *
 * # Example
 *
 * ```kt
 * @CopyTo(MergedState::class, visibility = CopyVisibility.INTERNAL)
 * data class ServerState(/* ... */)
 *
 * // Auto generate
 *
 * internal fun ServerState.copyToMergedState(/* ... */): MergedState = /* ... */
 * ```
 *
 * @see CopyTo
 * @see CopyFrom
 * @see CopyToChildren
 * @see SealedCopy
 */
enum class CopyVisibility {
    /**
     * Keep cream's current behaviour: the generated function inherits the visibility of
     * the target/sealed declaration it is derived from. This is the default so that
     * omitting `visibility` does not change any previously generated code.
     */
    INHERIT,

    /** Emit a `public` copy function. */
    PUBLIC,

    /** Emit an `internal` copy function. */
    INTERNAL,

    /** Emit a `private` copy function (visible only within the generated file's scope). */
    PRIVATE,
}
