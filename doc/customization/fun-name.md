[← README](../../README.md) | [日本語](./fun-name.ja.md)

# Customizing the function name

A guide for customizing the names of the copy functions cream.kt generates.

There are two ways to customize the function name:

- set the `funName` property on the annotation
- set KSP options to configure module-wide naming rules

## Setting the funName property on the annotation

Use the `funName` property available on each annotation to set the name of the functions
generated from that annotation.

```kt
import me.tbsten.cream.CopyTo

@CopyTo(UiState.Success::class, funName = "copyToSuccess")
data object Loading

fun Loading.copyToSuccess(): UiState.Success
```

`funName` can also be combined with predefined tokens to build the name dynamically.

```kt
import me.tbsten.cream.CopyTo
import me.tbsten.cream.CopyTargetSimpleName

@CopyTo(UiState.Success::class, funName = "copyTo${CopyTargetSimpleName}")
data object Loading

// here CopyTargetSimpleName is replaced with `Success`

// auto generate
fun Loading.copyToSuccess(): UiState.Success = ...
```

The available tokens are listed below (the tokens are `const val`s, so combining them with `+`
or string templates keeps the whole expression a compile-time constant):

| Token | Expands to (target `com.example.UiState.Success`) |
|---|---|
| `DefaultCopyFunctionName` | cream's derived default name (`copyToUiStateSuccess`; `copy` for `@SealedCopy`) |
| `CopyTargetSimpleName` / `copy_target_simple_name` | `Success` / `success` |
| `CopyTargetUnderPackage` / `copy_target_under_package` | `UiStateSuccess` / `uistate_success` |
| `CopyTargetInnerName` / `copy_target_inner_name` | `Success` / `success` |
| `CopyTargetFullName` / `copy_target_full_name` | `ComExampleUiStateSuccess` / `com_example_uistate_success` |

When one annotation generates more than one function (multiple targets, a sealed target, …), a
literal-only `funName` would give every function the same name and is rejected at build time.
Include a token so each function gets a distinct name.

## Configuring module-wide naming rules via KSP options

KSP options control the naming rules for all copy functions in the module.

```kts
// module/build.gradle.kts

ksp {
    arg("cream.copyFunNamePrefix", "copyTo")
    arg("cream.copyFunNamingStrategy", "under-package")
    arg("cream.escapeDot", "replace-to-underscore")
}
```

### `cream.copyFunNamePrefix`

| Default  | Possible values  |
|----------|------------------|
| `copyTo` | Arbitrary string |

Sets the string prefixed to the generated copy function name. Use a straightforward string that
describes the copy or state transition, such as `copyTo` or `to`. When the prefix ends with a
letter, the first character of the following name is capitalized on concatenation
(e.g. `copyTo` + `uiStateSuccess` → `copyToUiStateSuccess`).

```kts
// module/build.gradle.kts
ksp {
    arg("cream.copyFunNamePrefix", "transitionTo")
}
```

```kt
@CopyTo(UiState.Success::class)
data object Loading

// auto generate — the prefix becomes transitionTo
fun Loading.transitionToUiStateSuccess(/* ... */): UiState.Success = /* ... */
```

### `cream.copyFunNamingStrategy`

| Default         | Possible values |
|-----------------|-----------------|
| `under-package` | One of `under-package`, `diff`, `simple-name`, `full-name`, `inner-name` |

Sets how the class-name part after the prefix is built (every example in the table below is for a
transition `com.example.Aaa.Bbb` -> `com.example.Aaa.Bbb.Ccc.Ddd`).

| Value           | Description | Strategy result → generated function |
|-----------------|-------------|----------------------------------------|
| `under-package` | Uses a name reflecting the package hierarchy. | `Aaa.Bbb.Ccc.Ddd` → `Aaa.Bbb.copyToAaaBbbCccDdd(...)` |
| `diff`          | Uses only the difference from the source class. | `.Ccc.Ddd` → `Aaa.Bbb.copyToCccDdd(...)` |
| `simple-name`   | Uses the target class' `::class.simpleName`. | `Ddd` → `Aaa.Bbb.copyToDdd(...)` |
| `full-name`     | Uses the target class' `::class.qualifiedName`. | `com.example.Aaa.Bbb.Ccc.Ddd` → `Aaa.Bbb.copyToComExampleAaaBbbCccDdd(...)` |
| `inner-name`    | Uses the class names from the second nesting level onward. (Same as `simple-name` for non-nested classes) | `Bbb.Ccc.Ddd` → `Aaa.Bbb.copyToBbbCccDdd(...)` |

<img src="../cream.copyFunNamingStrategy.png" width="800" alt="Diagram of which part of the fully qualified class name each strategy uses" />

```kts
// module/build.gradle.kts
ksp {
    arg("cream.copyFunNamingStrategy", "simple-name")
}
```

```kt
@CopyTo(UiState.Success::class)
data object Loading

// auto generate — only the target's simpleName (Success) is used
fun Loading.copyToSuccess(/* ... */): UiState.Success = /* ... */
```

### `cream.escapeDot`

| Default            | Possible values |
|--------------------|-----------------|
| `lower-camel-case` | One of `lower-camel-case`, `replace-to-underscore` |

Kotlin function names cannot normally contain `.`, so the class name has to be converted into a
legal identifier in one of the ways shown below.

Sets how the class name produced by `cream.copyFunNamingStrategy` is escaped.
Escaping does not change the letter case of the class names — `lower-camel-case` joins the
segments in camelCase and lowercases only the very first character, while `replace-to-underscore`
replaces each `.` with `_` (in both cases the first character is capitalized again when the name
is concatenated after the prefix).

```kts
// module/build.gradle.kts
ksp {
    arg("cream.escapeDot", "replace-to-underscore")
}
```

```kt
@CopyTo(UiState.Success::class)
data object Loading

// auto generate — the `.` in the under-package result UiState.Success becomes `_`
fun Loading.copyTo_UiState_Success(/* ... */): UiState.Success = /* ... */
```

## See also

- [KSP options](./options.md) — index of all KSP arguments
- [KDoc](./kdoc.md) — the `kdoc = KDoc(...)` argument for generated functions
- [Visibility](./visibility.md) — the `visibility` argument and `cream.defaultVisibility`
- [Copy — @CopyTo / @CopyFrom / @CopyMapping](../copy.md)
