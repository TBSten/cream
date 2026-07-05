[← README](../README.md) | [日本語](./comparison.ja.md)

# Comparison with other libraries

When choosing a data mapping library for Kotlin, you might consider several alternatives. Here's how cream.kt compares to other popular mapping libraries:

> **Last reviewed: 2026-07** (as part of the cream.kt docs restructure). These libraries keep evolving — for the latest details, always check the official docs: [MapStruct](https://mapstruct.org/) / [KOMM](https://github.com/Scogun/komm) / [Mappie](https://mappie.tech/).

## vs. MapStruct

**[MapStruct](https://mapstruct.org/)** ([GitHub](https://github.com/mapstruct/mapstruct)) is a mature Java-based code generation library for mapping between different object types.

| Feature | cream.kt | MapStruct |
|---------|----------|-----------|
| **Language** | Kotlin-first with KSP | Java-first with annotation processing |
| **State Transitions** | ✅ Designed for sealed class state transitions ([@CopyToChildren](./copy-to-children.md), [@SealedCopy](./sealed-copy.md)) | ❌ Its stated focus is Java bean (entity-DTO) mapping |
| **Default Value Override** | ✅ Matched properties become default arguments you can override at the call site ([Copy](./copy.md)) | ⚠️ Mapping is declared on a mapper interface; no default-argument-style call-site override |
| **Multiplatform** | ✅ Kotlin Multiplatform support | ❌ JVM only |
| **IDE Support** | ✅ Native Kotlin IDE integration | ⚠️ Java-first tooling (used from Kotlin via annotation processing / kapt) |
| **Use Case** | Frontend state management (e.g., UI states) | Backend entity-DTO conversions |

**When to choose cream.kt over MapStruct:**

- You're working with Kotlin (especially Kotlin Multiplatform)
- You need to manage UI state transitions with sealed classes
- You want to override default values during state transitions
- You prefer a lightweight, Kotlin-native solution

**When MapStruct might be better:**

- You're on a Java-centric backend where the team is already fluent in MapStruct. MapStruct's
  mature ecosystem and your team's existing expertise will likely outweigh cream.kt's Kotlin-first
  advantages there.

## vs. KOMM (Kotlin Object Multiplatform Mapper)

**[KOMM](https://github.com/Scogun/komm)** is a lightweight Kotlin Multiplatform mapping library that also uses KSP for code generation.

| Feature | cream.kt | KOMM |
|---------|----------|------|
| **Structural Mismatch Handling** | ✅ Unmatched properties become required parameters; mismatched names are remapped with [`.Map`](./customization/property-mapping.md) | ⚠️ Configured per property with `@MapName` / resolvers (per the KOMM docs) |
| **Default Value Override** | ✅ Matched properties become default arguments you can override at the call site ([Copy](./copy.md)) | ⚠️ Generated `toX()` functions take no parameters; values are fixed by annotations/resolvers |
| **Advanced Features** | ✅ [`@CopyToChildren`](./copy-to-children.md), [`@CombineTo`](./combine.md), [`@CopyMapping`](./copy.md#copymapping) | ⚠️ Multi-source mappings (one function per source) and plugins; no sealed-hierarchy fan-out |
| **Object Singleton Copy** | ✅ Copy to `object` types, with opt-out ([notCopyToObject](./copy-to-children.md#notcopytoobject)) | ⚠️ Not mentioned in the KOMM docs |
| **Complexity** | ⚠️ More features = steeper learning curve | ✅ Simpler, more lightweight |
| **Flexibility** | ⚠️ Opinionated for state management patterns | ✅ More general-purpose flexibility |

**When to choose cream.kt over KOMM:**

- You're building applications with complex state management (e.g., using libraries like [Koma](https://github.com/komakt/koma))
- You need to copy from a sealed interface to all its children (`@CopyToChildren`)
- You frequently work with sealed class hierarchies for UI states
- You need to combine multiple source classes into one target (`@CombineTo`)
- You want library-to-library mapping without modifying source code (`@CopyMapping`)

**When KOMM might be better:**

- You want a simpler, more general-purpose mapping library
- You prefer maximum flexibility over opinionated patterns
- You have simpler mapping needs without complex state hierarchies

### Side-by-side example

The same `SourceObject` → `DestinationObject` copy, written with each library. The KOMM side
is a simplified version of the minimal example in the [KOMM README](https://github.com/Scogun/komm).

**cream.kt** ([@CopyFrom](./copy.md#copyfrom)):

```kt
class SourceObject {
    val id = 150
}

@CopyFrom(SourceObject::class)
data class DestinationObject(
    val id: Int,
)

// auto generate
fun SourceObject.copyToDestinationObject(
    id: Int = this.id,
): DestinationObject = /* ... */

// usage — any matched property can be overridden at the call site
val copied = sourceObject.copyToDestinationObject()
val overridden = sourceObject.copyToDestinationObject(id = 0)
```

**KOMM** (based on the [KOMM README](https://github.com/Scogun/komm)):

```kt
class SourceObject {
    val id = 150
}

@KOMMMap(from = [SourceObject::class])
data class DestinationObject(
    val id: Int,
)

// auto generate
fun SourceObject.toDestinationObject(): DestinationObject = DestinationObject(
    id = id,
)

// usage — values are fixed by the mapping (no call-site override)
val copied = sourceObject.toDestinationObject()
```

## vs. Mappie

**[Mappie](https://github.com/Mr-Mappie/mappie)** ([official docs](https://mappie.tech/)) is an object-mapping library that works as a Kotlin **compiler plugin**. You declare a mapper object extending `ObjectMappie<From, To>` and describe the properties that cannot be mapped implicitly with a `mapping { ... }` DSL.

| Feature | cream.kt | Mappie |
|---|---|---|
| **Mechanism** | KSP (generates plain Kotlin sources you can read and debug) | Compiler plugin (generated at compile time; no generated sources to inspect) |
| **How you write it** | Just annotate a class (no mapper class needed) | Declare a mapper object and describe it with the `mapping { }` DSL |
| **Combining multiple sources** | ✅ [`@CombineTo` / `@CombineFrom` / `@CombineMapping`](./combine.md). Same-named properties across sources are still mapped automatically (arguments win over the receiver) | ⚠️ `ObjectMappie2`–`ObjectMappie5` (up to 5 sources). Same-named properties across sources are not mapped implicitly and must be specified explicitly |
| **Default value override** | ✅ Name-matched properties become default arguments that the call site can override ([Copy](./copy.md)) | ❌ The mapping is fixed inside the mapper; call sites just call `mapper.map(from)` |
| **State transitions** | ✅ Optimized for sealed-class state transitions ([@CopyToChildren](./copy-to-children.md), [@SealedCopy](./sealed-copy.md)) | ❌ Focused on object-to-object mapping (Entity-DTO and the like) |
| **Multiplatform** | ✅ Kotlin Multiplatform support | ✅ Kotlin Multiplatform compiler plugin |

**Choose cream.kt over Mappie when:**

- You want call-site overrides of the defaults (state-transition-style calls that replace only part of the values)
- You need sealed-class state transitions, or combining more than 5 sources
- You prefer a single annotation over writing mapper classes
- You want to inspect the generated code as plain Kotlin sources

**Mappie fits better when:**

- You want to centralize conversion logic in mapper classes (including enum mappings and the like)
- A Kotlin compiler plugin is acceptable in your build, and your mappings are fixed ones that don't need call-site overrides

## vs. hand-written mapping code

Not every project needs a library. In the following cases, hand-written copy code is likely the better choice:

- **You only have one or two mapping sites.** At that scale, hand-written copy code is cheaper to maintain, and adding KSP to your build (setup effort plus code-generation build-time overhead) likely costs more than it saves.
- **Most of your mapping is transformation logic** — type conversions, validation, aggregation — with few same-name pass-through properties. cream.kt automates the pass-through part, so if there is little of it, there is little for cream.kt to do.
- **Your project has a policy against introducing code generation / KSP.** cream.kt is fundamentally a KSP processor; there is no reflection- or runtime-based alternative mode.
- **You build commonMain-centric Kotlin Multiplatform and can't accept the workaround's constraint.** With the recommended setup, annotations in platform source sets (`androidMain`, `iosMain`, …) are not processed — see [Multiplatform](./customization/multiplatform.md) for details.

## See also

- [Copy — @CopyTo / @CopyFrom / @CopyMapping](./copy.md)
- [Copy to children — @CopyToChildren](./copy-to-children.md)
- [Combine — @CombineTo / @CombineFrom / @CombineMapping](./combine.md)
- [Sealed copy — @SealedCopy](./sealed-copy.md)
