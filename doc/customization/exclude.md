[← README](../../README.md) | [日本語](./exclude.ja.md)

# Excluding auto-copy defaults (.Exclude)

Marks a property so the generated copy function **removes its auto-copy default**, making the parameter required.
The parameter itself remains in the function signature; it just loses the `= this.<property>` default and the caller
must supply an explicit value. For `@CopyMapping` / `@CombineMapping` — where the mapped classes cannot be
annotated — the same effect is available via the [`excludes` annotation argument](#copymapping--combinemapping--excludes).

## Where to place it

| Annotation | Where to place it |
|---|---|
| `@CopyTo.Exclude` | Source class constructor parameter |
| `@CopyFrom.Exclude` | Target class constructor parameter |
| `@CombineTo.Exclude` | Source class property |
| `@CombineFrom.Exclude` | Target class constructor parameter |
| `@SealedCopy.Exclude` | Abstract property on the sealed parent |
| `@CopyToChildren.Exclude` | Property on the sealed parent (applied to all per-child copy functions) |
| `@CopyMapping(excludes = [...])` | `excludes` argument of the annotation (target-side property names) |
| `@CombineMapping(excludes = [...])` | `excludes` argument of the annotation (target-side property names) |

## CopyTo.Exclude

```kt
import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
data class Source(
    val name: String,
    @CopyTo.Exclude val count: Int, // no auto-copy default — caller must specify
)

data class Target(
    val name: String,
    val count: Int,
)

// Generated:
fun Source.copyToTarget(
    name: String = this.name,
    count: Int,              // required — no default
): Target = Target(name = name, count = count)

// usage
val source: Source = /* ... */
val target: Target = source.copyToTarget(
    count = 1, // required — must be passed explicitly
)
```

## CopyFrom.Exclude

```kt
import me.tbsten.cream.CopyFrom

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

// usage
val state: State = /* ... */
val success: State.Success = state.copyToStateSuccess(
    count = 1, // required — must be passed explicitly
)
```

## CombineTo.Exclude

Annotating a property on just one source class makes it required in **every generated combine
function** — even if another source has a property with the same name, cream does not fall back
to that source's default.

```kt
import me.tbsten.cream.CombineTo

@CombineTo(Target::class)
data class SourceA(
    @CombineTo.Exclude val shared: String, // no auto-copy default — caller must specify
    val uniqueA: Int,
)

@CombineTo(Target::class)
data class SourceB(
    val shared: String,
    val uniqueB: Boolean,
)

data class Target(
    val shared: String,
    val uniqueA: Int,
    val uniqueB: Boolean,
)

// Generated (shared is also required in the SourceB-receiver function):
fun SourceA.copyToTarget(
    sourceB: SourceB,
    shared: String,                     // required — no default
    uniqueA: Int = this.uniqueA,
    uniqueB: Boolean = sourceB.uniqueB,
): Target = Target(shared = shared, uniqueA = uniqueA, uniqueB = uniqueB)

// usage
val target: Target = sourceA.copyToTarget(
    sourceB = sourceB,
    shared = "value", // required — must be passed explicitly
)
```

## CombineFrom.Exclude

```kt
import me.tbsten.cream.CombineFrom

data class SourceA(
    val name: String,
    val count: Int,
)

data class SourceB(
    val extra: Int,
)

@CombineFrom(SourceA::class, SourceB::class)
data class Target(
    val name: String,
    @CombineFrom.Exclude val count: Int, // no auto-copy default — caller must specify
    val extra: Int,
)

// Generated:
fun SourceA.copyToTarget(
    sourceB: SourceB,
    name: String = this.name,
    count: Int,              // required — no default
    extra: Int = sourceB.extra,
): Target = Target(name = name, count = count, extra = extra)

// usage
val target: Target = sourceA.copyToTarget(
    sourceB = sourceB,
    count = 1, // required — must be passed explicitly
)
```

## SealedCopy.Exclude

Annotating an abstract property on the sealed parent makes it a required parameter of the
`@SealedCopy`-generated `copy()`.

```kt
import me.tbsten.cream.SealedCopy

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

// usage
val state: MyState = MyState.Loading("a", 1)
val updated: MyState = state.copy(count = 2) // required — must be passed explicitly
```

## CopyToChildren.Exclude

Annotating a property on the sealed parent makes it a required parameter of **every**
`@CopyToChildren`-generated per-child copy function.

```kt
import me.tbsten.cream.CopyToChildren

@CopyToChildren
sealed interface UiState {
    val sessionId: String
    @CopyToChildren.Exclude val count: Int  // required in every per-child copy function

    data class Loading(override val sessionId: String, override val count: Int) : UiState
    data class Success(override val sessionId: String, override val count: Int, val data: String) : UiState
}

// Generated:
fun UiState.copyToUiStateLoading(
    sessionId: String = this.sessionId,
    count: Int,   // required
): UiState.Loading = /* ... */

fun UiState.copyToUiStateSuccess(
    sessionId: String = this.sessionId,
    count: Int,   // required
    data: String,
): UiState.Success = /* ... */

// usage
val state: UiState = /* ... */
val loading: UiState.Loading = state.copyToUiStateLoading(
    count = 0, // required — must be passed explicitly
)
```

## CopyMapping / CombineMapping — `excludes`

`@CopyMapping` / `@CombineMapping` map classes you don't own, so there is no property to annotate.
Instead, list the parameters to make required in the `excludes` annotation argument. Each entry
names a generated parameter — i.e. a **target-side** property name, consistent with the generated
signature (including `properties = [Map(...)]` renames).

```kt
import me.tbsten.cream.CopyMapping

// in library X / Y — cannot be modified
data class LibXModel(val shareProp: String, val xProp: Int)
data class LibYModel(val shareProp: String, val yProp: Int)

@CopyMapping(
    source = LibXModel::class,
    target = LibYModel::class,
    excludes = ["shareProp"],
)
private object Mapping

// Generated:
fun LibXModel.copyToLibYModel(
    shareProp: String,       // required — no default
    yProp: Int,
): LibYModel = ...
```

`@CombineMapping(excludes = [...])` works the same way: entries name target-side parameters of
the generated combine function.

### With `canReverse = true`

`excludes` applies in **both** directions. Entries are translated through the reversed
`properties` mappings: an entry that names the `target` of a `Map(source = ..., target = ...)`
excludes the **source-side** parameter in the reverse function; entries without a mapping
(same-named shared properties) apply as-is.

```kt
@CopyMapping(
    source = LibXModel::class,
    target = LibYModel::class,
    canReverse = true,
    properties = [CopyMapping.Map(source = "xProp", target = "yProp")],
    excludes = ["yProp"],
)
private object Mapping

// Forward — yProp is required:
fun LibXModel.copyToLibYModel(
    shareProp: String = this.shareProp,
    yProp: Int,              // required — no default
): LibYModel = ...

// Reverse — the exclude is translated through the reversed Map, so xProp is required:
fun LibYModel.copyToLibXModel(
    shareProp: String = this.shareProp,
    xProp: Int,              // required — no default
): LibXModel = ...
```

## Details / Edge cases

Applying `@Exclude` to a parameter that is not matched to any source property has **no effect** and emits a KSP
warning. Likewise, an `excludes` entry that matches no auto-defaulted parameter has no effect and emits a KSP
warning.

`@SealedCopy.Exclude` only affects the `@SealedCopy`-generated `copy()` function; `@CopyToChildren.Exclude` only
affects the `@CopyToChildren`-generated per-child copy functions. Both annotations can coexist on the same sealed
type without interfering.

`@CopyMapping` and `@CombineMapping` have no `.Exclude` annotation (the source and target classes are not in your
own code, so annotating their properties is not possible) — use the `excludes` annotation argument instead.

## See also

- [Property mapping (.Map)](./property-mapping.md) — map a parameter to a differently-named property instead of removing its default
- [Copy — @CopyTo / @CopyFrom / @CopyMapping](../copy.md)
- [Combine — @CombineTo / @CombineFrom / @CombineMapping](../combine.md)
- [Sealed copy — @SealedCopy](../sealed-copy.md)
- [Copy to children — @CopyToChildren](../copy-to-children.md)
