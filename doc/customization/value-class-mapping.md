[← README](../../README.md) | [日本語](./value-class-mapping.ja.md)

# Automatic value class mapping

cream automatically maps a name-matched property whose type differs from the target constructor
parameter **only by `value class` wrappers** ([issue #21](https://github.com/TBSten/cream/issues/21)).
This is **enabled by default** for every copy / combine annotation — `@CopyTo`, `@CopyFrom`,
`@CopyToChildren`, `@CombineTo`, `@CombineFrom`, `@CopyMapping` (including `canReverse` reverse
functions), and `@CombineMapping` — with no per-annotation argument:

- **Wrap** — the target parameter type is a value class `V(val u: U)` and the source property is
  a `U` → the generated default is `V(this.<property>)`
- **Unwrap** — the source property is a value class `V(val u: U)` and the target parameter type
  is a `U` → the generated default is `this.<property>.u`

A type-compatible name match always wins; the conversion is only consulted when normal property
matching finds nothing, so plain matching behavior is unchanged. Without a conversion, such a
parameter simply stays required — exactly as it was before this feature.

`@SealedCopy` is not affected: its generated `copy()` parameters are the sealed parent's own
abstract properties (the same types on both sides), so there is nothing to wrap or unwrap.

## Wrap: plain type → value class

```kt
import me.tbsten.cream.CopyTo

@JvmInline
value class DomainId(val value: String)

@CopyTo(DomainModel::class)
data class DataModel(
    val id: String,
    val name: String,
)

data class DomainModel(
    val id: DomainId,
    val name: String,
)

// usage
val dataModel = DataModel(id = "id-1", name = "cream")
val domainModel: DomainModel = dataModel.copyToDomainModel()
// domainModel.id == DomainId("id-1")
```

<details>
<summary>Generated code</summary>

```kt
// auto generate
fun DataModel.copyToDomainModel(
    id: DomainId = DomainId(this.id), // auto-wrapped
    name: String = this.name,
): DomainModel = ...
```

</details>

## Unwrap: value class → plain type

The opposite direction works the same way (shown here with `@CopyFrom`):

```kt
import me.tbsten.cream.CopyFrom

@JvmInline
value class DomainId(val value: String)

data class DomainModel(
    val id: DomainId,
    val name: String,
)

@CopyFrom(DomainModel::class)
data class DataModel(
    val id: String,
    val name: String,
)

// usage
val domainModel = DomainModel(id = DomainId("id-1"), name = "cream")
val dataModel: DataModel = domainModel.copyToDataModel()
// dataModel.id == "id-1"
```

<details>
<summary>Generated code</summary>

```kt
// auto generate
fun DomainModel.copyToDataModel(
    id: String = this.id.value, // auto-unwrapped
    name: String = this.name,
): DataModel = ...
```

</details>

In combine functions (`@CombineTo` / `@CombineFrom` / `@CombineMapping`) the conversion picks the
right source qualifier, e.g. `id: DomainId = DomainId(sourceB.id)` when the matching property
comes from a secondary source.

## Nullability

The conversion only generates defaults that are **sound** — a default that could turn `null` into
a non-null value class (or vice versa) is never generated; the parameter stays required instead,
and cream emits a [warning](#warnings) since that is plausibly not what you meant.

For a value class `V(val u: U)` (non-null underlying):

| Source property | Target parameter | Generated default |
|---|---|---|
| `U` | `V` | `V(this.x)` |
| `U` | `V?` | `V(this.x)` |
| `U?` | `V?` | `this.x?.let { V(it) }` |
| `U?` | `V` | none — required parameter + warning |
| `V` | `U` | `this.x.u` |
| `V` | `U?` | `this.x.u` |
| `V?` | `U?` | `this.x?.u` |
| `V?` | `U` | none — required parameter + warning |

For a **nullable underlying** `V(val u: U?)`, wrapping accepts both `U` and `U?` sources
(`V(this.x)` — the underlying itself is nullable), while unwrapping always yields a nullable
value, so the target parameter must be nullable:

| Source property | Target parameter | Generated default |
|---|---|---|
| `U` or `U?` | `V` or `V?` | `V(this.x)` |
| `V` | `U?` | `this.x.u` |
| `V` | `U` | none — required parameter + warning |

## Chained value classes

Value classes wrapping value classes convert through **every layer** (up to 8 — far beyond
real-world modeling; the cap only guards against pathological hierarchies):

```kt
@JvmInline value class RawId(val value: String)
@JvmInline value class UserId(val rawId: RawId)

// Source(id: String) -> Target(id: UserId)
id: UserId = UserId(RawId(this.id))       // chained wrap

// Source(id: UserId) -> Target(id: String)
id: String = this.id.rawId.value          // chained unwrap
```

The nullability rules above apply to the chain as a whole (e.g. `String?` → `UserId?` generates
`this.id?.let { UserId(RawId(it)) }`; a nullable hop in an unwrap chain switches to `?.` and
makes the rest of the expression nullable). A conversion either only wraps or only unwraps —
unwrapping one value class and re-wrapping the result into a *different* value class is never
done (see [Limitations](#limitations)).

## Typealiases

Typealiases are resolved on both sides. When the parameter type is an alias of a value class, the
generated constructor call uses the **resolved class** (an alias of a nullable type would not be
constructible):

```kt
@JvmInline value class DomainId(val value: String)
typealias DomainIdAlias = DomainId

// Source(id: String) -> Target(id: DomainIdAlias)
id: DomainIdAlias = DomainId(this.id)
```

## Interaction with `.Map`

The conversion resolves the source property with the **same name-resolution rules as normal
matching**, so a property renamed with `.Map` (or with a mapping annotation's
`properties = [Map(...)]` entry) also wraps / unwraps when its type differs by a value class:

```kt
@CopyTo(DomainModel::class)
data class DataModel(
    @CopyTo.Map("id") val rawId: String,
)

data class DomainModel(val id: DomainId)

// generated
id: DomainId = DomainId(this.rawId)
```

## Interaction with `.Exclude`

**`.Exclude` wins.** A parameter whose default would come from a value-class conversion can be
excluded exactly like a normally matched one: the conversion default is suppressed and the
parameter stays required. Since the exclude *is* effective there, the
"`@Exclude ... has no effect`" warning does not fire — and an explicit `.Exclude` also silences
the near-miss warnings below for that property.

## Warnings

When a conversion **almost** applies but is skipped for a reason you plausibly did not intend,
cream emits a positioned KSP warning (`Automatic value class mapping for '<param>' skipped: ...`)
and leaves the parameter required:

- the source property is nullable but the target parameter type is non-null (wrap direction),
- the unwrapped value is nullable but the target parameter type is non-null (unwrap direction),
- the value class's primary constructor is not accessible from generated code (wrap direction),
- the underlying property is not accessible from generated code (unwrap direction).

A plain type mismatch (e.g. `id: Int` vs `DomainId(val value: String)`) is NOT warned about — the
parameter stays required silently, as it always did.

## When both directions apply

Both a wrap and an unwrap can apply only when the source property is a value class whose
underlying type is the target's value class (`Holder(val wrapper: Wrapper)` into `Wrapper`) and
the target's own underlying is a supertype of the source (e.g. `Wrapper(val raw: Any)`).
**Unwrap wins, deterministically**: it extracts the exact `Wrapper` already inside `Holder`
(`this.x.wrapper`), whereas the wrap would box the whole `Holder` into `Any`.

## Opting out: `cream.autoValueClassMapping`

There is no per-annotation switch, but the module-wide KSP option
[`cream.autoValueClassMapping`](./options.md) (default `true`) disables the conversion entirely
(including its warnings). Affected parameters then stay required, as they were before this
feature:

```kts
// module/build.gradle.kts
ksp {
    arg("cream.autoValueClassMapping", "false")
}
```

Like `cream.notCopyToObject`, the option is parsed leniently: only the literal `"false"`
(case-insensitive) disables it; any other value keeps the default.

## Limitations

Each of these leaves the parameter **required** (the safe, pre-feature behavior) — no broken code
is ever generated:

- **Generic value classes** (`V<T>(val t: T)`) are never converted. They are an experimental
  Kotlin language feature (`-XXLanguage:+GenericInlineClasses`); cream skips them entirely.
- **`vararg` parameters** are never converted — the property counterpart is an *array* of the
  element type, and element-wise wrapping/unwrapping is out of scope.
- **No mixed conversions.** A conversion either only wraps or only unwraps; unwrapping a value
  class and re-wrapping the result into a different value class
  (`SourceId(val value: String)` → `TargetId(val value: String)`) is never done. Map such
  properties explicitly.
- **Visibility**: wrapping needs a callable primary constructor and unwrapping a readable
  underlying property — `public` always works, `internal` only for value classes declared in the
  same module (a classpath value class from `@CopyMapping` / `@CombineMapping` sources cannot use
  `internal`), `private`/`protected` never do. Near misses warn (see [Warnings](#warnings)).
- **Detection is modifier-based** (`value class`), so multiplatform `value class` /
  `expect value class` declarations qualify the same as `@JvmInline` ones — an `expect` value
  class converts as long as the expect declaration itself has a visible single-parameter primary
  constructor.
- **Typealias of a generic value class** is skipped along with generic value classes.

## See also

- [KSP Options](./options.md) — `cream.autoValueClassMapping` (module-wide opt-out)
- [Property mapping (.Map)](./property-mapping.md) — map a parameter to a differently-named property
- [Excluding auto-copy defaults (.Exclude)](./exclude.md) — remove a parameter's auto-copy default
- [Copy — @CopyTo / @CopyFrom / @CopyMapping](../copy.md)
- [Combine — @CombineTo / @CombineFrom / @CombineMapping](../combine.md)
