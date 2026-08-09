[← README](../README.md) | [日本語](./parent-optional.ja.md)

# @ParentOptional

`@ParentOptional` exposes a property of a sealed child class on the sealed parent type as a
**nullable extension property**. The generated accessor returns the property's value when the
receiver is the annotated child, or `null` otherwise — replacing the
`(state as? Success)?.data` boilerplate that piles up around UI state (MVI / UiState) code.

```mermaid
flowchart LR
    Prop["Property of a sealed child class"] --> Fn["Auto-generates a nullable accessor on the sealed parent<br>val Parent.prop: T?"]
    Fn --> Parent["Sealed parent type"]
    Prop ~~~ ParentOptional["@ParentOptional"]
    ParentOptional -. "attach" .-> Prop
    linkStyle 0,1 stroke:#059669,stroke-width:2px
    classDef annotated stroke:#7c3aed,stroke-width:2px
    classDef generated stroke:#059669,stroke-width:2px,font-weight:bold
    class ParentOptional annotated
    class Fn generated
```

## Quick example

```kt
import me.tbsten.cream.ParentOptional

sealed interface MyState {
    data class Success(
        @ParentOptional val data: String,
        @ParentOptional val message: String,
    ) : MyState

    data class Failure(
        @ParentOptional val message: String,
        @ParentOptional(propertyName = "errorCode") val code: Int,
    ) : MyState

    data object Loading : MyState
}

// usage
val state: MyState = MyState.Success(data = "d", message = "hello")
state.data      // "d" — null when state is Failure or Loading
state.message   // "hello" — Success.message and Failure.message merge into ONE accessor
state.errorCode // null — renamed from `code` via propertyName; the Int value when state is Failure
```

```mermaid
flowchart LR
    Success["MyState.Success<br>(data, message)"] --> Fn1["val MyState.data: String?"]
    Success --> Fn2["val MyState.message: String?"]
    Failure["MyState.Failure<br>(message, code)"] --> Fn2
    Failure --> Fn3["val MyState.errorCode: Int?"]
    ParentOptional["@ParentOptional"] -. "attach (per property)" .-> Success
    ParentOptional -. "attach (per property)" .-> Failure
    linkStyle 0,1,2,3 stroke:#059669,stroke-width:2px
    classDef annotated stroke:#7c3aed,stroke-width:2px
    classDef generated stroke:#059669,stroke-width:2px,font-weight:bold
    class ParentOptional annotated
    class Fn1,Fn2,Fn3 generated
```

<details>
<summary>Generated code</summary>

```kt
// auto generate
public val MyState.data: String?
    get() = when (this) {
        is MyState.Success -> data
        else -> null
    }

public val MyState.message: String?
    get() = when (this) {
        is MyState.Success -> message
        is MyState.Failure -> message
        else -> null
    }

public val MyState.errorCode: Int?
    get() = when (this) {
        is MyState.Failure -> code
        else -> null
    }
```

</details>

## Details

### Same-name properties of multiple children are merged into one accessor

When properties of multiple children resolve to the same generated name on the same sealed
parent, they are merged into a **single** accessor with one `is` branch per child (see
`MyState.message` in the [Quick example](#quick-example)). All merged properties must have the
same type — a type mismatch is an error. Renaming one side with
`@ParentOptional(propertyName = ...)` avoids the merge.

When the merged children are themselves in a **subtype relation** (e.g. a sealed intermediate
class contributes its own property and so does a leaf below it), the `is` branches are ordered
**most-derived-first**, so the most derived child's property wins for its instances — the
broader supertype branch can never shadow it.

### Accessors are generated for every sealed ancestor

Accessors are generated for **all transitive sealed supertypes** of the child — intermediate
sealed types included, each in its own generated file. The call site picks the accessor
matching the static type of the receiver.

```kt
sealed interface Shape {
    sealed interface Polygon : Shape {
        data class Rect(
            @ParentOptional val corners: Int,
        ) : Polygon
    }

    data object Circle : Shape
}

// auto generate — both the root and the intermediate sealed type get an accessor:
// val Shape.corners: Int?           (in ParentOptional__Shape.kt)
// val Shape.Polygon.corners: Int?   (in ParentOptional__Shape.Polygon.kt)
```

### Generic parents

A generic sealed parent is supported when the child **pins the parent's type parameter
directly** in its supertype list (`Child<T> : Parent<T>` style):

```kt
sealed interface Source<T> {
    data class Filled<E>(
        @ParentOptional val item: E,
    ) : Source<E>
}

// auto generate
public val <T : Any?> Source<T>.item: T?
    get() = when (this) {
        is Source.Filled -> item
        else -> null
    }
```

Carrying a type parameter to an ancestor **across an intermediate sealed type**
(`Leaf<X> : Middle<X>`, `Middle<E> : Root<E>` — then referring to `Root`) is not supported:
the directly pinned parent (`Middle`) still gets its accessor, but the chained ancestor
(`Root`) is reported as an error.

Type parameters with multiple upper bounds are preserved via a `where` clause on the
generated extension property.

### Nullable property types

A nullable property (`val data: String?`) is allowed and keeps its type as-is — the `?` is
never doubled. The accessor's `null` is then **ambiguous**: it can mean "the receiver is not
such a child" or the property's own `null` value. The generated accessor's KDoc carries a note
about this; use an `is` check when the distinction matters.

### Property shapes

Anything readable as a plain property on the child instance is supported and pinned by tests:

- Constructor `val`/`var` and **body-declared** properties (custom getters included).
- **Delegated** properties (`by lazy { ... }`).
- **`lateinit var`** — like a direct read, the accessor throws if it is read before
  initialization.
- Properties declared by an **`object` / `data object`** child.
- **Hard-keyword names** (`val \`object\``) — escaped with backticks in the generated code.
  (An illegal `propertyName` argument is not validated by cream, mirroring `funName`: it
  simply fails to compile in the generated file.)
- **Typealiased types** are preserved as the alias in the generated signature (not expanded).
  Note that an aliased and a non-aliased spelling of the same type therefore do **not** merge —
  that is a type-mismatch error.
- **Extension properties** are not supported (see [Errors](#errors)).

### @Deprecated propagation

When a merged child class or property is `@Deprecated`, the annotation (message + level) is
propagated onto the generated accessor, so callers keep seeing the deprecation — and a
`DeprecationLevel.ERROR` source stays compilable. For a merged accessor the first deprecation
in branch order wins.

### Errors

The following usages are reported as compile errors (with a suggested solution):

- `@ParentOptional` on a property whose enclosing class has **no sealed supertype**.
- `@ParentOptional` on a **private** property (the generated top-level accessor could not
  reference it).
- `@ParentOptional` on a property declared by a class the generated accessor **cannot
  reference** (`private` / `protected` anywhere in its enclosing chain — the generated `is`
  check would not compile).
- **Type mismatch** among properties merged into one accessor.
- The sealed parent **already has a member with the generated name** (visible members always
  win over extensions, so the accessor would be dead code).
- A property type referencing a type parameter **not pinned directly** by the sealed parent
  (see [Generic parents](#generic-parents)).
- `@ParentOptional` on an **extension property** (the accessor cannot supply the extension
  receiver).
- Forcing `visibility = CopyVisibility.PUBLIC` (or `cream.defaultVisibility=PUBLIC`) when the
  accessor's signature would **expose an internal symbol** — an internal sealed parent
  (receiver) or an internal property type. Kotlin would reject the generated declaration, so
  cream rejects it up front. (Reading an *internal property* from a public accessor is fine —
  only the signature is constrained.)

### Known limitations

- The fallback value is always `null` — a non-null fallback cannot be configured.
- No **module-wide** naming template: `propertyName` reaches one property, and there is no
  `cream.*` option applying to every accessor in the module.
- Type-mismatched merges are not unified to a common supertype (no LUB resolution); this
  includes `T` vs `T?` and a typealias vs its expansion.
- Properties whose type uses a child-specific (unpinned) type parameter are not supported —
  including pinning **across** an intermediate sealed type (see
  [Generic parents](#generic-parents)).
- The `propertyName` argument is not validated (same policy as `funName`): a name that is not
  a legal Kotlin identifier even with backticks fails to compile in the generated file.
- The KDoc `kdoc = KDoc(...)` argument of a **merged** accessor renders only the first entry's
  value (in branch order).
- `expect`/`actual` sealed hierarchies are untested: KSP processes each compilation
  independently, so behavior follows whatever declarations the processed compilation sees.
- KSP multi-round processing: symbols deferred across rounds that re-aggregate into an
  already-written `ParentOptional__<Parent>` file could collide; not observed in practice,
  unverified.

### Other customizations

- The **KDoc** of the generated accessor can be augmented with `kdoc = KDoc(...)` —
  see [KDoc](./customization/kdoc.md).
- The **visibility** of the generated accessor can be controlled with the `visibility`
  argument — see [Visibility](./customization/visibility.md). With the default `INHERIT`, the
  `cream.defaultVisibility` option applies first, then the accessor inherits the narrowest
  visibility among the sealed parent, the child, and the property.
- The **name** is customized per property via `@ParentOptional(propertyName = ...)` — see
  [Naming the accessor](#naming-the-accessor). The function-naming options (`funName`,
  `cream.copyFunNamePrefix`, ...) do not apply here, since this annotation generates an
  extension property, not a function.

### Naming the accessor

`propertyName` is a **template string**, the same way `funName` is for cream's copy/combine
annotations. The token `DefaultAccessorPropertyName` resolves to the annotated property's own
name, and it is the argument's default — so omitting `propertyName` keeps the base name.

```kt
import me.tbsten.cream.*

sealed interface Fetch {
    data class Loaded(
        @ParentOptional                                                         // body
        val body: String,
        @ParentOptional(propertyName = "${DefaultAccessorPropertyName}OrNull")  // statusOrNull
        val status: Int,
        @ParentOptional(propertyName = "resultCode")                            // resultCode
        val code: Int,
    ) : Fetch

    data object Pending : Fetch
}
```

Because the token is a `const val`, both `"${DefaultAccessorPropertyName}OrNull"` and
`DefaultAccessorPropertyName + "OrNull"` work and stay compile-time constants.

The resolved name decides which contributions **merge**: two children whose accessors resolve
to the same name become one accessor, and two whose base property names differ stay separate
even when both use the same template.

## See also

- [@CopyToChildren](./copy-to-children.md) — the copy-function counterpart on sealed parents:
  it generates `Parent.copyToChild(...)` functions, whereas this annotation generates a
  read-only nullable accessor on the parent.
- [@SealedCopy](./sealed-copy.md) — `copy()` on the sealed parent that preserves the subtype.
- [KDoc](./customization/kdoc.md) — the `kdoc = KDoc(...)` argument for generated declarations.
- [Visibility](./customization/visibility.md) — the `visibility` argument and `cream.defaultVisibility`.
- [Options](./customization/options.md) — index of all KSP arguments.
