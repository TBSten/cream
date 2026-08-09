# cream.kt

![Maven Central Version](https://img.shields.io/maven-central/v/me.tbsten.cream/cream-runtime)
![GitHub License](https://img.shields.io/github/license/TBSten/cream)
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/TBSten/cream)

English |
[日本語](./README.ja.md) |
[DeepWiki](https://deepwiki.com/TBSten/cream)

**Contents:**
[Why cream.kt?](#why-creamkt) ·
[Setup](#setup) ·
[Quick Start](#quick-start) ·
[Annotations](#annotations) ·
[Customization](#customization) ·
[Use Cases](#use-cases)

---

cream.kt is a KSP plugin that enables **declarative data copy** and makes it easy to **copy across classes**.
Annotate a class, and cream automatically generates a copy function to another, similar class —
properties with matching names are carried over for you.

```kt
// Without cream.kt
// ❌ Hard to see which data was actually added or changed
MyUiState.Success(
    userName = prevState.userName,    // manual copy
    password = prevState.password,    // manual copy
    data = newData,
)

// With cream.kt — copyToMyUiStateSuccess is generated automatically
// ✅ Only the data that changed stands out
prevState.copyToMyUiStateSuccess(data = newData)
```

Function names are customizable (e.g. shorten it to `toSuccess`) — see [Function name](doc/customization/fun-name.md).

## Why cream.kt?

- **Declarative data copy** — one annotation generates the copy function; properties with matching
  names become default arguments, so you only pass what changed.
- **State transitions across classes** — like data class `copy()`, but across classes
  (e.g. `Loading` → `Success`). Designed for sealed class/interface state management.
- **Kotlin Multiplatform ready** — runtime annotations are published for all Kotlin platforms.

See also the [comparison with other mapping libraries](doc/comparison.md) (MapStruct, KOMM).

## Setup

|                   |                                                                          |
|-------------------|--------------------------------------------------------------------------|
| `<cream-version>` | ![GitHub Release](https://img.shields.io/github/v/release/TBSten/cream) |
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

Kotlin Multiplatform (commonMain) requires additional setup due to a KSP limitation
→ [Kotlin Multiplatform support](doc/customization/multiplatform.md)

## Quick Start

Annotate the source class with `@CopyTo`, and cream generates a copy function to the target class:

```kt
import me.tbsten.cream.CopyTo

@CopyTo(UiState.Success::class)
class UiState {
    data class Success(
        val data: String,
    )
}

// Auto-generated
fun UiState.copyToUiStateSuccess(
    data: String,
): UiState.Success = /* ... */

// Usage
val uiState: UiState = /* ... */
val nextUiState: UiState.Success = uiState.copyToUiStateSuccess(
    data = /* ... */,
)
```

Constructor parameters that match property names of the source class get default values, so you
only pass what changed. Details: [Copy](doc/copy.md)

## Use Cases

### UI state transitions

In GUI apps (such as Android apps) where you need to manage screen state, modeling the state as a
sealed interface is convenient — but the constructor calls at each state transition tend to become
hard to read.

With cream.kt you can keep the state transitions simple while sticking with sealed interfaces.

```kt
sealed interface HomeState {
    @CopyTo(Success::class, Error::class)
    data object Loading : HomeState

    data class Success(
        val data: HomeScreenData,
    ) : HomeState

    data class Error(
        val message: String,
    ) : HomeState
}

class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)

    fun initialLoad() = viewModelScope.launch {
        val loadingState = HomeState.Loading
        _state.update { loadingState }

        runCatching {
            fetchHomeScreenDataFromServer()
        }.fold(
            onSuccess = { _state.update { loadingState.copyToHomeStateSuccess(data = it) } },
            onFailure = { _state.update { loadingState.copyToHomeStateError(message = it.message ?: "Unknown error") } },
        )
    }
}
```

See [UI state management with sealed classes](doc/use-case/ui-state-management-by-sealed-class/README.md) for details.

### Layers

Defining separate models for the data layer and the domain layer keeps data-layer changes from
affecting the rest of the app (such as the UI layer).

In small-to-mid-sized apps, however, this mapping often produces tedious boilerplate. With
cream.kt you can replace the hand-written mapping code with generated functions:

```kt
// domain layer
data class Item(
    val itemId: String,
    val name: String,
    val price: Int,
)

// data layer
@CopyTo(Item::class)
data class GetItemApiResponse(
    val itemId: String,
    val name: String,
    val price: Int,
)

class ItemRepositoryImpl : ItemRepository {
    override suspend fun getItem(itemId: String): Item {
        val apiResponse = itemApi.getItem(itemId)
        return apiResponse.copyToItem()
    }
}
```

See [cross-layer model mapping](doc/use-case/model-mapping.md) for details.

## Annotations

See the docs below for the details of each feature.

| Annotation | Put it on | Generates | Docs |
|---|---|---|---|
| `@CopyTo(Target::class)` | Source class | Copy function from source to target | [docs](doc/copy.md#copyto) |
| `@CopyFrom(Source::class)` | Target class | Same as `@CopyTo`, annotation placed on the target side | [docs](doc/copy.md#copyfrom) |
| `@CopyMapping(Source::class, Target::class)` | A declaration in your module | Copy function between two classes you cannot modify (e.g. library classes) | [docs](doc/copy.md#copymapping) |
| `@CopyToChildren` | Sealed class/interface | Copy functions from the sealed parent to **all** concrete leaves | [docs](doc/copy-to-children.md) |
| `@SealedCopy` | Sealed class/interface | `copy()` on the sealed parent that preserves the subtype | [docs](doc/sealed-copy.md) |
| `@CombineTo(Target::class)` | Each source class | Combine function from **multiple** sources to one target | [docs](doc/combine.md#combineto) |
| `@CombineFrom(SourceA::class, SourceB::class, ...)` | Target class | Same as `@CombineTo`, annotation placed on the target side | [docs](doc/combine.md#combinefrom) |
| `@CombineMapping(...)` | A declaration in your module | Combine function between classes you cannot modify | [docs](doc/combine.md#combinemapping) |
| `@CallFrom(Args::class)` | Function (top-level / member / extension) | Bridge overload that calls the function from an argument-holder class | [docs](doc/call-from.md) |

## Customization

When you need finer-grained customization, see the following.

| I want to... | API | Docs |
|---|---|---|
| Map properties whose names differ | `.Map` (e.g. `@CopyTo.Map`) | [Property mapping](doc/customization/property-mapping.md) |
| Drop the auto-copy default and make callers pass a value | `.Exclude` (e.g. `@CopyTo.Exclude`), `excludes` for mapping annotations | [Exclude](doc/customization/exclude.md) |
| Add my own notes/examples to the generated KDoc | `kdoc = KDoc(...)` | [KDoc](doc/customization/kdoc.md) |
| Control the visibility of generated functions | `visibility` / `CopyVisibility` | [Visibility](doc/customization/visibility.md) |
| Rename generated functions (per-declaration / module-wide) | `funName` / `cream.copyFunNamePrefix` / … | [Function name](doc/customization/fun-name.md) |
| See all module-wide KSP options | `cream.*` KSP options | [KSP Options](doc/customization/options.md) |

