# cream.kt

cream.kt is a KSP Plugin that makes it easy to copy across classes.

## 0. Quick Summary

- Generates copy functions for classes annotated with `@CopyTo(<target-class>::class)`,
  `@CopyFrom(<source-class>::class)`.
    - Example of generated copy functions: `fun UiState.toLoading(): Loading`,
      `fun UiState.toSuccess(data: Data): Success`
- Generates copy functions from classes annotated with `@CopyToChildren` to all their child classes.

## 1. Motivation

Suppose you have a UiState like this in your project:

```kt
sealed interface MyUiState {
    val userName: String
    val password: String

    data class Loading(
        override val userName: String,
        override val password: String,
    ) : MyUiState

    data class Stable(
        override val userName: String,
        override val password: String,
        val loadedData: List<String>,
    ) : MyUiState
}
```

When MyUiState transitions from Loading to Stable:

```kt
val prevState: MyUiState.Loading = TODO()
val loadedData: List<String> = TODO()

MyUiState.Stable(
    // ⚠️ See here !
    userName = prevState.userName,
    password = prevState.password,
    loadedData = loadedData,
)
```

Look at the 2 lines below "⚠️ See here !".
We're instantiating a Stable state by inheriting data from prevState, but this means that changes to
MyUiState
(ex. adding, removing properties) will affect the child class MyUiState.Stable.
Every time MyUiState properties increase, we need to add more copy code.

Using cream.kt, the previous code can be simplified as follows:

```kt
val prevState: MyUiState.Loading = TODO()
val loadedData: List<String> = TODO()

prevState.toStable(
    loadedData = loadedData,
)
```

The `userName = prevState.userName, password = prevState.password,` part is gone and it's much
cleaner.

When there's no particular reason not to inherit previous values (in the example above, prevState:
MyUiState.Loading), this behavior is similar to **data class copy methods**.
Unlike copy, **cream.kt enables state transitions across classes** (in the example above, we're
copying state across classes from .Loading -> .Stable).

## 2. Setup

```kts
// module/build.gradle.kts
plugins {
    alias("com.google.devtools.ksp") version "<ksp-version>"
}

dependencies {
    implementation("me.tbsten.cream:cream-runtime:<ksp-version>")
    ksp("me.tbsten.cream:cream-ksp:<ksp-version>")
}
```

## 3. Usage

### CopyTo

Generates copy functions to transition from classes annotated with `@CopyTo` to the specified target
class.

```kt
@CopyTo(UiState.Success::class)
class UiState {
    data class Success(
        val data: Data,
    )
}

// auto generate
fun UiState.copyToUiStateSuccess(
    data: Data,
): UiState.Success = /* ... */

// usage
val uiState: UiState = /* ... */
val nextUiState: UiState.Success = uiState.copyToUiStateSuccess(
    data = /* ... */,
)
```

Copy functions are generated for each constructor of the target class.
Constructor arguments that match property names of the source class are set as default values.

```kt
@CopyTo(UiState.Success::class)
class ItemDetailUiState(
    val itemId: String
) {
    data class Success(
        override val itemId: String,
        val data: Data,
    )
}

// auto generate
fun UiState.copyToUiStateSuccess(
    itemId: String = this.itemId,
    data: Data,
): UiState.Success = /* ... */

// usage
val uiState: UiState = /* ... */
val nextUiState: UiState.Success = uiState.copyToUiStateSuccess(
    data = /* ... */,
)
```

### CopyFrom

Similar to `@CopyTo`, but differs in that the **source** class is specified as an argument.

```kt
class UiState {
    @CopyFrom(UiState::class)
    data class Success(
        val data: Data,
    )
}

// auto generate
fun UiState.toUiStateSuccess(
    data: Data,
): UiState.Success = /* ... */
```

### CopyToChildren

When applied to a sealed class/interface, automatically generates copy functions from that sealed
class/interface to all classes that inherit from it.

```kt
@CopyToChildren
sealed interface UiState {
    data object Loading : UiState

    sealed interface Success : UiState {
        val data: Data

        data class Done(
            override val data: Data,
        ) : Success

        data class Refreshing(
            override val data: Data,
        ) : Success
    }
}

// auto generate
fun UiState.copyToUiStateSuccessDone(
    data: Data,
): UiState.Success.Done = /* ... */

fun UiState.copyToUiStateSuccessRefreshing(
    data: Data,
): UiState.Success.Refreshing = /* ... */
```

## 4. Options

Several options are available to customize behavior.
All option settings are optional. Configure as needed.

```kts
// module/build.gradle.kts

ksp {
    arg("cream.copyFunNamePrefix", "copyTo")
    arg("cream.copyFunNamingStrategy", "under-package")
    arg("cream.escapeDot", "replace-to-underscore")
}
```

| Option | Description | Default | Examples |
| ----------------------------- | ------------------------------------------------------------------------------------- | ------------------------- | ---------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| `cream.copyFunNamePrefix`     | Prefix for generated copy function names. Can be set to any
string. | `copyTo`                  | `copyTo`, `transitionTo`, `mapTo`                          |
| | | | `copyTo`                                                   | Functions like `copyToHoge`,
`copyToFuga` will be generated. |
| `cream.copyFunNamingStrategy` | Method for setting names after `cream.copyFunNamePrefix` in
generated copy functions. | `under-package`           | `under-package`, `diff-parent`,
`simple-name`, `full-name` |
| | | | `under-package`                                            |
`com.example.ParentClass.ChildClass` -> Functions like prefix + `ParentClassChildClass`(...) will be
generated |
| | | | `diff-parent`                                              | Copy from
`com.example.ParentClass` to `com.example.ParentClass.ChildClass` -> Functions like prefix +
`ChildClass`(...) will be generated |
| | | | `simple-name`                                              |
`com.example.ParentClass.ChildClass` -> Functions like prefix + `ChildClass`(...) will be
generated |
| | | | `full-name`                                                |
`com.example.ParentClass.ChildClass` -> Functions like prefix +
`com.example.ParentClass.ChildClass`(...) will be generated |
| `cream.escapeDot`             | Method for escaping `.` in generated copy function names. |
`"replace-to-underscore"` | `replace-to-underscore`, `pascal-case`                     |
| | | | `replace-to-underscore`                                    | `.` will be replaced with
`_`. |
| | | | `pascal-case`                                              | `.` is treated as word
boundaries, and each word's first letter is capitalized and concatenated. |
