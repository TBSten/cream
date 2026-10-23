# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

cream.kt is a Kotlin Symbol Processing (KSP) plugin that automatically generates cross-class copy functions. It enables easy state transitions across similar classes (e.g., `Loading` → `Success`) without manually copying shared properties.

**Published Artifacts:**
- `me.tbsten.cream:cream-runtime` - Runtime annotations (Multiplatform)
- `me.tbsten.cream:cream-ksp` - KSP processor (JVM)

## Development Commands

### Testing
```bash
# Run all tests (multiplatform)
./gradlew test

# Run tests for specific platform
./gradlew jvmTest
./gradlew iosSimulatorArm64Test
./gradlew androidDebugUnitTest

# Run tests for specific module
./gradlew :test:test
./gradlew :cream-ksp:test

# Architecture test (katachi): every file must belong to a declared role
./gradlew :architecture-test:test

# Generate per-role documentation from the same definition (architecture-test/build/katachi/docs/)
./gradlew :architecture-test:katachiDocs
```

### Building
```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :cream-ksp:build
./gradlew :cream-runtime:build

# Assemble without running tests
./gradlew assemble
```

### Code Quality
```bash
# Check formatting / lint (ktlint)
./gradlew ktlintCheck

# Format code
./gradlew ktlintFormat
```

### Publishing
```bash
# Publish to Maven Local for testing
./gradlew publishToMavenLocal

# Publish to Maven Central (requires credentials)
./gradlew publish
```

### KSP Development
```bash
# Test KSP code generation during development
./gradlew :test:kspCommonMainKotlinMetadata

# View generated code
ls test/build/generated/ksp/metadata/commonMain/kotlin/
```

## Architecture

### Module Structure

```
cream/
├── cream-runtime/      # Runtime annotations (Multiplatform)
├── cream-ksp/          # KSP processor (JVM only): composition root + feature / core / options / util
├── test/               # Integration tests (Multiplatform)
├── architecture-test/  # katachi architecture definition + the one test that checks it (JVM)
└── buildLogic/         # Convention plugins (included build)
```

**The architecture rules live in one place: the [katachi](https://tbsten.github.io/katachi/) definition in
`architecture-test/`.** It declares every kind of file in the repository (deny by default), cream-ksp's
layering and dependency table, naming, file-size budgets, test style and documentation rules, and
`./gradlew :architecture-test:test` enforces all of it. The same definition generates the readable pages in
[`doc/internal/architecture/`](doc/internal/architecture/README.md) — start there for where things live
(e.g. [cream-ksp](doc/internal/architecture/creamKsp/README.md), [cream-ksp tests](doc/internal/architecture/creamKspTest/README.md)).

### Key Components

**KSP Processing Flow:**
1. `CreamSymbolProcessorProvider` creates `CreamSymbolProcessor` instances and builds a `ProcessContext` (`{resolver, options, codeGenerator, logger}`)
2. `CreamSymbolProcessor.process()` dispatches to each feature's `processXxx()` (in `feature/<name>/`), which discovers annotations using `Resolver.getSymbolsWithAnnotation()`
3. Each feature (`feature/<name>/Process<Name>.kt`):
   - Extracts source and target classes
   - Validates annotation usage
   - Calls a `core/` generator (`appendXxx`) to build the copy function
4. Generated files written to `build/generated/ksp/`

**Code Generation Strategy:**
- `core/copyFun/` dispatches to specialized generators based on target type (regular class / object / sealed interface)
- `core/combineFun/` generates combine functions (N source -> 1 target); `core/sealedCopy/` generates `@SealedCopy` self-copy
- `core/parentOptional/` generates nullable extension-property accessors on sealed parents (`@ParentOptional` / `@ChildOptionals`)
- Shared building blocks (type params, `where`, property matching, KDoc, naming) live in `core/common/` and are composed by `copyFun`/`combineFun`/`sealedCopy`/`parentOptional`
- Naming strategy lives in `core/common/` (`CopyFunctionName.kt` / `FunctionNameTemplate.kt`)

**Configuration System:**
- `CreamOptions` data class in `cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/options/` defines all options
- Parsed from KSP build arguments in `build.gradle.kts`:
  ```kotlin
  ksp {
      arg("cream.copyFunNamePrefix", "copyTo")
      arg("cream.copyFunNamingStrategy", "under-package")
      arg("cream.escapeDot", "lower-camel-case")
      arg("cream.notCopyToObject", "false")
  }
  ```

### Important Patterns

**Annotation Tracking:**
- `GenerateSourceAnnotation` (in `core/common/`) identifies which annotation triggered a generation and carries that annotation's generation rules
- Exposes the generated function's metadata (KDoc / visibility / funName template) plus three overridable rules decided once per generated function, each with a "does nothing special" default:
  `warnedTargetExcludeAnnotation` / `warnedSourceExcludeAnnotation` (ineffective-`@Exclude` warnings) and `skipsObjectTarget` (`object` targets)
- The two PER-PARAMETER rules are deliberately NOT on the interface: `FindMappedSourceProperty` (`.Map` resolution) and `IsExcluded` (`@Exclude`) are standalone function types in `core/common/` that the generators take as ordinary parameters, so a caller not driving generation from an annotation can pass plain lambdas. cream's implementations expose them as properties of the same names
- Ten implementations, split by family: `CopySourceAnnotation.kt` (`CopyToSourceAnnotation` / `CopyFromSourceAnnotation` / `CopyToChildrenSourceAnnotation` / `SealedCopySourceAnnotation`), `CombineSourceAnnotation.kt` (`CombineToSourceAnnotation` / `CombineFromSourceAnnotation`), `MappingSourceAnnotation.kt` (`CopyMappingSourceAnnotation` / `CombineMappingSourceAnnotation`), `ParentOptionalSourceAnnotation.kt` (`ParentOptionalSourceAnnotation` / `ChildOptionalsSourceAnnotation` — accessor generation: every rule keeps its default and neither `findMappedSourceProperty` nor `isExcluded` is supplied)
- **Deliberately NOT sealed**: rules are resolved by polymorphic dispatch, never by an exhaustive `when` over the implementations, so an implementation defined outside the package plugs into the same generators

**Property Mapping:**
- `@CopyTo.Map("targetProperty")` and `@CopyFrom.Map("sourceProperty")` map mismatched property names
- `findMatchedProperty()` (in `core/common/`) resolves constructor parameters to source properties
- Default values generated when properties match by name

**Type Parameter Preservation:**
- Type-parameter handling (in `core/common/`) preserves generic type parameters
- Generates functions with same type parameters as target class
- Handles complex generic scenarios

**Error Handling:**
- `CreamException` hierarchy: `InvalidCreamUsageException`, `InvalidCreamOptionException`
- All exceptions include helpful solution messages
- Logger outputs link to GitHub issues for problem reporting

## Testing Approach

All tests use the [kotest](https://kotest.io) `FreeSpec` style (`class XxxTest : FreeSpec({ "..." { ... } })`,
nested groups via `"group" - { "..." { ... } }`) with kotest matchers (`actual shouldBe expected`,
`shouldBeInstanceOf`, `shouldContain`, …). The JVM
(`cream-ksp`, `test` jvm) and Android unit-test tasks run via the JUnit Platform (`kotest-runner-junit5`
+ `useJUnitPlatform()`); native/`commonTest` runs via `kotest-framework-engine` and the `io.kotest`
KSP plugin (which generates a per-target spec launcher). Note for native: spec classes must have unique
simple names across packages on older kotest, but 6.1.x emits aliased imports so duplicates are fine.

### Integration Tests (`test/` module)

The `test/` module uses KSP to generate code from test data and verifies the generated functions work correctly:

1. **Test Data** (`src/commonMain/`): Classes annotated with `@CopyTo`, `@CopyFrom`, `@CopyToChildren`
2. **KSP Generation**: Build process generates copy functions
3. **Test Verification** (`src/commonTest/`): Tests call generated functions and verify behavior

**Example Test Structure:**
```kotlin
// test/src/commonMain/kotlin/me/tbsten/cream/test/copyTo/CopyToClasses.kt
@CopyTo(Target::class)
data class Source(val prop: String)
data class Target(val prop: String, val extra: Int)

// test/src/commonTest/kotlin/me/tbsten/cream/test/copyTo/CopyToTest.kt
class CopyToTest : FreeSpec({
    "testCopyFunction" {
        val source = Source("value")
        val target = source.copyToTarget(extra = 42)
        target.prop shouldBe "value"
        target.extra shouldBe 42
    }
})
```

### Unit Tests (`cream-ksp/src/test/`)

Unit tests for processor logic (naming strategies, property matching, etc.):
```bash
./gradlew :cream-ksp:test
```

### KSP Compilation Tests (`cream-ksp/src/test/` with kctfork)

JVM-only end-to-end tests built on kctfork live under
`cream-ksp/src/test/kotlin/me/tbsten/cream/ksp/`. See
[doc/internal/architecture/creamKspTest/](doc/internal/architecture/creamKspTest/README.md) for the
layout (the five tests of every feature, `testing/`, snapshot golden files), the snapshot
regeneration command, and how to add new tests.

### Architecture Test (`architecture-test/` with katachi)

[katachi](https://tbsten.github.io/katachi/) checks **where every file in the repository may live**.
The definition (`architecture { }` in `architecture-test/`) declares one Role per kind of file; it is
deny by default, so a file no Role declares fails `./gradlew :architecture-test:test` as
`[UnexpectedFile]`. Roles also carry `konsist { }` / `fileConstraint { }` rules: cream-ksp's dependency
table, the feature entry-point signature, the per-file line budget, package follows directory, tests are
kotest `FreeSpec`, every user doc has its `.ja.md` while skills stay English only.

When adding a new **kind** of file (not just one more file of an existing kind), add a Role for it —
see [.claude/rules/architecture-test.md](.claude/rules/architecture-test.md). Adding a new annotation
needs no change: `feature/*/`, `test/.../*/` and the feature test roles are wildcards.

## Multiplatform Considerations

**KSP Limitation:** KSP doesn't support intermediate source sets (e.g., `commonMain`) directly. The `test/` module uses a workaround:
```kotlin
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
```

**Supported Platforms:**
- `cream-runtime`: All Kotlin platforms (iOS, JVM, Android, JS, WASM, Linux, macOS, watchOS, tvOS)
- `cream-ksp`: JVM only (KSP limitation)

## Adding New Features

### Adding a New Annotation

See [doc/internal/architecture/creamKsp/](doc/internal/architecture/creamKsp/README.md) for the full architecture (feature/core/util boundaries, ProcessContext).

1. Define annotation in `cream-runtime/src/commonMain/kotlin/me/tbsten/cream/`
2. Add a `GenerateSourceAnnotation` implementation named `<Name>SourceAnnotation` in `core/common/` (package `me.tbsten.cream.ksp.core.common`), overriding only the rules the annotation needs and exposing `findMappedSourceProperty` / `isExcluded` properties when it has `.Map` / `@Exclude` semantics — do NOT add a `when` over the implementations
3. Add `feature/<name>/Process<Name>.kt` with `context(ctx: ProcessContext) fun processXxx(): List<KSAnnotated>` (discover -> validate -> call core; no generation logic in feature)
4. Reuse / extend generation logic under `core/` (`copyFun` / `combineFun` / `sealedCopy` / `parentOptional`, shared parts in `common`)
5. Register the dispatch in `CreamSymbolProcessor.process()`
6. Add test data in `test/src/commonMain/kotlin/me/tbsten/cream/test/newAnnotation/` and test cases in `test/src/commonTest/...`, plus snapshot/diagnostic tests in `cream-ksp` (see [doc/internal/architecture/creamKspTest/](doc/internal/architecture/creamKspTest/README.md))

### Adding Configuration Options

1. Add property to `CreamOptions` in `cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/options/`
2. Update parsing logic in `CreamOptions.kt` to read from KSP arguments
3. Use option in code generation logic (under `core/`; surfaced via the layered `context(options, ...)`)
4. Document in README.md under "Options" section

### Modifying Code Generation

Generation logic lives under `core/` (see [doc/internal/architecture/creamKsp/core/](doc/internal/architecture/creamKsp/core/README.md) for the sub-directory layout):

- **Function naming:** `core/common/CopyFunctionName.kt`
- **Per-declaration `funName` templates/tokens:** `core/common/FunctionNameTemplate.kt` and the public token consts in `CopyFunctionNameToken.kt` (cream-runtime)
- **Property matching:** `core/common/`
- **Copy generation (class/object/sealed):** `core/copyFun/`
- **Combine generation:** `core/combineFun/`
- **`@SealedCopy` generation:** `core/sealedCopy/`
- **`@ParentOptional` / `@ChildOptionals` accessor generation:** `core/parentOptional/`
- **Type parameters / KDoc:** `core/common/`

## Key Files to Know

Where each kind of file lives is declared (and checked) by the katachi definition; see the generated
[doc/internal/architecture/README.md](doc/internal/architecture/README.md) document map.

## Common Pitfalls

1. **Multiplatform KSP**: Don't expect KSP to work in `commonMain` without the workaround. Generated code only appears in platform-specific source sets by default.

2. **Property Matching**: The processor matches properties by name (or via `@Map` annotation). Ensure constructor parameter names align with source property names for automatic copying.

3. **Sealed Class Hierarchy**: `@CopyToChildren` is transitive — it generates copy functions to **all transitive concrete leaves** of the sealed hierarchy, recursing through any intermediate sealed types (not just direct children). You do not need multiple annotations or explicit `@CopyTo` for deeply nested sealed hierarchies.

4. **Generic Type Parameters**: When working with generics, ensure type parameters are properly preserved in generated functions. Check `CopyFunctionTypeParameters.kt` if issues arise.

5. **Visibility**: Generated copy functions respect the visibility of the target class constructor. Private constructors won't have accessible copy functions.

6. **Object Targets**: By default, copy functions to `object` types are generated (they just return the singleton). Use `cream.notCopyToObject=true` to suppress if undesired.
