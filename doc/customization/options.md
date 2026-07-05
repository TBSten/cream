[← README](../../README.md) | [日本語](./options.ja.md)

# KSP Options

Several KSP options are provided to customize the generated copy functions.
All options are optional. Set them as needed via `ksp { arg(...) }` in your module's
`build.gradle.kts`:

```kts
// module/build.gradle.kts

ksp {
    arg("cream.copyFunNamePrefix", "copyTo")
    arg("cream.copyFunNamingStrategy", "under-package")
    arg("cream.escapeDot", "replace-to-underscore")
    arg("cream.notCopyToObject", "false")
    arg("cream.defaultVisibility", "INHERIT")
}
```

[Option Builder](https://tbsten.github.io/cream/option-builder) is useful for verifying the
behavior of each option.

## Option index

Each option is documented in detail on its topic page:

| Option name                       | Description                                                                                                      | Default            | Details                                                                          |
|-----------------------------------|------------------------------------------------------------------------------------------------------------------|--------------------|-----------------------------------------------------------------------------------|
| **`cream.copyFunNamePrefix`**     | String prefixed to the generated copy function (`copyTo`, `transitionTo`, `to`, `mapTo`, ...)                    | `copyTo`           | [Function name (funName)](./fun-name.md#creamcopyfunnameprefix)                  |
| **`cream.copyFunNamingStrategy`** | Copy function naming convention (`under-package`, `diff`, `simple-name`, `full-name`, `inner-name`)              | `under-package`    | [Function name (funName)](./fun-name.md#creamcopyfunnamingstrategy)              |
| **`cream.escapeDot`**             | How to escape `.` in the name given by `cream.copyFunNamingStrategy` (`lower-camel-case`, `replace-to-underscore`) | `lower-camel-case` | [Function name (funName)](./fun-name.md#creamescapedot)                          |
| **`cream.notCopyToObject`**       | If `true`, `@CopyToChildren` will not generate a copy function to the `object`                                   | `false`            | [@CopyToChildren](../copy-to-children.md)                                         |
| **`cream.defaultVisibility`**     | Module-wide default visibility for generated functions, applied when an annotation's `visibility` is `INHERIT`   | `INHERIT`          | [Visibility](./visibility.md#module-wide-default-creamdefaultvisibility)         |

## See also

- [Function name (funName)](./fun-name.md) — naming options in detail + per-declaration `funName` override
- [Visibility](./visibility.md) — `cream.defaultVisibility` and the per-annotation `visibility` argument
- [@CopyToChildren](../copy-to-children.md) — `cream.notCopyToObject` (module-wide) and the annotation's `notCopyToObject` property
