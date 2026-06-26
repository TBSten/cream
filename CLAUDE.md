# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

cream.kt is a Kotlin Symbol Processing (KSP) plugin that automatically generates cross-class copy functions. It enables easy state transitions across similar classes (e.g., `Loading` → `Success`) without manually copying shared properties.

**Published Artifacts:**
- `me.tbsten.cream:cream-runtime` - Runtime annotations (Multiplatform)
- `me.tbsten.cream:cream-ksp` - KSP processor (JVM)
- `me.tbsten.cream:cream-ksp-shared` - Shared utilities (Multiplatform)

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
├── cream-runtime/          # Runtime annotations (Multiplatform)
│   └── src/commonMain/kotlin/me/tbsten/cream/
│       ├── CopyTo.kt           # Source-side annotation
│       ├── CopyFrom.kt         # Target-side annotation
│       ├── CopyToChildren.kt   # Sealed class annotation
│       └── CopyMapping.kt      # Library-to-library mapping
├── cream-ksp/              # KSP processor (JVM only)
│   ├── src/main/kotlin/me/tbsten/cream/ksp/
│   │   ├── CreamSymbolProcessor.kt         # Composition root: dispatch all features
│   │   ├── CreamSymbolProcessorProvider.kt # KSP provider
│   │   ├── ProcessContext.kt               # {resolver, options, codeGenerator, logger}
│   │   ├── GenerateSourceAnnotation.kt     # Cross-cutting sealed type (source annotation)
│   │   ├── feature/                        # Per-annotation entry points (8 = one dir per annotation)
│   │   │   ├── copyTo/ copyFrom/ copyToChildren/ sealedCopy/
│   │   │   └── combineTo/ combineFrom/ copyMapping/ combineMapping/
│   │   │       # each: Process<Name>.kt with `context(ctx) fun processXxx()`
│   │   ├── core/                           # cream-specific code generation
│   │   │   ├── common/    # type params, where, property match, KDoc, naming, target validation, diagnostics
│   │   │   ├── copyFun/   # copy generation (class/object/sealed dispatch)
│   │   │   ├── combineFun/ # combine generation (N source -> 1 target)
│   │   │   └── sealedCopy/ # @SealedCopy generation
│   │   └── util/                           # Generic helpers only (no cream-specific types)
│   └── shared/             # Shared logic (Multiplatform, KSP-independent)
│       └── src/commonMain/kotlin/me/tbsten/cream/ksp/
│           ├── options/                    # Configuration
│           └── transform/CopyFunctionName.kt
├── test/                   # Integration tests (Multiplatform)
│   ├── src/commonMain/kotlin/me/tbsten/cream/test/
│   │   ├── copyTo/         # @CopyTo test data
│   │   ├── copyFrom/       # @CopyFrom test data
│   │   └── copyToChildren/ # @CopyToChildren test data
│   └── src/commonTest/kotlin/me/tbsten/cream/test/
└── optionBuilder/          # Configuration UI tool
```

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
- Shared building blocks (type params, `where`, property matching, KDoc, naming) live in `core/common/` and are composed by `copyFun`/`combineFun`/`sealedCopy`
- Naming strategy is applied via `core/common/` (bridging to `cream-ksp/shared`)

**Configuration System:**
- `CreamOptions` data class in `cream-ksp/shared` defines all options
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
- `GenerateSourceAnnotation` sealed interface tracks which annotation triggered generation
- Used in KDoc generation to reference source annotation
- Eight implementations: `CopyFrom`, `CopyTo`, `CopyToChildren`, `SealedCopy`, `CombineTo`, `CombineFrom`, `CopyMapping`, `CombineMapping`
- `when` over its subtypes must enumerate all branches (no `else`) so new annotations are caught by the compiler

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
[.claude/rules/ksp-test.md](.claude/rules/ksp-test.md) for the layout
(`testing/` / `diagnostic/` / `options/` / `snapshot/` / `architecture/`), the snapshot
regeneration command, and how to add new tests. The `architecture/` tests use
[Konsist](https://github.com/LemonAppDev/konsist) (not kctfork) to enforce the
feature/core/util layering documented in `.claude/rules/ksp-architecture.md`.

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
- `cream-ksp/shared`: JVM + JS + WASM

## Adding New Features

### Adding a New Annotation

See `.claude/rules/ksp-architecture.md` for the full architecture (feature/core/util boundaries, ProcessContext).

1. Define annotation in `cream-runtime/src/commonMain/kotlin/me/tbsten/cream/`
2. Add a sealed implementation to `GenerateSourceAnnotation` (in `ksp/GenerateSourceAnnotation.kt`, package `me.tbsten.cream.ksp`)
3. Add `feature/<name>/Process<Name>.kt` with `context(ctx: ProcessContext) fun processXxx(): List<KSAnnotated>` (discover -> validate -> call core; no generation logic in feature)
4. Reuse / extend generation logic under `core/` (`copyFun` / `combineFun` / `sealedCopy`, shared parts in `common`)
5. Register the dispatch in `CreamSymbolProcessor.process()`
6. Add test data in `test/src/commonMain/kotlin/me/tbsten/cream/test/newAnnotation/` and test cases in `test/src/commonTest/...`, plus snapshot/diagnostic tests in `cream-ksp` (see `.claude/rules/ksp-test.md`)

### Adding Configuration Options

1. Add property to `CreamOptions` in `cream-ksp/shared/src/commonMain/kotlin/me/tbsten/cream/ksp/options/`
2. Update parsing logic in `CreamOptions.kt` to read from KSP arguments
3. Use option in code generation logic (under `core/`; surfaced via the layered `context(options, ...)`)
4. Document in README.md under "Options" section

### Modifying Code Generation

Generation logic lives under `core/` (see `.claude/rules/ksp-core-top-level.md` for the sub-directory layout):

- **Function naming:** `core/common/` (bridging to `CopyFunctionName.kt` in cream-ksp/shared)
- **Per-declaration `funName` templates/tokens:** `FunctionNameTemplate.kt` (cream-ksp/shared) and the public token consts in `CopyFunctionNameToken.kt` (cream-runtime)
- **Property matching:** `core/common/`
- **Copy generation (class/object/sealed):** `core/copyFun/`
- **Combine generation:** `core/combineFun/`
- **`@SealedCopy` generation:** `core/sealedCopy/`
- **Type parameters / KDoc:** `core/common/`

## Key Files to Know

| Responsibility | Location |
|------|----------|
| KSP entry point & orchestration (`CreamSymbolProcessor`) | cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/ |
| Process context (`{resolver, options, codeGenerator, logger}`) | cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/ProcessContext.kt |
| Per-annotation entry points (`processXxx`) | cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/feature/<name>/ |
| Copy / combine / sealed-copy generation | cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/core/{copyFun,combineFun,sealedCopy}/ |
| Shared generation parts (naming, property matching, type params, KDoc) | cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/core/common/ |
| Generic helpers (no cream-specific types) | cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/util/ |
| Configuration parsing (`CreamOptions`) | cream-ksp/shared/src/commonMain/kotlin/me/tbsten/cream/ksp/options/CreamOptions.kt |

## Common Pitfalls

1. **Multiplatform KSP**: Don't expect KSP to work in `commonMain` without the workaround. Generated code only appears in platform-specific source sets by default.

2. **Property Matching**: The processor matches properties by name (or via `@Map` annotation). Ensure constructor parameter names align with source property names for automatic copying.

3. **Sealed Class Hierarchy**: `@CopyToChildren` is transitive — it generates copy functions to **all transitive concrete leaves** of the sealed hierarchy, recursing through any intermediate sealed types (not just direct children). You do not need multiple annotations or explicit `@CopyTo` for deeply nested sealed hierarchies.

4. **Generic Type Parameters**: When working with generics, ensure type parameters are properly preserved in generated functions. Check `CopyFunctionTypeParameters.kt` if issues arise.

5. **Visibility**: Generated copy functions respect the visibility of the target class constructor. Private constructors won't have accessible copy functions.

6. **Object Targets**: By default, copy functions to `object` types are generated (they just return the singleton). Use `cream.notCopyToObject=true` to suppress if undesired.
