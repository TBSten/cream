# Feature profiles

Per-feature structural facts for the snapshot-test rollout. These are pre-computed from the
processors, but the **live processor is the source of truth** — confirm before relying on a detail.
Paths are relative to repo root `cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/feature/<name>/Process<Name>.kt`
and `cream-runtime/src/commonMain/kotlin/me/tbsten/cream/<Name>.kt`.

## Archetypes (pick the closest reference)

| Archetype | Features | Reference to mirror | Core generator |
|---|---|---|---|
| 1→1 copy, source-annotated | CopyTo | copyTo (done) | `appendCopyFunction` |
| 1→1 copy, target-annotated | CopyFrom | copyFrom (done) | `appendCopyFunction` |
| 1→N sealed fan-out | CopyToChildren (✅ done) | **copyToChildren** (done) | `appendCopyFunction` per child |
| sealed self-copy | SealedCopy (✅ done) | **sealedCopy** (done) | `appendSealedCopyFunction` |
| N→1 combine | CombineTo (✅ done), CombineFrom (✅ done) | **combineTo / combineFrom** (done) | `appendCombineToFunction` |
| library mapping | CopyMapping (✅ done), CombineMapping (✅ done) | **copyMapping / combineMapping** (done) | `appendCopyFunction` / `appendCombineToFunction` |
| sealed-parent accessor (child prop → parent) | ParentOptional (✅ done), ChildOptionals (✅ done) | **parentOptional / childOptionals** (done) | `appendParentOptionalAccessor` |

`GenerateSourceAnnotation` (10 sealed subtypes) per-subtype fields: `CopyToChildren.notCopyToObject: Boolean?`,
`CombineFrom.funNameTemplate: String`, `CopyMapping.reversed: Boolean`. The rest carry no extra field.

## Summary table

| Aspect | CopyToChildren | SealedCopy | CombineTo | CombineFrom | CopyMapping | CombineMapping |
|---|---|---|---|---|---|---|
| Annotation site | sealed parent | sealed parent | source | target | mapping holder | mapping holder |
| Cardinality | 1→N (fan-out) | 1→1 (self) | N→1 (per target) | N→1 (merged) | 1↔1 (±reverse) | N→1 (merged) |
| `@Repeatable` | No | Yes | No | Yes | Yes | Yes |
| Nested `@Exclude` on | sealed parent abstract props | sealed parent abstract props | source props | target params | (none) | (none) |
| Nested `@Map` on | (none) | child functions | source props | target params | annotation `properties` arg | annotation `properties` arg |
| Core generator | `appendCopyFunction` | `appendSealedCopyFunction` | `appendCombineToFunction` | `appendCombineToFunction` | `appendCopyFunction` | `appendCombineToFunction` |
| File name | `CopyToChildren__<src>` | `SealedCopy__<annotated>` | `CombineTo__<src>__<tgt>` | `CombineFrom__<src>__<tgt>` | `CopyMapping__<holder>` | `CombineMapping__<holder>` |
| `generatesMultipleFunctions` | `targets.size > 1` | (per-annotation, stacked) | `targets.size > 1` | (single merged) | `canReverse || target.isSealed()` | (single merged) |

## CopyToChildren — 1 sealed parent → N children (fan-out)  ✅ DONE — live reference (sealed fan-out)

Suite built: `feature/copyToChildren/scenario/` + `CopyToChildrenSnapshotTest.kt` (9 files / 27 scenarios / 81 goldens). Mirror it for `@SealedCopy` (the other sealed-parent feature).

- Annotated: the **sealed parent** (= the source/receiver AND the annotated decl, so #144 does NOT
  bite — attribution `of [Source]` is correct here). Targets discovered via `getSealedSubclasses()`,
  recursing to all transitive concrete leaves.
- `@CopyToChildren.Exclude` goes on the **sealed parent's abstract properties**; removes the
  `= this.x` default from *every* per-child function. Warns on non-abstract props (no-op). No `.Map`.
  Not `@Repeatable`. No `funName` arg.
- `notCopyToObject` arg controls whether `object` children get a function (falls back to the
  `cream.notCopyToObject` option). **Feature-unique axis** → a `notCopyToObject` family (arg on/off).
  Note: the arg-unset→option-fallback branch isn't exercised (the 3 representative options all have
  `notCopyToObject=false`); output is byte-identical to arg=true anyway, so acceptable / defer to Edge.
- **Families used (8)**: `sealedParentKind` (recast sourceKind: sealed interface vs class +
  non-sealed-parent reject), `hierarchyShape` (recast targetKind: data/object/nested-sealed leaves,
  **shared-prop direct + transitive `= this.x` propagation**, enum-child reject, noChildren), generics,
  propertyShape, exclude (abstract-prop + no-effect warning), kdoc, visibility, notCopyToObject.
  Dropped: map/funName/repeatable (annotation lacks them), constructor/matching (same per-child
  `appendCopyToClassFunction` as copyTo → byte-identical → redundant), nesting (FQ child resolution
  covered by hierarchyShape's nested-sealed leaves).
- **Lessons (now in SKILL.md)**: (1) the defining behavior — a shared parent prop defaulting to
  `= this.x` on (transitive) children — must be a scenario; structural hierarchy variety alone left it
  uncovered. (2) An `internal` child nested in a sealed *interface* is illegal Kotlin (error-golden
  bug, not error-as-golden) → use a top-level sibling.
- Existing test data: `test/src/commonTest/.../copyToChildren/{Basic,Nested,ComplexTypes,ObjectTarget,Visibility}Test.kt`

## SealedCopy — sealed self-copy (type-preserving)  ✅ DONE — live reference (sealed self-copy)

Suite built: `feature/sealedCopy/scenario/` (12 files / 32 scenarios / 96 goldens). Reuses copyToChildren's sealed-parent helpers (`sealedInterfaceParent`/`childClass`).

- Annotated: the **sealed parent** (= receiver AND annotated → no #144). Generated = a single
  type-preserving self-copy: `fun Parent.copy(absProp = this.absProp, …): Parent = when (this) { is Leaf
  -> this.copy(…) }` over ALL transitive concrete leaves. `@Repeatable` (stacked → multiple variants
  in one file; duplicate resolved funName across the stack → clean cream reject).
- `@SealedCopy.Map` marks a **child's delegate function** (leaf dispatches `is X -> this.<mappedName>(…)`
  instead of `this.copy(…)`) — NOT copyTo's property `.Map`. `@SealedCopy.Exclude` on **sealed-parent
  abstract properties**. `nonCopyableStrategy` (per-annotation arg): `ERROR` → clean reject; `RETURN_AS_IS`
  → `is X -> this`; `RETURN_NULL` → `is X -> null` AND widens return to `Parent?` (only when a
  non-copyable leaf exists). A non-data `class` WITH a compatible `copy()` member is copyable without
  `@Map` (the `classify`/`findCompatibleCopyFunction` branch — easy to miss, add a case).
- **Families used (11)**: sealedParentKind (interface/class + non-sealed reject), hierarchyShape
  (data/transitive-nested/no-abstract-props/**nonDataClassWithCopyMember**), generics (incl.
  star-projection `is Tagged<T, *>`), propertyShape, **nonCopyableStrategy** (ERROR/RETURN_AS_IS/
  RETURN_NULL/nonDataClassRejected — feature-unique), map (child-delegation), exclude (parent abstract
  prop + no-effect warning), kdoc, visibility (`@SealedCopy` HAS a visibility arg), funName,
  **repeatable** (stackedVariants + duplicateFunNameRejected). Dropped: matching/nesting/constructor/
  multiSource (N/A — no findMatchedProperty, no per-ctor loop, subsumed by transitive hierarchy).
  Note: NO `zeroProps` (would be an illegal empty `data class`) — use `noAbstractProperties` (empty-param
  self-copy on an interface) instead.
- No structural twin → byte-identity substitute = generated output identical across all 3 option
  variants (SealedCopy's default funName `copy` + `CopyTargetSimpleName` token are option-independent).
- Quirk note: unbounded type params render `<T : Any?>` — this is SHARED with copy (same renderer),
  NOT a sealedCopy bug — verified by diffing copyTo's `sharedTypeParam`. Don't file.
- Existing test data: `test/src/commonTest/.../sealedCopy/{Basic,FunName,Multiple}Test.kt`

## CombineTo — N sources → 1 target, source-annotated  ✅ DONE — live reference (N→1 combine + multiSource)

Suite built: `feature/combineTo/scenario/` (12 files / 39 scenarios / 117 goldens). Mirror it for `@CombineFrom` (target-annotated twin, shares `appendCombineToFunction`) and `@CombineMapping` (holder + multiSource).

- Annotated: each **source** carries `@CombineTo(vararg targets)`; multiple sources → same target are
  combined into one function (one is the receiver `this`, others are leading params). One file per
  source-target pair (`CombineTo__<src>__<tgt>`). Duplicate-target rejected. **Not `@Repeatable`.**
- `@CombineTo.Map` / `.Exclude` on **source props**. `.Exclude` no-effect warning spans the union of
  all target params; an exclude in ANY contributing source suppresses the default across EVERY
  generated function (`CombineToClass.kt` — cross with multiSource to exercise it, not single-source).
- **Families used (12)**: sourceKind, targetKind, **multiSource** (2/3 sources, overlap-winner,
  `excludeSuppressesAcrossSources`), generics, propertyShape, matching, map (`@CombineTo.Map` on source
  prop), exclude, kdoc, visibility (`internalSourceClass` — source-annotated like copyTo), **funName**
  (multi-target → token required; literal+multi → clean cream `COMPILATION_ERROR`). Dropped:
  nesting/constructor (reuse copy's generic FQ-resolution & per-ctor loop verbatim → redundant),
  repeatable (not repeatable). typealias → can't (TypeAliasSpec) → EdgeUsage.
- **Known quirk (#132, shared by all combine features)**: combine lacks copy's
  `concreteClassRejection()` → abstract/inner/private-ctor/annotation-class targets emit *uncompilable
  generated code* (error on the generated file) instead of clean rejection. Frozen as goldens with
  `// TODO(#132)` markers; do NOT fix the generator. Also: combine does NOT fan out a sealed target
  (rejects it), unlike copy.
- Single-source byte-identity: a 1-source combine's signature/body == copyTo's modulo `// file:` +
  attribution + the KDoc auto-description (combine renders `[Source] -> [Target]` links).
- Existing test data: `test/src/commonTest/.../combineTo/{Basic,TypeAlias,PropertyMapping,Generics,Nullable,ObjectTarget,Overlap,Visibility,FunName,MultiSource}Test.kt`

## CombineFrom — N sources → 1 target, target-annotated  ✅ DONE — live reference (target-annotated combine + @Repeatable)

Suite built: `feature/combineFrom/scenario/` (14 files / 42 scenarios / 126 goldens). Mirror combineTo (multiSource) + copyFrom (target-annotated) + this one's `Repeatable.kt` (merge/dedup/funName).

- Annotated: the **target** carries `@CombineFrom(vararg sources)`; `@Repeatable`. Stacked occurrences
  are **merged into ONE function** (sources flattened in order, first = receiver, then `.distinct()`
  to avoid the #101 duplicate-param bug). Single merged fn ⇒ **plain-literal funName always allowed**
  (no token requirement); the only funName concern is cross-occurrence agreement.
- `@CombineFrom.Map` / `.Exclude` on **target constructor params** (`@Map` arg = a SOURCE prop name;
  value can resolve from a non-primary source → `= sourceB.x`). Mirror copyFrom's placement.
- **Families used (12)**: sourceKind (sources referenced), targetKind (annotated merge target + **#132**
  reject captures), multiSource (twoSources/threeSources/overlap/**excludeOverlappingProperty**/
  **mapAcrossSources**), generics, propertyShape, matching, map (`@CombineFrom.Map` on target param),
  exclude, kdoc, visibility (`internalTargetClass` + overrides — CombineFrom HAS a `visibility` arg),
  funName (literal+token only, NO multi-rejected — single merged fn), **repeatable** (stackedAnnotations
  merge **#134**, `duplicateSourceDeduped` `.distinct()`, stackedAnnotationsSameFunName accept,
  conflictingFunNamesRejected reject). Dropped nesting/constructor (redundant shared core).
- **Known quirks**: #132 (combine target validation — abstract/inner/private-ctor emit uncompilable
  code, `// TODO(#132)`), #134 (stacked occurrences merge into one fn vs one-per-occurrence,
  `// TODO(#134)`). Captured as goldens; generator untouched.
- Single-source families byte-identical to combineTo (same `appendCombineToFunction` core).
- Existing test data: `test/src/commonTest/.../combineFrom/{Basic,TypeAlias,PropertyMapping,Generics,Nullable,ObjectTarget,Overlap,Visibility,FunName,MultiSource}Test.kt`

## CopyMapping — library-to-library 1↔1  ✅ DONE — now a live reference (holder-annotated)

The snapshot suite is built (`feature/copyMapping/scenario/` + `CopyMappingSnapshotTest.kt`, 13
scenario files / 38 scenarios / 117 goldens). Mirror it for `@CombineMapping` and any holder feature.

- Annotated: a **mapping holder** (any class/object, typically `private object Mapping`). Source and
  target are **annotation arguments** (`source`/`target: KClass<*>`), so both can be external classes.
  `@Repeatable`. `canReverse` generates the reverse direction too (`reversed: Boolean` on the GSA);
  if `target.isSealed()` it fans out per child. Multi-occurrence path: `groupBy { sourceClass.packageName }`
  → one emitted file per source package.
- `@CopyMapping.Map(source=…, target=…)` lives in the annotation's `properties` array — **pure
  config, not on class members**. No `.Exclude`. **No `visibility` arg** → generated fn inherits the
  **target** class's visibility.
- **Family set actually used (12)**: sourceKind, targetKind, nesting, generics, constructor,
  propertyShape, matching, **map** (config array), **canReverse** (replaces exclude; bidirectional +
  reversed mappings), kdoc, **visibility** (reduced: internalTargetClass + propertyVisibilities, no
  override), **funName** (literal/token/token-reversible/literal-reversible-rejected), **repeatable**
  (multiple `@CopyMapping` same-package → one file, N fns). DROPPED: exclude (none), visibility-override
  (no arg), typeAlias (SnapshotScenario can't carry a `TypeAliasSpec`; integration-tested instead).
- **Known quirk captured**: sealed target → KDoc misattributes `of [Source]` instead of `of [Mapping]`
  (issue #144, the shared `appendCopyToSealedClassFunction` drops `annotated`; non-sealed path is correct).
- Existing test data: `test/src/commonTest/.../copyMapping/{CopyMapping,TypeAlias,FunName}Test.kt`

## CombineMapping — library-to-library N→1  ✅ DONE — live reference (holder + N→1 combine)

Suite built: `feature/combineMapping/scenario/` (13 files / 38 scenarios / 114 goldens). Fuses copyMapping (holder/properties/repeatable) + combineTo (multiSource/combine core/#132/funName).

- Annotated: a **mapping holder**; `sources: Array<KClass<*>>` (**min 2**) + one `target`, all external.
  `@Repeatable`. **One merged function per annotation** (NOT merged across occurrences — no #134);
  occurrences `groupBy { sourceClasses.first().packageName }` → one file per package (same as copyMapping).
  No reverse. Source kind validated: only CLASS / ANNOTATION_CLASS sources allowed.
- `@CombineMapping.Map(source=…, target=…)` in the `properties` array (pure config). **No `.Exclude`.**
- **⚠ It DOES have a `visibility` arg** (`CombineMapping.kt:105`, `= INHERIT`) — unlike `@CopyMapping`,
  which does NOT. So keep the FULL visibility family (internalTargetClass + 2 overrides + propertyVisibilities),
  like combineFrom — NOT the reduced copyMapping one. (Earlier this profile wrongly said "no visibility
  arg"; the build agent's processor-first read caught it. Lesson: confirm every arg against the runtime.)
- **Families used (12)**: sourceKind (value/plain — sealed source rejected, moved to validation),
  targetKind (+ **#132** captures), multiSource (twoSources/threeSources/overlap/mapAcrossSources),
  generics, propertyShape, matching, map (config-array + mapToNonexistentProperty/mapOverridesNameMatch),
  kdoc, visibility (FULL), funName (literal+token, single fn → no multi-reject), repeatable
  (multipleAnnotations, group-by-package), **sourceKindValidation** (insufficientSources <2, nonClassSource
  — feature-unique rejects). Dropped: exclude (none), canReverse (none), nesting/constructor (redundant),
  zeroProps (illegal empty data class).
- Known quirk: #132 (shared combine target validation — abstract/inner/private-ctor uncompilable,
  `// TODO(#132)`). Falsified as new (combineTo/combineFrom identical) → no new issue.
- multiSource families byte-identical to combineFrom (config-array `@Map` ≡ member `@Map` via same core).
- Deferred (framework limits, like copyMapping): typealias (`SnapshotScenario` can't carry `TypeAliasSpec`),
  cross-package multi-file grouping + the one-bad-annotation-suppresses-holder short-circuit (single
  `GENERATED_PACKAGE` generator can't express multi-package input) → EdgeUsage / integration tests.
- Existing test data: `test/src/commonTest/.../combineMapping/{Basic,TypeAlias,PropertyMapping,Overlap,FunName,MultiSource}Test.kt`

## ParentOptional — child property → nullable accessor on sealed ancestors  ✅ DONE — live reference (sealed-parent accessor)

Suite built: `feature/parentOptional/scenario/` (10 files / 38 scenarios / 152 goldens). NOT a copy/combine
archetype: no referenced target, no constructor call, no property matching — its own `core/parentOptional/`
generator (`appendParentOptionalAccessor`), shared with `@ChildOptionals`.

- Annotated: a **child property** (`AnnotationTarget.PROPERTY`; for a primary-constructor `val` KSP may
  surface the annotation on the value parameter — `parentOptionalAnnotationOrNull()` handles both).
  Generates one nullable extension property per (sealed ancestor, accessor name): `public val Parent.x: T?
  get() = when (this) { is Child -> x; else -> null }` on EVERY transitive sealed ancestor (intermediate
  sealed types get their own file `ParentOptional__<Parent>`). Same-named props of multiple children merge
  into one accessor (one `is` branch each). Not `@Repeatable`; args: `propertyName` / `visibility` / `kdoc`
  (no funName / Map / Exclude).
- **Ownership rule**: an ancestor annotated `@ChildOptionals` is skipped here (that feature generates it,
  respecting the property's `@ParentOptional` args) — pinned from the ChildOptionals side.
- **Generics v1 limit**: child→parent type-param mapping uses the DIRECT supertype reference only
  (`Filled<E> : Source<E>` → `val <T> Source<T>.item: T?`); a chain (`Leaf<X> : Middle<X> : Root<X>`)
  generates on Middle but rejects on Root (`chainedTypeParamRejected` error-as-golden).
- **Families used (9)**: sealedParentKind (interface/class), hierarchyShape (siblings / intermediate
  ancestors / multi-accessor file), **merge** (two children + across-intermediate + subtype
  most-derived-first + diamond two-sealed-interface parents — feature-defining),
  **propertyName** (rename + rename-avoids-merge), propertyShape (primitives / nullable incl. the
  ambiguity KDoc note / collection / custom type / typealias-preserved / lateinit / delegated /
  object-child body prop / hard-keyword name), generics (direct-pin OK / bounded / multi-bound
  `where` on the property / chain reject / generic-leaf star-projection), kdoc
  (+ `mergedKdocUsesFirstEntry` pins the v1 first-entry-wins quirk), visibility
  (inherit-narrowest incl. merged-narrowest + overrides both ways + public-read-of-internal-prop),
  **deprecated** (property / child class / level=ERROR / merged-first-wins — propagation +
  `DEPRECATION(_ERROR)` suppression, since property accessors get NO containment exemption unlike
  functions). Dropped: targetKind / nesting / constructor / matching / funName / map / exclude /
  repeatable (annotation & archetype lack them); misuse diagnostics live in
  `ParentOptionalInvalidUsageTest` goldens (13: incl. extension property, nullable-vs-non-null &
  alias-vs-expansion merges, forced-public exposure ×3).
- Byte-identity substitute: only `defaultVisibility` affects output — all scenarios byte-identical across
  the 3 non-visibility option variants (checked mechanically). `<T : Any?>` rendering is the shared
  renderer behavior (== copyTo/sealedCopy), not a quirk.
- **Input trap (recurring)**: an `internal` child nested in a sealed *interface* is illegal Kotlin —
  top-level sibling instead (hit again here; same lesson as copyToChildren).
- Existing test data: `test/src/commonTest/.../parentOptional/` + `ParentOptionalBasicUsageTest`.

## ChildOptionals — sealed parent sweeps leaf properties  ✅ DONE — live reference (blanket accessor sweep)

Suite built: `feature/childOptionals/scenario/` (8 files / 24 scenarios / 96 goldens). Same core generator
as `@ParentOptional` (`appendParentOptionalAccessor`); one file `ChildOptionals__<Parent>` per annotated parent.

- Annotated: the **sealed parent**; sweeps every transitive concrete leaf's OWN declared public/internal
  properties (constructor + body). Skips parent-visible props (overrides — member always beats extension)
  and SILENTLY skips private props (unlike explicit `@ParentOptional`, which errors). Args: `visibility` /
  `kdoc` only. Not `@Repeatable`.
- **Interop (ownership)**: a swept prop carrying `@ParentOptional` keeps its `propertyName`/`kdoc`/
  `visibility` and its KDoc attribution; with an intermediate sealed type the parent's accessor comes from
  ChildOptionals and the intermediate's from ParentOptional (two files, no redeclaration) —
  `ownershipSplitAcrossAncestors`.
- **Families used (7)**: sealedParentKind, hierarchyShape (sweep / transitive nested leaves / same-name
  merge), **propertyFiltering** (parent-visible skip / private silent skip / body props included /
  extension props silent skip / unpinned-generic prop skip WITH warning (Console-pinned) —
  feature-defining), **parentOptionalInterop** (rename respected / ownership split), kdoc, visibility
  (per-accessor narrowest: internal child's accessor internal while sibling's stays public + override),
  **exclude** (`@ChildOptionals.Exclude` sweep opt-out: single-prop skip / one contributor dropped from a
  merge / all-contributors-excluded → no accessor / no-effect warning (Console-pinned) / `@ParentOptional`
  overrides the exclude — feature-unique). Dropped: generics (byte-equivalent shared core, pinned by
  ParentOptionalSnapshotTest), funName / map / repeatable / targetKind / nesting / constructor / matching
  (annotation & archetype lack them); misuse diagnostics live in `ChildOptionalsInvalidUsageTest` goldens.
- Existing test data: `test/src/commonTest/.../childOptionals/` + `ChildOptionalsBasicUsageTest`.
