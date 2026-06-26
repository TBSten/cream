# Family catalog

What each of the 11 reference families covers, the case list as built for `@CopyTo`/`@CopyFrom`,
and how to adapt per archetype. Read the live reference file for a family before mirroring it —
this is a map, not a substitute. Families are numbered `00`–`10`; the number drives the golden
directory order, so keep it stable.

Each family file is `scenario/<Family>.kt`, package `…feature.<name>.scenario`, exposing
`internal fun <family>Scenarios(): Generator<SnapshotScenario>` =
`Generator.snapshotScenarios("case" to <anno>(...), …)`.

## 00 sourceKind — kind of the receiver/source declaration
Vary the source's declaration kind; target fixed. Cases: `valueClassSource` (`@JvmInline value class`),
`plainClassSource`, `sealedInterfaceSource`. data-class is covered by propertyShape, so it's not repeated here.
- Combine: replace with / add a **multiSource** axis. Mapping: the "source" is a referenced arg, so
  this becomes part of the mapping-shape family.

## 01 targetKind — ClassKind dispatch on the target
The core dispatches on the target's `ClassKind`: concrete→generate, `object`→singleton,
sealed→fan-out, and reject `abstract` / non-sealed `interface` / `enum` / `inner` / private-ctor.
Cases: `objectTarget`, `sealedInterfaceTarget` (use a 3-level nested sealed to prove transitive
fan-out), `abstractTarget`, `nonSealedInterfaceTarget`, `enumTarget`, `innerTarget`,
`privateConstructorTarget`. Reject cases produce `COMPILATION_ERROR` goldens — expected.
- The dispatch runs on the target regardless of which side is annotated, so the cases are identical
  between copyTo and copyFrom; only the annotation placement (and inner/error node location) differs.
- CopyToChildren/SealedCopy: no referenced target → recast as variation of the sealed hierarchy's
  leaf kinds (object / data / nested-sealed child).

## 02 nesting — declaration nesting + FQ-name resolution
Exercises reference resolution from the annotation site and FQ-name shortening in the generated
file. Vary where the *referenced* class sits relative to the *annotated* one. copyFrom cases:
`sourceNestedInTarget`, `targetNestedInSource`, `bothNestedInOuter`, `sourceNestedTwoLevelsInTarget`,
`sourceNestedTwoLevelsInNestedTarget`. (copyTo mirrors with source/target roles swapped.) Use
`containing`, `classWithNested`, `classNameOf("Outer","Inner")`.

## 03 generics — type-parameter merge
Source/target type params are merged by name; bounds become a `where` clause. Cases:
`targetOnlyTypeParam`, `sourceOnlyTypeParam`, `sharedTypeParam`, `boundedTypeParam`. Note:
`@Map` on a `TYPE_PARAMETER` can't be rendered by KotlinPoet (annotation on a type-param decl), so
it's out of scope here — covered by the VALUE_PARAMETER form in the map family.

## 04 constructor — per-constructor generation
One function per target constructor; `vararg` rendering. Cases: `multipleConstructors` (secondary
constructors via `callThisConstructor`), `varargParameter`.

## 05 propertyShape — property count/type rendering
Source==target (full match) so only the property list varies; checks each type renders in the
signature. Cases: `zeroProps`, `stringProp`, `mixedPrimitives` (String/Int/Boolean/Double),
`nullableProp`, `collectionProp` (`List<String>`), `customTypeProp` (a referenced user type → needs
a 3rd declaration).
- **`zeroProps` MUST use plain classes (`clazz(...)`), not `dataClass(...)`** — an empty `data class`
  is illegal Kotlin (`Data class must have at least one primary constructor parameter`), so a data-class
  zeroProps freezes a *kotlinc input error* (the error-provenance trap), not cream's zero-param copy fn.
  copyTo/copyFrom/copyMapping all shipped this bug; sealedCopy/combineMapping avoided it via plain
  classes / `noAbstractProperties`. `"zeroProps" to copyTo(clazz("Source"), clazz("Target"))`.

## 06 matching — property matching / type compatibility
`findMatchedProperty` + type compatibility; data class fixed. Case: `typeIncompatible` (same name,
incompatible type → no `= this.x` default, becomes a required param).

## 07 map — `@<Anno>.Map`
Map a param to a differently-named property. copyTo: `@CopyTo.Map("targetName")` on the **source**
prop (arg = target name). copyFrom: `@CopyFrom.Map("sourceName")` on the **target** param (arg =
source name). **Get the direction right from the processor** — this is the easiest thing to mirror
wrong. Mapping features: `@Map` is an annotation-arg array (`properties = [Map(source=, target=)]`),
not on a member. Cases: `singleMapping`, `multipleMappings`.
- **Map edge cases** (worth pinning — `findMatchedProperty` has no validation, it silently
  falls through): `mapToNonexistentProperty` (a `Map` whose source name doesn't exist → the param
  gets no default and becomes **required**, NO error/warning, exit OK); `mapOverridesNameMatch`
  (an explicit `Map` is checked *before* by-name matching, so it redirects a param even when a
  same-named source prop exists → `param = this.<mappedSource>`). Both copyMapping (config-array)
  and copyTo/copyFrom (member-annotation) can host these; reference: `copyMapping/scenario/Map.kt`.

## 08 exclude — `@<Anno>.Exclude` (+ no-effect warning)
Drop the auto-copy default from a matched param. Cases: `excludedProperty` (matched → becomes
required), `excludeNoEffect` (on an unmatched declaration → emits a `w: @Exclude … has no effect`
warning, captured in the Console facet). Placement mirrors `@Map`'s side (source props vs target
params vs sealed-parent abstract props).

## 09 kdoc — `@<Anno>(kdoc = ...)`
Custom KDoc on the generated function. Cases: `description`, `descriptionAndExamples` (the
`examples` array renders fenced ```kt blocks — the snapshot fence auto-expands; never hand-edit).

## 10 visibility — `@<Anno>(visibility = ...)` + class/property visibility
INHERIT takes the annotated class's visibility; the arg overrides. Cases: `propertyVisibilities`
(public/internal/private props on the property-bearing class), an `internal<X>Class` case (the
annotated class internal → generated fn inherits internal; pick the side that's annotated — copyFrom
used `internalTargetClass`, which copyTo's suite never covered), `visibilityOverridePublic`,
`visibilityOverrideInternal`.
- **No `visibility` arg → reduce the family**: `@CopyMapping` has NO `visibility` arg (DROP the two
  `visibilityOverride*` cases; keep `internalTargetClass` + `propertyVisibilities`). **But `@CombineMapping`
  DOES have `visibility`** (and so do copyTo/copyFrom/combineTo/combineFrom/copyToChildren/sealedCopy) —
  keep the full family there. **Confirm against the runtime annotation's constructor before deciding**;
  the per-feature profile has been wrong about this (a build agent's processor-first read corrected it).
  When `visibility` is absent, INHERIT resolves against the **target** class (`toModifierString(targetClass)`),
  independent of the annotated holder.

# Feature-specific families (copyTo/copyFrom have none — add when the feature needs them)

## funName — `@<Anno>(funName = ...)` template + the multi-fn name-collision guard
Add whenever `generatesMultipleFunctions` can be true (canReverse, sealed target, multi-target/source).
Cases: `literalFunName` (single fn, plain literal name is allowed); `tokenFunName`
(`"to" + CopyTargetSimpleName` → derived name, stable across all option variants since tokens ignore
`copyFunNamePrefix`/`strategy`/`escapeDot`); `tokenFunName<Multi>` (e.g. `tokenFunNameReversible` —
token + multi-fn → each fn gets a DISTINCT name); `literalFunName<Multi>Rejected` (plain literal +
multi-fn → `validateFunName` emits `COMPILATION_ERROR`, an error-as-golden). Render the token via
`MemberName("me.tbsten.cream", "CopyTargetSimpleName")` so the input reads `"to" + CopyTargetSimpleName`
(a compile-time const). Live reference: `copyMapping/scenario/FunName.kt`.

## repeatable — multiple `@<Anno>` occurrences on one declaration
Add whenever the annotation is `@Repeatable`. This exercises the processor's multi-occurrence
collection + grouping/merging — **often the feature's only unique branch vs copyTo, and the easiest
to miss**. Cover the distinct processor branches, not just "two occurrences":
- **Group / merge**: `multipleAnnotations` (copyMapping: `holder.with<Anno>(a,b).with<Anno>(b,c)`, same
  package → ONE file, N functions) or `stackedAnnotations` (combineFrom: two `@CombineFrom` on one
  target → ONE merged function, sources flattened in order, first = receiver).
- **Dedup**: if the processor `.distinct()`s flattened references (combineFrom dedups sources to avoid
  the #101 "Conflicting declarations" duplicate-param bug), add a `duplicateSourceDeduped` case —
  the SAME reference in two occurrences → it appears ONCE in the params and `@see`. Stacking only
  *distinct* references never reaches the dedup branch (combineFrom shipped without it until review).
- **Cross-occurrence funName agreement**: if stacked occurrences must agree on `funName`, cover BOTH
  the accept path (`stackedAnnotationsSameFunName` → same name → applied) and the reject path
  (`conflictingFunNamesRejected` → different names → clean cream `COMPILATION_ERROR`).
- **Cross-package fan-out** (multiple emitted files, e.g. copyMapping's `groupBy { package }`) needs
  the multi-`FileSpec` overload → `EdgeUsageTest`, not the generator union (see SKILL.md gotchas).
Live references: `copyMapping/scenario/Repeatable.kt` (group/file-per-package),
`combineFrom/scenario/Repeatable.kt` (merge + dedup + funName accept/reject).

## multiSource — N sources merged into one function (combine features)
The defining axis for `@CombineTo`/`@CombineFrom`/`@CombineMapping`. Live reference:
`feature/combineTo/scenario/MultiSource.kt`. Cases: `twoSources` / `threeSources` (one source is the
receiver `this`, the others become leading params; each target param pulled from the correct distinct
source via `= this.x` / `= otherSource.y`; a param no source supplies → required), `overlappingProperty`
(a prop in 2+ sources → **last-listed source wins**, `asReversed().firstNotNullOfOrNull`).
- **Cross multiSource with the secondary features — this is where the combine-specific bugs live, NOT
  the bare axis.** `@Exclude` on an overlapping prop in *one* source must suppress the `= this.x`
  default in *every* generated function (a dedicated core branch: "exclude in ANY contributing
  source"); a single-source exclude case won't reach it. Same for `@Map` across sources (`= otherSource.x`
  rename) and generics across sources. Add at least the multiSource×exclude case (`excludeSuppressesAcrossSources`).
- **Combine target-validation quirk (#132)**: `appendCombineToFunction` lacks copy's
  `concreteClassRejection()`, so abstract / inner / private-ctor / annotation-class targets emit
  *uncompilable generated code* (kotlinc error on the GENERATED file) instead of a clean cream
  rejection — unlike copy. Capture as error-as-golden, mark with a protected `// TODO(#132)` comment
  in `TargetKind.kt`, do NOT fix the generator. Affects every combine feature (shared core).

## Utils.kt (not a family) — the annotation helper
```kotlin
internal fun TypeSpec.with<Anno>(referenced: ClassName, visibility: CopyVisibility? = null, kdoc: CodeBlock? = null): TypeSpec =
    toBuilder().addAnnotation(
        AnnotationSpec.builder(<Anno>::class).addMember("%T::class", referenced)
            .apply {
                if (visibility != null) addMember("${<Anno>::visibility.name} = %T.%L", CopyVisibility::class, visibility.name)
                if (kdoc != null) addMember("%L = %L", <Anno>::kdoc.name, kdoc)
            }.build(),
    ).build()

// First arg = the ANNOTATED/primary declaration (appears first in the generated file).
internal fun <anno>(primary: TypeSpec, referenced: TypeSpec, visibility: CopyVisibility? = null, kdoc: CodeBlock? = null): SnapshotScenario =
    SnapshotScenario(primary.with<Anno>(classNameOf(referenced.name!!), visibility, kdoc), referenced)
```
For combine/mapping the helper takes more referenced classes (vararg) and/or builds a holder; adapt
the shape but keep "first arg = the declaration that carries the annotation".

**Holder-annotated (mapping) form** — the annotated decl is a fixed boilerplate holder, so the helper
builds it and still takes it first. `@Map` is an annotation-arg array rendered as a `CodeBlock`:
```kotlin
internal fun mappingHolder(): TypeSpec = TypeSpec.objectBuilder("Mapping").build()

internal fun TypeSpec.withCopyMapping(
    source: ClassName, target: ClassName,
    canReverse: Boolean = false, properties: List<Pair<String, String>> = emptyList(),
    kdoc: CodeBlock? = null, funName: CodeBlock? = null,
): TypeSpec = toBuilder().addAnnotation(
    AnnotationSpec.builder(CopyMapping::class)
        .addMember("%T::class", source).addMember("%T::class", target)   // positional source, target
        .apply {                                                          // named, emitted only when non-default
            if (canReverse) addMember("%L = %L", CopyMapping::canReverse.name, true)
            if (properties.isNotEmpty()) addMember("%L = %L", CopyMapping::properties.name, propertiesBlock(properties))
            if (kdoc != null) addMember("%L = %L", CopyMapping::kdoc.name, kdoc)
            if (funName != null) addMember("%L = %L", CopyMapping::funName.name, funName)
        }.build(),
).build()

// @Map config array -> CodeBlock "[CopyMapping.Map(source = "a", target = "b"), …]"
private fun propertiesBlock(properties: List<Pair<String, String>>): CodeBlock =
    CodeBlock.builder().add("[").apply {
        properties.forEachIndexed { i, (s, t) ->
            if (i > 0) add(", "); add("%T(source = %S, target = %S)", CopyMapping.Map::class, s, t)
        }
    }.add("]").build()

internal fun copyMapping(holder: TypeSpec, source: TypeSpec, target: TypeSpec, /* …same opts… */): SnapshotScenario =
    SnapshotScenario(holder.withCopyMapping(classNameOf(source.name!!), classNameOf(target.name!!), /* … */), source, target)
```
For `@Repeatable`, chain `withCopyMapping(...)` twice on the same holder to get two annotations. Full
live source: `feature/copyMapping/scenario/Utils.kt` + `Repeatable.kt`.
