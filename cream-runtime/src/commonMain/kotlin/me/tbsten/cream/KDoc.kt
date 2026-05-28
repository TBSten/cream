package me.tbsten.cream

/**
 * Customize the KDoc of cream-generated copy/combine functions.
 *
 * Use as the `kdoc` argument of cream's source annotations (`@CopyTo`, `@CopyFrom`,
 * `@CopyToChildren`, `@CombineTo`, `@CombineFrom`, `@CopyMapping`, `@CombineMapping`).
 *
 * # Example
 *
 * ```kt
 * @CopyTo(
 *     Target::class,
 *     kdoc = KDoc(
 *         description = "This function should not be used in the case of ~.",
 *         examples = [
 *             """
 *             # Avoid this
 *
 *             ```kt
 *             val bad = source.copyToTarget() // do not!
 *             ```
 *             """,
 *         ],
 *     ),
 * )
 * class Source { /* ... */ }
 * ```
 *
 * Rendered order in the generated function's KDoc:
 * 1. Auto-generated header (`(Auto generate by @[...] ...)`)
 * 2. Auto-generated description line (`Source -> Target copy function.`)
 * 3. [description] (this annotation)
 * 4. Auto-generated `# Example` sections
 * 5. [examples] (this annotation, each rendered verbatim)
 * 6. `@see` references
 *
 * Both [description] and each [examples] entry are passed through `trimIndent`, then have
 * leading/trailing blank lines stripped. Entries are responsible for their own Markdown
 * headings and fenced code blocks.
 *
 * @param description Inserted right after the auto-generated description line and before the
 * auto-generated examples. Use this for high-level remarks or usage warnings.
 * @param examples Each entry is rendered verbatim after the auto-generated examples and
 * before the `@see` block. Provide your own `# heading` and ` ```kt ... ``` ` fences inside
 * each entry.
 */
@Target()
@Retention(AnnotationRetention.SOURCE)
annotation class KDoc(
    val description: String = "",
    val examples: Array<String> = [],
)
