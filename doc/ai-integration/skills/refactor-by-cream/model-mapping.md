# Refactor cross-layer model mapping by cream.kt

Covers hand-written mapping functions between models of different layers — API response or DB
entity in the data layer, and the model the rest of the app works with in the domain layer.

The problem this solves is **signal-to-noise**. A `toDomain()` grows one line per property, and
almost all of those lines are trivial copies. The two or three lines that actually matter — a
`value class` wrap, a `String` -> `Instant` parse, a derived value — look exactly like the copies
sitting next to them, so reviewers have to read all of it to find them. Lines that only differ in name
(`thumbnailUrl = this.imageUrl`) are also where copy-paste mistakes hide.

## Before / after

```kt
// data layer
@Serializable
data class ItemResponse(
    val itemId: String,
    val name: String,
    val price: Int?,
    val description: String,
    val imageUrl: String?,
    val stock: Int,
    val updatedAt: String, // ISO-8601
)

// domain layer
@JvmInline
value class ItemId(val value: String)

data class Item(
    val itemId: ItemId,
    val name: String,
    val price: Int?,
    val description: String,
    val thumbnailUrl: String?, // named imageUrl in the data layer
    val stock: Int,
    val updatedAt: Instant,
)

fun ItemResponse.toDomain(): Item = Item(
    itemId = ItemId(this.itemId),              // value class wrap
    name = this.name,                          // trivial
    price = this.price,                        // trivial
    description = this.description,            // trivial
    thumbnailUrl = this.imageUrl,              // only the name differs
    stock = this.stock,                        // trivial
    updatedAt = Instant.parse(this.updatedAt), // matters
)
```

Declare the mapping once, and bind the renamed property:

```kt
import me.tbsten.cream.CopyMapping

// neither ItemResponse nor Item is modified
@CopyMapping(
    source = ItemResponse::class,
    target = Item::class,
    properties = [CopyMapping.Map(source = "imageUrl", target = "thumbnailUrl")],
)
private object ItemMapping
```

cream generates a default for every property whose **name and type match**, and deliberately
generates **no default** where the types differ:

```kt
// auto generate
fun ItemResponse.copyToItem(
    itemId: ItemId,                        // no default: String -> ItemId
    name: String = this.name,
    price: Int? = this.price,
    description: String = this.description,
    thumbnailUrl: String? = this.imageUrl, // bound via properties
    stock: Int = this.stock,
    updatedAt: Instant,                    // no default: String -> Instant
): Item
```

So the mapping function collapses to only the parts worth reviewing:

```kt
fun ItemResponse.toDomain(): Item = copyToItem(
    itemId = ItemId(this.itemId),
    updatedAt = Instant.parse(this.updatedAt),
)
```

`toDomain()` here is still the project's **own hand-written function** — the same one from the
Before block, with its body replaced. cream neither generates it nor has any say over its name;
`cream.copyFunNamePrefix` and friends only ever name the generated `copyToItem`.

> **Version note — automatic `value class` mapping.**
> The signature above is what the **latest released** cream produces (0.9.0): a `value class`
> wrapper counts as a type difference, so `itemId` stays required.
> [Automatic value class mapping](https://github.com/TBSten/cream/blob/main/doc/customization/value-class-mapping.md)
> — which would generate `itemId: ItemId = ItemId(this.itemId)` instead — is implemented on `main`
> but is **not in any release yet**. That doc describes the unreleased behaviour. When it ships,
> value-class-only differences move from the last group into the auto-mapped group and the
> `itemId` argument disappears from `toDomain()`.
> Read the generated file for the version actually on the classpath; never assume either way.

The compiler now enforces the important half: add a property with mismatched types later and the
call site fails to compile until the conversion is written.

## The generated name

`copyToItem` above is the **default** name — the `cream.copyFunNamePrefix` prefix (`copyTo`) plus
the target class name rendered by `cream.copyFunNamingStrategy`. A project that configured those
options at setup time gets something else (`mapToItem`, `toItem`, ...). Never assume the name; read
it out of the generated file.

**Leave it at whatever the project's configuration produces.** The name only ever appears inside
the wrapper; call sites keep seeing `toDomain()`. Renaming the generated function to look nicer
adds configuration for no behavioural gain.

The one case where the annotation's `funName` argument earns its keep is a mapper whose last group
in [step 2](#2-classify-each-property-pair) is **empty**. The generated function then takes no
arguments, so the wrapper would do nothing but forward — pointing `funName` at the existing
mapper's name and deleting the wrapper leaves every call site compiling untouched. Even then, say
you are doing it rather than doing it silently: it is a rename, and a literal-only `funName` is
rejected at build time when one annotation generates more than one function (with
`canReverse = true`, build distinct names from the `CopyTargetSimpleName` /
`CopyTargetUnderPackage` tokens).

`funName` **replaces the whole name**; the prefix is not prepended to it. `funName = "toDomain"`
generates `fun ItemResponse.toDomain(...)`, not `copyToDomain`. (That is why the example in
[Function name](https://github.com/TBSten/cream/blob/main/doc/customization/fun-name.md) spells out
`funName = "copyToSuccess"` in full.) `cream.copyFunNamePrefix` only feeds the derived default name
used when `funName` is absent.

## Choosing the annotation

| Situation | Use |
|---|---|
| Neither class may reference the other — library types, generated types, or strict layering you must not break | `@CopyMapping` on a `private object` in a mapping file |
| You own the source class, and its layer is allowed to depend on the target's layer | `@CopyTo(Target::class)` on the source |
| You own the target class, and its layer is allowed to depend on the source's layer | `@CopyFrom(Source::class)` on the target |
| One target is built from **several** sources (response + cache + user input) | `@CombineTo` / `@CombineFrom` / `@CombineMapping` |

`@CopyMapping` is usually the right default for cross-layer work: because neither class is touched,
the domain model keeps no reference to the data layer. Reach for `@CopyTo` only when the dependency
direction genuinely allows it — the data layer depending on the domain layer is normally fine.

Rename bindings are `CopyMapping.Map(source = ..., target = ...)` on the mapping annotation. When
the annotation sits on a class instead, the binding is a `.Map` on that class's own constructor
parameter, naming the other side's property: `@CopyTo.Map("dataId") val domainId: String` on the
source, `@CopyFrom.Map("dataId") val domainId: String` on the target. See
[Property mapping](https://github.com/TBSten/cream/blob/main/doc/customization/property-mapping.md).

## Finding candidates

```shell
# conventional mapper function names
grep -rnE --include='*.kt' --exclude-dir=build "fun .*\.(toDomain|toEntity|toModel|toDto|toResponse|toData|asDomain|asEntity)\(" .
# mapper types
grep -rnE --include='*.kt' --exclude-dir=build "(class|object) .*Mapper" .
# mapping files
find . -name '*Mapper*.kt' -o -name '*Mapping*.kt' | grep -v '/build/'
```

Two details these commands depend on: **quote the `--include` glob** (zsh expands `--include=*.kt`
and refuses to run the command at all), and **exclude `build/`** — once cream has generated
anything, the same patterns match the generated sources and the `build/kspCaches/` backups of them.

A function is a strong candidate when most of its arguments are plain `this.x` / `it.x` reads and
only a few apply a conversion.

Skip a mapper where **every** line is a real conversion — there is no boilerplate to remove, and
cream would only add indirection.

## Workflow

### 1. Inventory the mapping functions

List the mapper functions in scope, and for each: the source type, the target type, and which
module/layer each lives in. You need the layer information to pick the annotation.

### 2. Classify each property pair

For one mapper at a time, split its properties into four groups:

- **same name, same type** — cream generates the default; the line disappears
- **different name, same type** — needs a `.Map` binding; the line disappears
- **same name, differing only by a `value class` wrapper** — depends on the cream version. On 0.9.0
  it is a type difference and stays an explicit argument; with automatic value class mapping it
  disappears. The generated signature settles it — see the version note above
- **different type, or derived from several properties, or a constant** — stays an explicit
  argument at the call site

Anything you cannot confidently place in one of the first three groups belongs in the last.

### 3. Check the dependency direction, then annotate

Pick the annotation from the table above. If in doubt, use `@CopyMapping` — it never introduces a
new dependency between the two classes.

Put the `@CopyMapping` declaration in the layer that is already allowed to see both types (usually
a mapping file in the data layer), and keep it `private` unless the generated function is needed
outside the file.

### 4. Generate, then read the generated signature

```shell
./gradlew :<module>:kspCommonMainKotlinMetadata   # KMP
./gradlew :<module>:compileKotlin                 # single-platform
find <module>/build/generated/ksp -name '*.kt'
```

Read the generated function before rewriting anything, for two things.

**The name.** It depends on `cream.copyFunNamePrefix` and `cream.copyFunNamingStrategy`, so it is
`copyToItem` only under the defaults. Do not guess it.

**Which parameters got defaults.** This is the authoritative answer to step 2 — if a property you
expected to be copied still has no default, the names or the types do not match as closely as you
assumed. It also tells you whether the wrapper is still doing anything: no required parameters
means the `funName` case above applies.

### 5. Rewrite the mapper body

If the generated function has required parameters, keep the existing public function (`toDomain()`
and friends) so call sites are untouched, and replace only its body with the generated call plus
the arguments from the last group. Delete the hand-written constructor call.

If it has none, the wrapper would only forward. Propose the `funName` route above instead of
leaving an empty wrapper — but say so rather than renaming silently.

### 6. Verify

Run the verification method the user chose in step 1 of the parent skill. That answer governs —
including when it was "compile only" or "do not verify". Do not upgrade it on your own initiative.

If the chosen method looks too weak for what you changed — mapper unit tests exist and would catch
a mis-bound property, but the user picked compile-only — say so in the report and let them decide.
Do not quietly run them anyway.

## Watch out for

- **Nullability differences.** `String?` -> `String` is a type mismatch, so no default is
  generated and the compiler asks for an explicit value. That is the correct outcome — supply the
  fallback deliberately rather than annotating your way around it.
- **`value class` conversions depend on the version.** On 0.9.0 they are plain type differences and
  stay explicit arguments. Once automatic value class mapping ships, a conversion you were about to
  write by hand is already generated — except where it would be unsound (`U?` -> `V` keeps the
  parameter required and cream emits a warning), and it can be turned off module-wide with
  `cream.autoValueClassMapping=false`. Read the generated signature rather than assuming either way.
- **A same-named property that must NOT be copied.** Use `excludes` on the mapping annotation (or
  `.Exclude` on the property) so callers are forced to pass a value, instead of silently inheriting
  the source's.
- **Collections and nested models.** A `List<ItemResponse>` -> `List<Item>` property is a type
  mismatch and stays an explicit argument; map the element type separately and call `.map { }`
  there.
- **Bidirectional mapping.** `@CopyMapping(canReverse = true)` generates the reverse function too,
  with the `properties` mappings automatically reversed — a second annotation for domain -> data is
  not needed. Note that `excludes` then applies in both directions.
