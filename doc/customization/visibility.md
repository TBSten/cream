[← README](../../README.md) | [日本語](./visibility.ja.md)

# Visibility

By default the generated copy function inherits the visibility of the target (or sealed)
declaration it is derived from. Pass `visibility = CopyVisibility.<...>` to the copy-generating
annotations to force a specific visibility instead, or set
a module-wide default with the
[`cream.defaultVisibility`](#module-wide-default-creamdefaultvisibility) KSP option. For a
reversible (`canReverse`) `@CopyMapping`, the same visibility is applied to both the forward and
reverse functions.

## Quick example

```kt
import me.tbsten.cream.CopyTo
import me.tbsten.cream.CopyVisibility

@CopyTo(MergedState::class, visibility = CopyVisibility.INTERNAL)
data class ServerState(val shared: String)

// auto generate
internal fun ServerState.copyToMergedState(
    shared: String = this.shared,
    /* ... */
): MergedState = ...
```

## `CopyVisibility` values

`CopyVisibility` has the following values. Generated copy functions are top-level extension
functions, so only modifiers that keep them usable are offered. `private` (visible only inside
the generated file) and `protected` (not valid on top-level declarations) would make the
generated function unusable, so they are intentionally not provided:

| Value | Generated modifier |
|-------|--------------------|
| `INHERIT` (default) | Inherits the target/sealed declaration's visibility |
| `PUBLIC` | `public` |
| `INTERNAL` | `internal` |

Omitting `visibility` is fully backward compatible — it keeps the previously generated code unchanged.

## Module-wide default: `cream.defaultVisibility`

To set a default for the whole module instead of annotating every declaration, use the
`cream.defaultVisibility` KSP option.

It sets the module-wide default visibility for every generated copy / combine function — the
project-level counterpart of the per-annotation `visibility = CopyVisibility.<...>` argument.

| Default   | Possible values                        |
|-----------|----------------------------------------|
| `INHERIT` | One of `INHERIT`, `PUBLIC`, `INTERNAL` |

```kts
// module/build.gradle.kts

ksp {
    arg("cream.defaultVisibility", "INTERNAL")
}
```

For example, with `cream.defaultVisibility=INTERNAL`, a plain `@CopyTo(Target::class)` generates an
`internal` copy function without having to add `visibility = CopyVisibility.INTERNAL` to each
annotation.

## Precedence

The visibility of a generated function is decided in this order:

1. An explicit annotation `visibility` (anything other than `INHERIT`) — always wins.
2. Otherwise `cream.defaultVisibility`, when it is `PUBLIC` or `INTERNAL`.
3. Otherwise (both are `INHERIT`) the generated function inherits the target/sealed declaration's
   own visibility, exactly as before this option existed.

## See also

- [KSP Options](./options.md) — index of module-wide KSP options
- [Function name (funName)](./fun-name.md) — customize the generated function name
- [KDoc customization](./kdoc.md) — augment the generated function's KDoc
