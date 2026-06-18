package me.tbsten.cream

/**
 * Visibility modifier applied to the copy function cream generates.
 *
 * Generated copy functions are **top-level extension functions**, so only modifiers that
 * keep them usable are offered: [PUBLIC] and [INTERNAL] (plus [INHERIT]).
 *
 * `private` and `protected` are intentionally **not** provided: a `private` top-level
 * function would only be visible inside its generated file, and `protected` is not even
 * valid on top-level declarations. Either one would make the generated function unusable,
 * defeating the purpose of generating it, so they are excluded.
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
 * @see CombineTo
 * @see CombineFrom
 */
public enum class CopyVisibility {
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
}
