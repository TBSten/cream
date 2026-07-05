[← README](../../README.md) | [日本語](./kdoc.ja.md)

# KDoc customization

Every source annotation accepts a `kdoc = KDoc(...)`
parameter that lets you augment the KDoc of the generated function with your own
notes and examples.

## Quick example

```kt
import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    Target::class,
    kdoc = KDoc(
        description = "This function should not be used in the case of ~.",
        examples = [
            """
            # Prefer this

            ```kt
            val target = source.copyToTarget()
            ```
            """,
        ],
    ),
)
data class Source(val shared: String)
```

<details>
<summary>Generated code</summary>

````kt
/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 *
 * Source -> Target copy function.
 *
 * This function should not be used in the case of ~.
 *
 * # Example: Basic
 *
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget()
 * ```
 *
 * # Example: Override property values
 *
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget(property = value)
 * ```
 *
 * # Prefer this
 *
 * ```kt
 * val target = source.copyToTarget()
 * ```
 *
 * @see Source
 * @see Target
 */
fun Source.copyToTarget(
    shared: String = this.shared,
): Target = Target(
    shared = shared,
)
````

</details>

## Generated KDoc section order

The generated KDoc renders sections in this order:

1. Auto-generated header (`(Auto generate by @[...] of [...])`)
2. Auto-generated description (`Source -> Target copy function.`)
3. `KDoc.description` (when supplied)
4. Auto-generated `# Example: Basic` / `# Example: Override property values`
5. `KDoc.examples` (each entry, rendered verbatim after `trimIndent`)
6. `@see` references

## Details

Each `examples` entry is rendered verbatim — provide your own `# heading` and
` ```kt ... ``` ` fences inside each entry.

## See also

- [Visibility](./visibility.md) — control the visibility modifier of generated functions
- [Function name (funName)](./fun-name.md) — override the generated function name per declaration
- [KSP Options](./options.md) — index of module-wide KSP options
