# cream.kt

![Maven Central Version](https://img.shields.io/maven-central/v/me.tbsten.cream/cream-runtime)
![GitHub License](https://img.shields.io/github/license/TBSten/cream)
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/TBSten/cream)

English |
<a href="https://github.com/TBSten/cream/blob/main/README.ja.md">日本語</a> |
<a href="https://deepwiki.com/TBSten/cream">DeepWiki</a>

cream.kt is a KSP Plugin that enables **declarative data copy** and makes it easy to **copy across classes**.

Automatically generates a Mapper that copies an object to another instance of approximately the same
class.

## ⭐️ 0. Quick Summary

**KSP Plugin that automatically generates cross-class copy functions**

- **Before**: Manually copy properties one by one
- **After**: One-line conversion with `prevState.toNextState(data = newData)`. Readability is
  improved by eliminating the need to hand over non-trivial data.

```kt
// Traditional approach
// ❌ It is difficult to see which specific data has been added or changed.
MyUiState.Success(
    userName = prevState.userName,    // manual copy
    password = prevState.password,    // manual copy
    data = newData
)

// With cream.kt
// (toSuccess is generated automatically)
// ✅ A quick glance at the data added
prevState.copyToSuccess(data = newData)  // automatic copy
```

## 🤔 1. Motivation

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

prevState.copyToStable(
    loadedData = loadedData,
)
```

The `userName = prevState.userName, password = prevState.password,` part is gone and it's much
cleaner.

When there's no particular reason not to inherit previous values (in the example above, prevState:
MyUiState.Loading), this behavior is similar to **data class copy methods**.
Unlike copy, **cream.kt enables state transitions across classes** (in the example above, we're
copying state across classes from .Loading -> .Stable).

## ⚙️ 2. Setup

|                   |                                                                         |
|-------------------|-------------------------------------------------------------------------|
| `<cream-version>` | ![GitHub Release](https://img.shields.io/github/v/release/TBSten/cream?) |
| `<ksp-version>`   | ![GitHub Release](https://img.shields.io/github/v/release/google/ksp)   |

```kts
// module/build.gradle.kts
plugins {
    id("com.google.devtools.ksp") version "<ksp-version>"
}

dependencies {
    implementation("me.tbsten.cream:cream-runtime:<cream-version>")
    ksp("me.tbsten.cream:cream-ksp:<cream-version>")
}
```

<details>

<summary> Kotlin Multiplatform project </summary>

Currently KSP does not support generating code in intermediate source sets such as Kotlin
Multiplatform's commonMain .
([reference](https://github.com/google/ksp/issues/567))
This limitation currently prevents cream.kt from generating copy functions from classes such as
commonMain.

However, by setting it up as follows, you can enable code generation for only the commonMain code.
(Note that in this case, annotations for each platform will not be processed.)

```kt
fun Project.setupKspForMultiplatformWorkaround() {
    kotlin.sourceSets.commonMain {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }

    tasks.configureEach {
        if (name.startsWith("ksp") && name != "kspCommonMainKotlinMetadata") {
            dependsOn(tasks.named("kspCommonMainKotlinMetadata"))
            enabled = false
        }
    }
}
setupKspForMultiplatformWorkaround()
```

refs: https://github.com/TBSten/cream/blob/main/test/build.gradle.kts#L54-L66

</details>

## ❇️ 3. Usage

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
data class DataLayerModel(
    val data: Data,
)

@CopyFrom(DataLayerModel::class)
data class DomainLayerModel(
    val data: Data,
)

// auto generate
fun DataLayerModel.toDomainLayerModel(
    data: Data,
): DomainLayerModel = /* ... */
```

### CopyToChildren

When applied to a sealed class/interface, automatically generates copy functions from that sealed
class/interface to **all transitive concrete leaves** that inherit from it — recursing through any
intermediate sealed types, not just the direct children.

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

In the example above, `Done` and `Refreshing` are nested under the intermediate `sealed interface
Success`, yet copy functions are still generated for them — the generation recurses through every
intermediate sealed type down to the concrete leaves.

This is much easier than specifying @CopyTo for each sealed class/interface.

### SealedCopy

When applied to a sealed class/interface, generates a `copy()` extension on the
sealed parent that preserves the original subtype while updating shared abstract properties.

Unlike `@CopyToChildren` (which generates per-child copy functions whose return type is the
child), `@SealedCopy` keeps the parent type as both receiver and return type.

```kt
@SealedCopy
sealed interface MyState {
    val name: String
    val count: Int

    data class Loading(override val name: String, override val count: Int) : MyState
    data class Success(
        override val name: String,
        override val count: Int,
        val data: String,
    ) : MyState
}
```

<details>
<summary> Generated code </summary>

```kt
fun MyState.copy(
    name: String = this.name,
    count: Int = this.count,
): MyState = when (this) {
    is MyState.Loading -> this.copy(name = name, count = count)
    is MyState.Success -> this.copy(name = name, count = count)
}
```

</details>

```kt
// usage
val state: MyState = MyState.Loading("a", 1)
val updated: MyState = state.copy(name = "b")  // MyState.Loading("b", 1)
```

By default, an `object` subtype (or a non-data class without a compatible `copy(...)`) is
treated as non-copyable and triggers a compile-time error (`nonCopyableStrategy = ERROR`).
Change `nonCopyableStrategy` to control how non-copyable subtypes are handled.

<details>
<summary> <code>nonCopyableStrategy = RETURN_AS_IS</code> </summary>

Returns the instance **unchanged** (`-> this`) when it cannot be copied.

```kt
@SealedCopy(nonCopyableStrategy = NonCopyableStrategy.RETURN_AS_IS)
sealed interface MyState {
    val name: String

    data class Loading(override val name: String) : MyState
    data object Empty : MyState { override val name: String = "" }
}

// Generated code
fun MyState.copy(
    name: String = this.name,
): MyState = when (this) {
    is MyState.Empty -> this  // non-copyable: returned as-is
    is MyState.Loading -> this.copy(name = name)
}
```

</details>

<details>
<summary> <code>nonCopyableStrategy = RETURN_NULL</code> </summary>

Returns **null** when it cannot be copied. The generated function's return type widens to `MyState?`.

```kt
@SealedCopy(nonCopyableStrategy = NonCopyableStrategy.RETURN_NULL)
sealed interface MyState {
    val name: String

    data class Loading(override val name: String) : MyState
    data object Empty : MyState { override val name: String = "" }
}

// Generated code
fun MyState.copy(
    name: String = this.name,
): MyState? = when (this) {
    is MyState.Empty -> null  // non-copyable: null
    is MyState.Loading -> this.copy(name = name)
}
```

</details>

### CombineTo

Use `@CombineTo` to generate copy functions **from multiple source classes to a single target class**.
This is useful when combining multiple data sources to create a single state.

```kt
@CombineTo(SuccessState::class)
data class LoadingState(
    val itemId: String,
)

@CombineTo(SuccessState::class)
data class SuccessAction(
    val data: Data,
)

data class SuccessState(
    val itemId: String,  // from LoadingState.itemId
    val data: Data,      // from SuccessAction.data
    val lastUpdateAt: Date,
)

// auto generate
fun LoadingState.copyToSuccessState(
    successAction: SuccessAction,
    itemId: String = this.itemId,
    data: Data = successAction.data,
    lastUpdateAt: Date,
): SuccessState = /* ... */

// usage
val loadingState: LoadingState = /* ... */
val action: SuccessAction = /* ... */
val successState: SuccessState = loadingState.copyToSuccessState(
    successAction = action,
    lastUpdateAt = Date(),
)
```

When multiple source classes have the same property name, **the argument (non-receiver) source wins** —
that is, the value comes from a source passed as an argument rather than from the receiver. Because the
generated function is an extension on the primary source (the receiver) with the other sources passed as
arguments, the last-listed argument source takes precedence over the receiver for the overlapping property.

### CombineFrom

`@CombineFrom` is the inverse of `@CombineTo`, where you specify multiple source classes **on the target side**.

```kt
data class LoadingState(
    val itemId: String,
)

data class SuccessAction(
    val data: Data,
)

@CombineFrom(LoadingState::class, SuccessAction::class)
data class SuccessState(
    val itemId: String,  // from LoadingState.itemId
    val data: Data,      // from SuccessAction.data
    val lastUpdateAt: Date,
)

// auto generate
fun LoadingState.copyToSuccessState(
    successAction: SuccessAction,
    itemId: String = this.itemId,
    data: Data = successAction.data,
    lastUpdateAt: Date,
): SuccessState = /* ... */
```

Both `@CombineTo` and `@CombineFrom` generate the same functions, but differ in where the annotation is placed:
- Use `@CombineTo` if you can modify the source side
- Use `@CombineFrom` if you can modify the target side

### CopyTo.Map, CopyFrom.Map, CombineTo.Map, CombineFrom.Map

You can use `@CopyTo.Map`, `@CopyFrom.Map`, `@CombineTo.Map`, and `@CombineFrom.Map` to map
properties between source and target classes. This is useful when the property names differ
between the source and target but you want to copy values between them.

#### CopyTo.Map / CopyFrom.Map

```kt
@CopyTo(DataModel::class)
data class DomainModel(
    @CopyTo.Map("dataId")
    val domainId: String,
)

data class DataModel(
    val dataId: String,
)

// auto generate
fun DomainModel.copyToDataModel(
    dataId: String = this.domainId, // domainId is mapped to dataId
): DataModel = ...
```

```kt
@CopyFrom(DataModel::class)
data class DomainModel(
    @CopyFrom.Map("dataId")
    val domainId: String,
)

data class DataModel(
    val dataId: String,
)

// auto generate
fun DataModel.copyToDomainModel(
    domainId: String = this.dataId, // dataId is mapped to domainId
)
```

#### CombineTo.Map / CombineFrom.Map

`@CombineTo.Map` and `@CombineFrom.Map` can also be used for property mapping when copying from multiple source classes to a single target class.

**Specifying mapping on source side:**

```kt
@CombineTo(TargetState::class)
data class SourceA(
    @CombineTo.Map("targetProperty")
    val sourceProperty: String,
)

@CombineTo(TargetState::class)
data class SourceB(
    val otherProperty: Int,
)

data class TargetState(
    val targetProperty: String,
    val otherProperty: Int,
)

// auto generate
fun SourceA.copyToTargetState(
    sourceB: SourceB,
    targetProperty: String = this.sourceProperty, // sourceProperty is mapped to targetProperty
    otherProperty: Int = sourceB.otherProperty,
): TargetState = ...
```

**Specifying mapping on target side:**

```kt
data class SourceA(
    val sourceProperty: String,
)

data class SourceB(
    val otherSourceProperty: Int,
)

@CombineFrom(SourceA::class, SourceB::class)
data class TargetState(
    @CombineFrom.Map("sourceProperty")
    val targetProperty: String,
    @CombineFrom.Map("otherSourceProperty")
    val otherProperty: Int,
)

// auto generate
fun SourceA.copyToTargetState(
    sourceB: SourceB,
    targetProperty: String = this.sourceProperty, // sourceProperty is mapped to targetProperty
    otherProperty: Int = sourceB.otherSourceProperty, // otherSourceProperty is mapped to otherProperty
): TargetState = ...
```

### CopyTo.Exclude, CopyFrom.Exclude, CombineTo.Exclude, CombineFrom.Exclude, SealedCopy.Exclude, CopyToChildren.Exclude

Marks a property so the generated copy function **removes its auto-copy default**, making the parameter required.
The parameter itself remains in the function signature; it just loses the `= this.<property>` default and the caller
must supply an explicit value.

| Annotation | Where to place it |
|---|---|
| `@CopyTo.Exclude` | Source class constructor parameter |
| `@CopyFrom.Exclude` | Target class constructor parameter |
| `@CombineTo.Exclude` | Source class property |
| `@CombineFrom.Exclude` | Target class constructor parameter |
| `@SealedCopy.Exclude` | Abstract property on the sealed parent |
| `@CopyToChildren.Exclude` | Property on the sealed parent (applied to all per-child copy functions) |

```kt
sealed interface State {
    val name: String
    val count: Int

    @CopyFrom(State::class)
    data class Success(
        val name: String,
        @CopyFrom.Exclude val count: Int, // no auto-copy default — caller must specify
    )
}

// Generated:
fun State.copyToStateSuccess(
    name: String = this.name,
    count: Int,              // required — no default
): State.Success = State.Success(name = name, count = count)
```

Applying `@Exclude` to a parameter that is not matched to any source property has **no effect** and emits a KSP
warning.

`@SealedCopy.Exclude` only affects the `@SealedCopy`-generated `copy()` function; `@CopyToChildren.Exclude` only
affects the `@CopyToChildren`-generated per-child copy functions. Both annotations can coexist on the same sealed
type without interfering.

`@CopyMapping` and `@CombineMapping` do not support `@Exclude` (the source and target classes are not in your own
code, so annotating their properties is not possible).

<details>
<summary>Generated code examples</summary>

**@SealedCopy.Exclude** — abstract property on sealed parent:

```kt
@SealedCopy
sealed interface MyState {
    val name: String
    @SealedCopy.Exclude val count: Int  // caller must specify count

    data class Loading(override val name: String, override val count: Int) : MyState
}

// Generated:
fun MyState.copy(
    name: String = this.name,
    count: Int,               // required
): MyState = when (this) {
    is MyState.Loading -> this.copy(name = name, count = count)
}
```

**@CopyToChildren.Exclude** — property on sealed parent applies to all per-child functions:

```kt
@CopyToChildren
sealed interface UiState {
    val sessionId: String
    @CopyToChildren.Exclude val count: Int  // required in every copyToCn

    data class Loading(override val sessionId: String, override val count: Int) : UiState
    data class Success(override val sessionId: String, override val count: Int, val data: String) : UiState
}

// Generated:
fun UiState.copyToUiStateLoading(
    sessionId: String = this.sessionId,
    count: Int,   // required
): UiState.Loading = ...

fun UiState.copyToUiStateSuccess(
    sessionId: String = this.sessionId,
    count: Int,   // required
    data: String,
): UiState.Success = ...
```

</details>

### CopyMapping

If you want to generate a copy function between classes where neither the source nor destination is in your own source
code, you can use CopyMapping. This allows you to generate copy functions between library classes without modifying
either class at all.

```kt
// in library X
data class LibXModel(
    val shareProp: String,
    val xProp: Int,
)

// in library Y
data class LibYModel(
    val shareProp: String,
    val yProp: Int,
)

// in your module
@CopyMapping(LibXModel::class, LibYModel::class)
private object Mapping

// auto generate
fun LibXModel.copyToLibYModel(
    shareProp: String = this.shareProp,
    yProp: Int,
): LibYModel = ...
```

The generated copy function is emitted into the same package as the `@CopyMapping`-annotated declaration (the `Mapping` object above), not the source class's package. So even when the source/target are library classes in another package, the function is usable directly from the package of the module where you declared the mapping.

### KDoc

Every source annotation (`@CopyTo`, `@CopyFrom`, `@CopyToChildren`, `@SealedCopy`,
`@CombineTo`, `@CombineFrom`, `@CopyMapping`, `@CombineMapping`) accepts a `kdoc = KDoc(...)`
parameter that lets you augment the KDoc of the generated function with your own
notes and examples.

```kt
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

The generated KDoc renders sections in this order:

1. Auto-generated header (`(Auto generate by @[...] of [...])`)
2. Auto-generated description (`Source -> Target copy function.`)
3. `KDoc.description` (when supplied)
4. Auto-generated `# Example: Basic` / `# Example: Override property values`
5. `KDoc.examples` (each entry, rendered verbatim after `trimIndent`)
6. `@see` references

Each `examples` entry is rendered verbatim — provide your own `# heading` and
` ```kt ... ``` ` fences inside each entry.

### Visibility

By default the generated copy function inherits the visibility of the target (or sealed)
declaration it is derived from. Pass `visibility = CopyVisibility.<...>` to the copy-generating
annotations (`@CopyTo`, `@CopyFrom`, `@CopyToChildren`, `@SealedCopy`, `@CombineTo`,
`@CombineFrom`, `@CopyMapping`, `@CombineMapping`) to force a specific visibility instead. For a
reversible (`canReverse`) `@CopyMapping`, the same visibility is applied to both the forward and
reverse functions.

```kt
@CopyTo(MergedState::class, visibility = CopyVisibility.INTERNAL)
data class ServerState(val shared: String)

// auto generate
internal fun ServerState.copyToMergedState(
    shared: String = this.shared,
    /* ... */
): MergedState = ...
```

`CopyVisibility` has the following values. Generated copy functions are top-level extension
functions, so only modifiers that keep them usable are offered. `private` (visible only inside
the generated file) and `protected` (not valid on top-level declarations) would make the
generated function unusable, so they are intentionally not provided:

| Value | Generated modifier |
|-------|--------------------|
| `INHERIT` (default) | Inherits the target/sealed declaration's visibility (cream's behaviour before this option existed) |
| `PUBLIC` | `public` |
| `INTERNAL` | `internal` |

Omitting `visibility` is fully backward compatible — it keeps the previously generated code unchanged.

To set a default for the whole module instead of annotating every declaration, use the
[`cream.defaultVisibility`](#option-5-creamdefaultvisibility) option. A per-annotation
`visibility` always wins; the project default only applies where the annotation leaves it at
`INHERIT` (unspecified).

### Function Name (funName)

By default cream derives the generated function name from the project-wide naming options
(`cream.copyFunNamePrefix` / `cream.copyFunNamingStrategy` / `cream.escapeDot`). Pass `funName`
to a copy/combine annotation (`@CopyTo`, `@CopyFrom`, `@CopyToChildren`, `@CombineTo`,
`@CombineFrom`, `@CopyMapping`, `@CombineMapping`, `@SealedCopy`) to override the name for that one
declaration, without touching the project options.

`funName` is a **template**: a few `const` tokens expand to pieces of the name, so you can keep
the derived name and only add a prefix/suffix, or build a name from scratch:

```kt
import me.tbsten.cream.*

@CopyTo(UiState.Success::class)                                              // copyToUiStateSuccess (default)
@CopyTo(UiState.Success::class, funName = DefaultCopyFunctionName + "OrNull") // copyToUiStateSuccessOrNull
@CopyTo(UiState.Success::class, funName = "to" + CopyTargetSimpleName)        // toSuccess
@CopyTo(UiState.Success::class, funName = "to_" + copy_target_under_package)  // to_uistate_success
@CopyTo(UiState.Success::class, funName = "toState")                          // toState (plain literal)
data class Source(/* ... */)
```

Because the tokens are `const val`, they can be combined with `+` while staying a compile-time
constant.

| Token | Expands to (target `com.example.UiState.Success`) |
|-------|---------------------------------------------------|
| `DefaultCopyFunctionName` | cream's derived name (`copyToUiStateSuccess`; `copy` for `@SealedCopy`) |
| `CopyTargetSimpleName` / `copy_target_simple_name` | `Success` / `success` |
| `CopyTargetUnderPackage` / `copy_target_under_package` | `UiStateSuccess` / `uistate_success` |
| `CopyTargetInnerName` / `copy_target_inner_name` | `Success` / `success` |
| `CopyTargetFullName` / `copy_target_full_name` | `ComExampleUiStateSuccess` / `com_example_uistate_success` |

The `PascalCase` tokens upper-case each dotted segment; the `snake_case` tokens lower-case each
dotted segment and join them with `_`. Unlike `DefaultCopyFunctionName`, the `CopyTarget*` tokens
render the target name with a fixed strategy, independent of `cream.copyFunNamingStrategy` /
`cream.escapeDot`.

When an annotation generates more than one function — multiple targets/sources, a sealed target,
or a reversible (`canReverse`) `@CopyMapping` — a plain-literal `funName` would name them all the
same and is rejected at build time; include a token so each function gets a distinct name. Omitting
`funName` is fully backward compatible — it keeps the previously generated name unchanged.

A plain-literal `funName` must be a valid Kotlin function name. To use a name that is a Kotlin
keyword (`is`, `in`, `object`, …) or contains spaces, backtick-quote it — otherwise it simply
won't compile (cream does not pre-validate the name; the Kotlin compiler reports it):

```kt
@CopyTo(Target::class, funName = "`is`")   // generates: fun Source.`is`(...)
```

## 💻 4. Usage Example

The primary use cases for cream.kt are outlined below.
Using the [Context7](https://context7.com/) for each use case allows you to instantly apply cream.kt's information to
your generative AI, which should be convenient.

- Improve state transition code in state management using sealed interfaces/classes in ViewModels, etc.
    - [Context7 Documentation](https://context7.com/tbsten/cream?topic=Improve+ViewModel+state+management&tokens=2000)
- Improve data model copying when transforming data models across layers such as Data <-> Domain.
    - [Context7 Documentation](https://context7.com/tbsten/cream?topic=Cross-Layer+Data+Model+Copy&tokens=2000)

(This is just one example of usage and does not limit the scope of cream.kt. If you encounter issues with other use
cases, please create an issue.)

## 🔨 5. Options

Several options are provided to customize the name of the generated copy function.
All options are optional. Set them as needed.

[Option Builder](http://tbsten.github.io/cream/option-builder) is useful for verifying the behavior of each option.

For a detailed example of the copy function name generated when setting each option, see
[@CopyFunctionNameTest.kt](./cream-ksp/src/test/kotlin/me/tbsten/cream/ksp/transform/CopyFunctionNameTest.kt)
See also the test case at .

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

### List of options

| Option name                       | Description                                                                 | Example                                                                  | Default            |
|-----------------------------------|-----------------------------------------------------------------------------|--------------------------------------------------------------------------|--------------------|
| **`cream.copyFunNamePrefix`**     | String prefixed to the generated copy function                              | `copyTo`, `transitionTo`, `to`, `mapTo`                                  | `copyTo`           |
| **`cream.copyFunNamingStrategy`** | Copy function naming conventions.                                           | `under-package`, `diff`, `simple-name`, `full-name`, `inner-name` | `under-package`    |
| **`cream.escapeDot`**             | How to escape `. ` in the name given by `cream.copyFunNamingStrategy`.      | `lower-camel-case`, `replace-to-underscore`                      | `lower-camel-case` |
| **`cream.notCopyToObject`**       | If `true`, @CopyToChildren will not generate a copy function to the object. | `true` , `false`                                                         | `false`            |
| **`cream.defaultVisibility`**     | Module-wide default visibility for generated functions, applied when an annotation's `visibility` is `INHERIT`. | `INHERIT`, `PUBLIC`, `INTERNAL` | `INHERIT`          |

### Option 1. `cream.copyFunNamePrefix`

| Default  | Possible values  |
|----------|------------------|
| `copyTo` | Arbitrary string |

Set the class name to be prefixed by the generated copy function name.
Set a straightforward string that describes the copy or state transition, such as `copyTo` or `to`.

### Option 2. `cream.copyFunNamingStrategy`

| Default         | `under-package`                                                                 |
|-----------------|---------------------------------------------------------------------------------|
| Possible values | One of `under-package`, `diff`, `simple-name`, `full-name`, `inner-name` |

How to set the class name string after the prefix of the copy function. The following table shows
the supported configuration methods.
If you want a naming scheme other than these, please make a request
to [issue](https://github.com/TBSten/cream/issues?q=sort%3Aupdated-desc+is%3Aissue+is%3Aopen).

| Value           | Description                                                                                                         | Example of generating a copy function that transitions from `com.example.Aaa.Bbb` -> `com.example.Aaa.Bbb.Ccc.Ddd`. |
|-----------------|---------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------|
| `under-package` | Use names that reflect the package hierarchy.                                                                       | Hoge.Fuga.copyTo **`Aaa.Bbb.Ccc.Ddd`** (...)                                                                        |
| `diff`          | Use a name that includes only the difference from the transition source class.                                      | Hoge.Fuga.copyTo **`CccDdd`** (...)                                                                                 |
| `simple-name`   | Transition destination class::class.simpleName.                                                                     | Hoge.Fuga.copyTo **`Ddd`** (...)                                                                                    |
| `full-name`     | Target class::class.qualifiedName.                                                                                  | Hoge.Fuga.copyTo **`ComExampleAaaBbbCccDdd`** (...)                                                                 |
| `inner-name`    | Use the class name from the second level of the nested class onward. (Same as `simple-name` for non-nested classes) | Hoge.Fuga.copyTo **`BbbCccDdd`** (...)                                                                              |

<img src="./doc/cream.copyFunNamingStrategy.png" width="800" />

### Option 3. `cream.escapeDot`

| Default            | Possible values                                            |
|--------------------|------------------------------------------------------------|
| `lower-camel-case` | One of `lower-camel-case`, `replace-to-underscore` |

Sets the method for escaping class names retrieved with `cream.copyFunNamingStrategy`.

Kotlin function names usually cannot contain `. ` in function names, they must be changed to a
string that can be named in one of the ways shown in the configuration examples.

| Value                   | Description                                                                        | Example of generating a copy function that transitions from `com.example.Hoge.Fuga` -> `com.example.Hoge.Piyo`. |
|-------------------------|------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| `lower-camel-case`      | Concatenate each dotted element with camelCase, beginning with a lowercase letter. | Hoge.Fuga.copyTohogePiyo(...)                                                                                   |
| `replace-to-underscore` | Replace dots with underscores                                                      | Hoge.Fuga.copyTo_hoge_piyo(...)                                                                                 |

### Option 4. `cream.notCopyToObject`

| Default | Possible values |
|---------|-----------------|
| `false` | `true`, `false` |

If set to `true`, copy functions from a class to an `object` will not be generated.

Copy functions to an `object` do not actually copy, but simply return the instance of the object
itself. If you prefer not to generate copy functions to data objects, set this option to `true` to
suppress them.

This option affects the entire module, but you can also limit it to a specific class by setting the
`notCopyToObject` property of the `@CopyToChildren` annotation to `true`.

### Option 5. `cream.defaultVisibility`

| Default   | Possible values                       |
|-----------|---------------------------------------|
| `INHERIT` | One of `INHERIT`, `PUBLIC`, `INTERNAL` |

Sets the module-wide default visibility for every generated copy / combine function. This is the
project-level counterpart of the per-annotation `visibility = CopyVisibility.<...>` argument
(see [Visibility](#visibility)).

Precedence is:

1. An explicit annotation `visibility` (anything other than `INHERIT`) — always wins.
2. Otherwise `cream.defaultVisibility`, when it is `PUBLIC` or `INTERNAL`.
3. Otherwise (both are `INHERIT`) the generated function inherits the target/sealed declaration's
   own visibility, exactly as before this option existed.

For example, with `cream.defaultVisibility=INTERNAL`, a plain `@CopyTo(Target::class)` generates an
`internal` copy function without having to add `visibility = CopyVisibility.INTERNAL` to each
annotation.

## 🆚 6. Comparison with Other Libraries

When choosing a data mapping library for Kotlin, you might consider several alternatives. Here's how cream.kt compares to other popular mapping libraries:

### vs. MapStruct

**MapStruct** is a mature Java-based code generation library for mapping between different object types.

| Feature | cream.kt | MapStruct |
|---------|----------|-----------|
| **Language** | Kotlin-first with KSP | Java-first with annotation processing |
| **State Transitions** | ✅ Designed for sealed class state transitions | ❌ Focused on entity-DTO mapping |
| **Default Value Override** | ✅ Generated functions allow overriding defaults | ⚠️ Limited default value handling |
| **Multiplatform** | ✅ Kotlin Multiplatform support | ❌ JVM only |
| **IDE Support** | ✅ Native Kotlin IDE integration | ⚠️ Better for Java projects |
| **Use Case** | Frontend state management (e.g., UI states) | Backend entity-DTO conversions |

**When to choose cream.kt over MapStruct:**
- You're working with Kotlin (especially Kotlin Multiplatform)
- You need to manage UI state transitions with sealed classes
- You want to override default values during state transitions
- You prefer a lightweight, Kotlin-native solution

### vs. KOMM (Kotlin Object Multiplatform Mapper)

**KOMM** is a lightweight Kotlin Multiplatform mapping library that also uses KSP for code generation.

| Feature | cream.kt | KOMM |
|---------|----------|------|
| **Structural Mismatch Handling** | ✅ Better handling when source/target structures differ | ⚠️ Requires more manual configuration |
| **Default Value Override** | ✅ All matched properties get defaults; can be overridden | ⚠️ More limited default handling |
| **Advanced Features** | ✅ `@CopyToChildren`, `@CombineTo`, `@CopyMapping` | ⚠️ Simpler feature set |
| **Object Singleton Copy** | ✅ Copy to `object` types (with opt-out) | ❌ Not supported |
| **Complexity** | ⚠️ More features = steeper learning curve | ✅ Simpler, more lightweight |
| **Flexibility** | ⚠️ Opinionated for state management patterns | ✅ More general-purpose flexibility |

**When to choose cream.kt over KOMM:**
- You're building applications with complex state management (e.g., using libraries like [Tart](https://github.com/TBSten/tart))
- You need to copy from a sealed interface to all its children (`@CopyToChildren`)
- You frequently work with sealed class hierarchies for UI states
- You need to combine multiple source classes into one target (`@CombineTo`)
- You want library-to-library mapping without modifying source code (`@CopyMapping`)

**When KOMM might be better:**
- You want a simpler, more general-purpose mapping library
- You prefer maximum flexibility over opinionated patterns
- You have simpler mapping needs without complex state hierarchies
