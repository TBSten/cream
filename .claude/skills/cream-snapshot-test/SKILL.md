---
name: cream-snapshot-test
description: >-
  Build a golden snapshot test for a cream.kt KSP feature/annotation by horizontally
  extending the established @CopyTo / @CopyFrom pattern. Use whenever the user wants to add,
  build, or "横展開" (roll out) a `<Feat>SnapshotTest` for a cream annotation —
  @CopyToChildren, @SealedCopy, @CombineTo, @CombineFrom, @CopyMapping, @CombineMapping (or any
  cream annotation) — including replacing a `<Feat>SnapshotTest` xtest stub, building scenario
  families, or getting golden coverage for cream's generated copy/combine functions. Trigger even
  if the user just names a feature and says "add the snapshot test" without mentioning copyTo or
  copyFrom. This is for the cream.kt repo (me.tbsten.cream KSP plugin) specifically.
---

# cream snapshot test 横展開

cream is a KSP plugin that generates cross-class copy/combine functions from annotations. Each
feature (`@CopyTo`, `@CopyFrom`, …) gets a `<Feat>SnapshotTest` that compiles many small
input programs and golden-compares the generated output. **All eight features are now built**
(copyTo, copyFrom, copyMapping, copyToChildren, combineTo, combineFrom, sealedCopy, combineMapping)
— so any of them is a live reference. Pick the closest annotation-site archetype: **copyTo**
(source-annotated), **copyFrom** (target-annotated), **copyMapping** (holder-annotated),
**copyToChildren/sealedCopy** (sealed-parent), **combineTo/combineFrom/combineMapping** (N→1 combine).
This skill now applies to *re-running, extending, or auditing* an existing suite, or adding a snapshot
test for a brand-new annotation.

Your job: build the snapshot test for one feature by mirroring the reference pattern, *correctly
adapted* to that feature's structure. The generation core is shared across features, so for
structurally-equivalent inputs the generated function is byte-identical — only the input
annotation and the generated file name differ. That makes the reference code a precise template
and a built-in correctness check.

## The two non-negotiables

1. **Reuse the shared infrastructure — never reinvent it.** Everything under
   `cream-ksp/src/test/kotlin/me/tbsten/cream/ksp/testing/` is feature-independent and was reused
   for copyFrom with zero changes. You will only write per-feature `scenario/` files and the test
   class. If you find yourself writing a KotlinPoet class builder, a compile harness, or an
   options generator, stop — it already exists.

2. **Output-preserving by construction.** Goldens are keyed by test-class name + test name, not by
   your scenario package. After generating goldens, re-run the test *without* the update flag and
   it must pass. Reject/error cases are captured as `COMPILATION_ERROR` goldens — that is correct
   and expected ("error-as-golden"). Do not hand-edit goldens.

## Reference implementations — read these first

The canonical templates are live code (so they never drift from the framework). Before writing
anything, read the closest reference for your feature's annotation site:

- **Source-annotated** (annotation on the source class, target referenced via `::class`):
  `cream-ksp/src/test/kotlin/me/tbsten/cream/ksp/feature/copyTo/scenario/` + `copyTo/CopyToSnapshotTest.kt`
- **Target-annotated** (annotation on the target class, source referenced via `::class`):
  `cream-ksp/src/test/kotlin/me/tbsten/cream/ksp/feature/copyFrom/scenario/` + `copyFrom/CopyFromSnapshotTest.kt`
- **Holder-annotated** (annotation on a boilerplate holder, source+target both referenced via
  `::class` annotation args; `@Repeatable`; annotation-arg `@Map`; no `visibility` arg):
  `cream-ksp/src/test/kotlin/me/tbsten/cream/ksp/feature/copyMapping/scenario/` + `copyMapping/CopyMappingSnapshotTest.kt`
  — the live reference for `@CombineMapping` and the closest one for any holder/library-mapping feature.

Also skim the project rules: `.claude/rules/ksp-test.md` (test layout, snapshot format, update
command) and `.claude/rules/ksp-architecture.md` (layering).

## Process

### 1. Analyze the feature's processor — this drives everything

Read `cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/feature/<name>/Process<Name>.kt` and the
runtime annotation `cream-runtime/src/commonMain/kotlin/me/tbsten/cream/<Name>.kt`. Extract:

- **Annotation site**: which class carries the annotation (source / target / sealed parent /
  mapping holder) and which class(es) are referenced. This decides which reference you mirror and
  the argument order of your scenario helper (see step 3).
- **Cardinality / archetype** (see `references/feature-profiles.md` for the per-feature answer):
  1→1 copy, 1→N sealed fan-out, sealed self-copy, N→1 combine, or library mapping.
- **`@Map` / `@Exclude` placement**: which declaration they sit on (source props? target params?
  abstract props on a sealed parent? annotation args?) and what their arguments mean. This is the
  most common thing to get subtly wrong when mirroring.
- **Generated file name** (`fileName = "..."`) and the **multi-function condition**
  (`generatesMultipleFunctions = ...`) — the latter tells you when `funName` needs a token, which
  is what a multi-source/multi-target scenario exercises.
- **`@Repeatable` + output grouping**: is the annotation `@Repeatable`, and does the processor
  collect multiple occurrences and group them (e.g. `groupBy { sourceClass.packageName }` → one
  emitted file per group)? This multi-occurrence path is frequently the feature's **only** unique
  processor branch versus copyTo/copyFrom and the single easiest axis to miss — if present, plan a
  `repeatable` family (see step 2). copyMapping/combineFrom/combineMapping/sealedCopy are all
  `@Repeatable`.
- **Core generator** invoked (`appendCopyFunction` / `appendCombineToFunction` /
  `appendSealedCopyFunction`). Same core as a reference ⇒ same generated output for equivalent
  inputs. This equivalence is also a **verification lever** — see the byte-identity cross-check in
  step 6.

`references/feature-profiles.md` has this pre-computed for all the remaining features — read the
profile for your target feature, but confirm against the live processor (it is the source of
truth).

### 2. Choose the family set

The reference features use 11 families. Each names an **axis of variation**, not a fixed
template — keep the ones that apply, drop or adapt the ones that don't, and add feature-specific
ones. See `references/families.md` for what each family covers and how to adapt per archetype.

| # | family | axis |
|---|--------|------|
| 00 | sourceKind | declaration kind of the receiver/source (value class / plain / sealed) |
| 01 | targetKind | ClassKind dispatch on the target (object→singleton, sealed→fan-out, abstract/enum/non-sealed-iface/inner/private-ctor→reject) |
| 02 | nesting | declaration nesting + FQ-name resolution from the annotation site |
| 03 | generics | type-param merge, bounds→`where` |
| 04 | constructor | per-constructor generation, `vararg` |
| 05 | propertyShape | property count/type rendering (source==target full match) |
| 06 | matching | property matching / type compatibility |
| 07 | map | `@<Anno>.Map` |
| 08 | exclude | `@<Anno>.Exclude` (+ its no-effect warning) |
| 09 | kdoc | `@<Anno>(kdoc = ...)` |
| 10 | visibility | `@<Anno>(visibility = ...)` + class/property visibility |

Combine / mapping / sealed features deviate: e.g. there is no single "target" to vary for
`@CopyToChildren` (it fans out), `@CombineTo` has an N-sources axis instead of `sourceKind`, and
mapping features put `@Map`/`@Exclude` in annotation args, not on class members. Tailor honestly;
note in your report where you intentionally diverge from the 11.

**Feature-specific families to ADD (copyTo/copyFrom don't have these — add when the feature needs them):**

| family | add when | cases |
|--------|----------|-------|
| `funName` | `generatesMultipleFunctions` can be true (canReverse / sealed target / multi-target/source) | `literalFunName` (single fn, plain literal OK) · `tokenFunName` (`"to" + CopyTargetSimpleName`) · `tokenFunName<Multi>` (token → distinct names) · `literalFunName<Multi>Rejected` (literal + multi-fn → `COMPILATION_ERROR`) |
| `repeatable` | annotation is `@Repeatable` | `multipleAnnotations` — two+ occurrences on one declaration in the same group → one emitted file, N functions (chain `with<Anno>(...).with<Anno>(...)`). This is often the **only** unique branch; nearly missed for copyMapping. |
| `multiSource` | N→1 combine (`@CombineTo`/`@CombineFrom`/`@CombineMapping`) | 2+ sources merged into one function — the defining combine axis |

`funName` was historically treated as a "coverage hole"; for any multi-function feature it is
**required**, because the multi-fn name-collision guard (`validateFunName`) is core behavior. The
`<Multi>Rejected` case is an error-as-golden (`COMPILATION_ERROR`), which is correct. **Even the 1→1
`@CopyTo`/`@CopyFrom` need it**: their `generatesMultipleFunctions = targets/sources.size>1 ||
sealed-target`, so a literal funName + a SEALED target hits the reject branch (`literalFunNameSealedRejected`).
`@CopyToChildren` is the ONLY feature with no `funName` arg → no `funName` family.

**Document intentional omissions.** When a feature deliberately drops a family or a case (typealias,
redundant constructor/nesting, an option-fallback that's byte-identical, a cross-package multi-file
path the single-package generator can't express, …), record it as a `/** … */` KDoc on the
`<Feat>SnapshotTest` class — a bulleted "Intentionally NOT covered (and why)" list. All 8 suites carry
one; mirror their format. (Avoid `[Brackets]` in that KDoc — they parse as KDoc links and warn.)

### 3. Build the `scenario/` package

One file per family, in
`cream-ksp/src/test/kotlin/me/tbsten/cream/ksp/feature/<name>/scenario/` (package
`…feature.<name>.scenario`). Mirror the reference file for each family, flipping the annotation to
your feature. Conventions, learned the hard way:

- **`Utils.kt`** holds the shared annotation helper: `TypeSpec.with<Anno>(referenced, visibility?, kdoc?)`
  and `<anno>(primary, referenced, …): SnapshotScenario`. **The first positional arg is always the
  ANNOTATED/primary declaration** (it appears first in the generated file): `copyTo(source, target)`
  annotates the source; `copyFrom(target, source)` annotates the target. Match the idiom of real
  usage for your feature.
  - **Holder-annotated (mapping) feature**: the annotated declaration is a fixed boilerplate holder.
    Add a `mappingHolder(): TypeSpec` (= `object Mapping`) helper and keep it as the first arg —
    `copyMapping(holder, source, target, …)`. Build source/target as positional `%T::class` members
    and everything else (`canReverse`/`properties`/`kdoc`/`funName`) as named members, emitted only
    when non-default. Holder visibility is irrelevant (the generated fn inherits the **target's**
    visibility), so a neutral public holder is fine. Live reference: `feature/copyMapping/scenario/`.
  - **Annotation-arg `@Map`** (mapping features): render the `properties` array as a `CodeBlock`,
    not a member annotation — `[CopyMapping.Map(source = %S, target = %S), …]` via a small
    `propertiesBlock(List<Pair<String,String>>)` helper (see `copyMapping/scenario/Utils.kt`).
  - **No `visibility` arg?** Check the annotation's constructor — do NOT trust the profile. `@CopyMapping`
    has NO `visibility` arg (drop the `visibilityOverride*` cases; keep `internalTargetClass` +
    `propertyVisibilities`, which prove the fn inherits the target's visibility). But `@CombineMapping`
    DOES have `visibility` (every other cream annotation does too) — keep the full family. A stale profile
    claimed `@CombineMapping` had none; the live runtime is the source of truth.
- Family functions are `internal fun <family>Scenarios(): Generator<SnapshotScenario>`, returning
  `Generator.snapshotScenarios("caseName" to <anno>(...), …)`. Family-specific helpers are `private`.
- **Reuse the poet builders** unchanged: `classNameOf`, `Prop`, `clazz`, `dataClass`,
  `sealedInterface`, `asInner`, `containing`, `classWithNested`, `SnapshotScenario`,
  `snapshotScenarios` (all in `testing/poet/`).
- **No design-rationale comments.** The reference scenario files are comment-free; keep it that way
  (the maintainer strips them otherwise). Let the case names and structure speak.
- **Spelling**: the package is `scenario` (not `snapshotsenario` / `senario`).

### 4. Write the inline `<Feat>SnapshotTest`

Use the **inline** form — do NOT extract a shared `snapshotMatrix` helper (the maintainer
considered and rejected that). Copy `CopyFromSnapshotTest.kt` verbatim and swap the package +
family imports. The exact skeleton:

```kotlin
internal class <Feat>SnapshotTest :
    FreeSpec({
        "All patterns" - {
            cartesian(
                union {
                    withNumberPrefix(length = 2) {
                        "sourceKind" case sourceKindScenarios()
                        // … one line per family, in NN order …
                    }
                },
                Generator.validCreamOptions(),
                label = { scenarioLabel, optionsLabel -> "option=$optionsLabel/$scenarioLabel" },
            ).representativeValues()
                .forEach { (testCaseName, value) ->
                    val (scenario, creamOptions) = value
                    testCaseName!! {
                        runCompileSnapshotTest(inputs = scenario.toFileSpecs(), options = creamOptions)
                    }
                }
        }
    })
```

Family order = `NN--` numbering = golden directory layout, so keep it deliberate and stable.

### 5. Generate goldens, then verify output-preserving

Save command logs under `.local/tmp/<time>-<cmd>.log` (project convention). From the repo root:

```bash
# generate goldens (first run writes them)
./gradlew :cream-ksp:test --tests '*.<Feat>SnapshotTest' -Dcream.snapshot.update=true
# verify deterministic / output-preserving (NO update flag → must pass)
./gradlew :cream-ksp:test --tests '*.<Feat>SnapshotTest'
# lint + module-wide Konsist (line limits, layering)
./gradlew :cream-ksp:ktlintTestSourceSetFormat :cream-ksp:ktlintTestSourceSetCheck \
          :cream-ksp:test --tests '*.AllKotlinFilesTest'
```

Budget ≈ a few dozen scenarios × 3 option variants ≈ ~100–120 compiles ≈ ~40s per feature (KSP2 in
kctfork is ~0.35s/compile). Finally run the whole `:cream-ksp:test` once for a clean signal.

### 6. Spot-check goldens + byte-identity cross-check; surface bugs, don't fix generation

Open a handful of goldens (one generate case, one reject, the `@Map`/`@Exclude`/sealed cases) and
confirm the output is sensible. The snapshot's whole value is surfacing latent generation quirks —
when one appears (copyFrom surfaced a real KDoc misattribution on sealed targets), **file a GitHub
issue, do not change the generator**. Capturing current behavior in the golden and reporting it is
the correct outcome for this task; fixing generation is a separate, opt-in change.

**Byte-identity cross-check (strongest verification — do this).** Because the generation core is
shared, any scenario *structurally equivalent* to a copyTo/copyFrom case must produce a
`## Output:Generated sources` facet that is **byte-identical to the reference golden except exactly
two lines**: the `// file: <Feat>__<x>.kt` line and the `(Auto generate by @[<Anno>] annotation of
[<x>])` KDoc line. Diff them — *any other* difference is either a scenario-construction bug on your
side or a real generation quirk to report. Mechanically:

```bash
strip() { sed -n '/## Output:Generated sources/,$p' "$1" | grep -vE '^// file:|Auto generate by @\['; }
diff <(strip "<your golden>.md") <(strip "<equivalent copyTo golden>.md")   # expect: no diff
```

Cross-check the structurally-shared families (sourceKind/targetKind/generics/constructor/
propertyShape/matching/kdoc, and the comparable visibility cases) against copyTo or copyFrom. This
caught a real authoring nit on copyMapping (a divergent kdoc-description string) and proved the
reject cases match. Reject (`COMPILATION_ERROR`) cases: also diff `Output:ExitCode` + `Output:Console`
(only the `Input.kt:NN:` line number may differ, since your input has extra declarations). Families
with no copyTo/copyFrom analogue (`map` config-array, `canReverse`, `funName`, `repeatable`,
`multiSource`) can't be cross-diffed — verify those for internal consistency instead.

**Before reporting a generation "quirk/inconsistency", FALSIFY it against the sibling's SAME input.**
A quirk claim ("feature X renders `<T : Any?>` but copy renders `<T>`") is only real if copy/copyFrom
produces *different* output for the *equivalent* input — diff them. sealedCopy was reported to have a
`<T : Any?>` inconsistency; copyTo renders `<T : Any?>` identically (same shared rendering code), so it
was a non-issue — do NOT file. The byte-identity check is a quirk *falsifier*, not just a coverage
tool. Only genuinely-divergent, genuinely-wrong output (e.g. combine's uncompilable abstract-target
code that copy rejects cleanly) warrants a `// TODO(#NNN)` + issue.

### 7. Self-review against the checklist

Before declaring done, audit the final state against this checklist. Every item is **objectively
checkable from the diff, the repo, and the test output** — so a *separate* reviewer agent can re-run
it without having watched you work. For each item record **PASS / FAIL with concrete evidence** (a
file path, a command's output, a golden excerpt). If anything FAILs, fix it and re-verify; never
report done with an unresolved FAIL. Run the listed command where one is given.

**Must hold — required (a missing one of these means the work is incomplete):**

- [ ] **Processor was analyzed**: the report states this feature's annotation site, archetype,
  `@Map`/`@Exclude` placement, generated file name, and multi-function condition — each traceable to
  `Process<Name>.kt`. A scenario suite written without these is guesswork.
- [ ] **Infra reused, not rebuilt**: every `scenario/*.kt` imports builders from
  `me.tbsten.cream.ksp.testing.{poet,generator,compile}`, and the feature dir defines no class
  builder / compile harness / options generator. Check: `git diff --stat` touches only
  `cream-ksp/.../feature/<name>/**` and `cream-ksp/src/test/resources/snapshots/<Feat>SnapshotTest/**`.
- [ ] **Structure matches a reference**: `scenario/` package, one file per family, each
  `internal fun <family>Scenarios(): Generator<SnapshotScenario>`; `Utils.kt` has the
  `with<Anno>`/`<anno>` helper with the **annotated/primary declaration as the first arg**; the test
  is the inline `FreeSpec({ "All patterns" - { … } })` skeleton with family order = `NN--` order.
- [ ] **Families fit the archetype**: if this is not a 1→1 copy, the report names which of the 11
  families were dropped / recast / added and why (multiSource for combine, hierarchy-shape for
  sealed, annotation-arg `@Map` for mapping), cross-checked against `references/feature-profiles.md`.
  Blindly mirroring all 11 copy families onto a combine/mapping/sealed feature is a FAIL.
- [ ] **Output-preserving**: `./gradlew :cream-ksp:test --tests '*.<Feat>SnapshotTest'` (NO update
  flag) is BUILD SUCCESSFUL — goldens are deterministic.
- [ ] **Lint + arch + full suite green**: `:cream-ksp:ktlintTestSourceSetCheck`, the
  `AllKotlinFilesTest` Konsist test, and the whole `:cream-ksp:test` all pass.
- [ ] **Goldens spot-checked**: a generate case, a reject case (→ `COMPILATION_ERROR`), and the
  `@Map`/`@Exclude`/sealed cases were opened and are correct — `@Map` wires `= this.<src>` in the
  direction the processor matches; `@Exclude` drops the default; `excludeNoEffect` shows the
  `w: @Exclude … has no effect` warning in the Console facet.
- [ ] **Byte-identity cross-check done**: each structurally-shared family was diffed against the
  equivalent copyTo/copyFrom golden and is identical modulo the `// file:` + KDoc-attribution lines
  (step 6). Any residual diff was explained (authoring choice) or reported (generation quirk). When
  the archetype has no structural twin (e.g. copyToChildren's sealed-parent receiver), substitute a
  characterizing diff PLUS explicit behavioral assertions (next item).
- [ ] **Behavioral (not just structural) coverage**: the feature's README headline behavior is
  exercised by at least one golden — for copy-family features that means a *matched* property
  rendering `= this.x` AND a function mixing matched-defaulted + required params. Structural variety
  (hierarchy shapes, property shapes, kinds) can leave the defining behavior unsnapshotted:
  copyToChildren shipped with **zero `= this.x`** across every hierarchy case until review caught it
  (the receiver/parent had no shared prop the children overrode). `grep '= this\.' <goldens>` should
  be non-empty and hit the feature's core path (e.g. a transitive child for a fan-out feature).
- [ ] **`@Repeatable` covered (if applicable)**: if the annotation is `@Repeatable`, the suite has a
  `repeatable` family exercising multiple occurrences on one declaration (the processor's
  multi-occurrence / per-group file emission — often its only unique branch). N/A otherwise.
- [ ] **Quirks reported, not patched**: if any golden shows wrong generated output, a GitHub issue
  was filed and linked, and the generator was left untouched.

**Must NOT hold — anti-patterns (any present is a FAIL):**

- [ ] **Generator edited**: `git diff cream-ksp/src/main/` is non-empty. Fixing generation is out of
  scope — capture-and-report instead.
- [ ] **A `snapshotMatrix` / shared driver was introduced**: `grep -rn snapshotMatrix cream-ksp/src/test`
  finds anything. The test body must be inline (the maintainer rejected that extraction).
- [ ] **Goldens hand-edited**: any golden differs from what the `-Dcream.snapshot.update=true` run
  produced, i.e. the no-flag verify would not pass clean.
- [ ] **Design-rationale comments in `scenario/`**: `grep -rnE '^[[:space:]]*//' <scenario dir>`
  returns anything other than protected `// TODO` / links. Case names + structure carry the meaning.
- [ ] **Misspelled package**: the dir is `snapshotsenario` or `senario` instead of `scenario`.
- [ ] **Double-snapshot**: the test calls `assertMatchesSnapshot` directly (`runCompileSnapshotTest`
  already emits every facet).
- [ ] **`@Map`/`@Exclude` on the wrong side**: their placement does not match this annotation's
  `findMatchedProperty` / `isExcludedFromCopy` branch in the processor (e.g. copy-pasted source-side
  placement onto a target-annotated feature).
- [ ] **Stray artifacts in the diff**: `.local/` logs or `*.kt.tmp` files are part of the change.

## Reporting back

Summarize: which families you used and any intentional divergence from the 11; the scenario/golden
counts; the verification results (output-preserving ✓, ktlint ✓, Konsist ✓); any generation quirks
the snapshots surfaced (with an issue link if you filed one); and the **Step 7 checklist results**
(PASS/FAIL per item with evidence) so a reviewer can confirm the gate without redoing the analysis.

## Gotchas

- **zsh doesn't word-split unquoted `$var`** — a `for f in $FILES` over multiline output feeds one
  giant blob. Use `find … | while IFS= read -r f; do …; done` for per-file loops.
- **Konsist `AllKotlinFilesTest`** enforces per-file line limits; keep scenario files small (the
  reference files are 18–103 lines). It only scopes `main` for layering, but checks all files for
  line count.
- **Don't double-snapshot**: `runCompileSnapshotTest` already emits all facets; don't also call
  `assertMatchesSnapshot` in the same test.
- **`COMPILATION_ERROR` provenance — cream's rejection or kotlinc's?** Error-as-golden is correct
  ONLY when cream *intentionally rejects* a valid-Kotlin-but-unsupported target (abstract / enum /
  non-sealed interface / private-ctor target, multi-fn + literal funName, …). If a *generate* case
  unexpectedly yields `COMPILATION_ERROR`, read the Console facet: a **kotlinc language error** (e.g.
  `Modifier 'internal' is not applicable inside 'interface'`) means your scenario INPUT is illegal
  Kotlin — a scenario bug to fix, NOT a golden to keep. copyToChildren shipped an
  `internal`-data-class-nested-in-`interface` case that was an illegal-input error masquerading as a
  visibility test (fix: make the child a top-level sibling, or use a sealed-class parent).
- **`SnapshotScenario` groups declarations into `ScenarioFile`s (one per package).** The
  single-package convenience (`SnapshotScenario(vararg/List<TypeSpec>)`) puts every decl in one
  `GENERATED_PACKAGE` file; `toFileSpecs()` emits one `FileSpec` per `ScenarioFile`. So: (1)
  **cross-package** output (source/target in a different package than the holder) IS expressible —
  pass multiple `ScenarioFile`s
  (`SnapshotScenario(files = listOf(ScenarioFile("a", …), ScenarioFile("b", …)))`), which
  `runCompileSnapshotTest(inputs = scenario.toFileSpecs())` compiles as separate files. The curated
  generator union stays single-package, so a cross-package case is a hand-written scenario (or an
  `EdgeUsageTest` case), not part of the union; a *same-package* `repeatable` case fits the union
  fine. (2) **`typealias`** referencing — a Kotlin `typealias` is a KotlinPoet `TypeAliasSpec`, not a
  `TypeSpec`, so a `ScenarioFile`'s `List<TypeSpec>` can't carry it; alias resolution is generic and
  already integration-tested under `test/.../<name>/TypeAlias*.kt`.

## Reference files

- `references/feature-profiles.md` — per-feature structural profile (annotation site, cardinality,
  @Map/@Exclude, file name, multi-fn condition, core generator, existing test data) for the
  remaining features (copyMapping ✅ done as a live reference) + the archetype table. Read the one for your target feature.
- `references/families.md` — what each of the 11 copy families covers (+ the feature-specific
  `funName` / `repeatable` / `multiSource` additions), with reference case lists and how to adapt
  per archetype (combine / mapping / sealed).
