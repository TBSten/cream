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
# Run linting
./gradlew detekt

# Format code
./gradlew spotlessApply

# Check formatting
./gradlew spotlessCheck
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
│   │   ├── CreamSymbolProcessor.kt         # Main entry point
│   │   ├── CreamSymbolProcessorProvider.kt # KSP provider
│   │   ├── transform/                      # Code generation
│   │   │   ├── Transform.kt                # Dispatcher
│   │   │   ├── Class.kt                    # Class generation
│   │   │   ├── Object.kt                   # Object generation
│   │   │   ├── SealedClass.kt              # Sealed generation
│   │   │   ├── CopyFunctionNameExt.kt      # Naming logic
│   │   │   └── FindMatchedProperty.kt      # Property matching
│   │   └── util/                           # KSP utilities
│   └── shared/             # Shared logic (Multiplatform)
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
1. `CreamSymbolProcessorProvider` creates `CreamSymbolProcessor` instances
2. `CreamSymbolProcessor.process()` discovers annotations using `Resolver.getSymbolsWithAnnotation()`
3. For each annotation type (`@CopyTo`, `@CopyFrom`, `@CopyToChildren`):
   - Extract source and target classes
   - Validate annotation usage
   - Generate copy functions via `Transform.appendCopyFunction()`
4. Generated files written to `build/generated/ksp/`

**Code Generation Strategy:**
- `Transform.kt` dispatches to specialized generators based on target type:
  - `appendCopyToClassFunction()` for regular classes
  - `appendCopyToObjectFunction()` for objects
  - `appendCopyToSealedInterfaceFunction()` for sealed interfaces
- Property matching: `FindMatchedProperty.kt` maps source properties to constructor parameters
- Naming: `CopyFunctionNameExt.kt` applies user-configured naming strategy

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
- Three implementations: `CopyFrom`, `CopyTo`, `CopyToChildren`

**Property Mapping:**
- `@CopyTo.Map("targetProperty")` and `@CopyFrom.Map("sourceProperty")` map mismatched property names
- `findMatchedProperty()` resolves constructor parameters to source properties
- Default values generated when properties match by name

**Type Parameter Preservation:**
- `CopyFunctionTypeParameters.kt` preserves generic type parameters
- Generates functions with same type parameters as target class
- Handles complex generic scenarios

**Error Handling:**
- `CreamException` hierarchy: `InvalidCreamUsageException`, `InvalidCreamOptionException`
- All exceptions include helpful solution messages
- Logger outputs link to GitHub issues for problem reporting

## Testing Approach

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
class CopyToTest {
    @Test
    fun testCopyFunction() {
        val source = Source("value")
        val target = source.copyToTarget(extra = 42)
        assertEquals("value", target.prop)
        assertEquals(42, target.extra)
    }
}
```

### Unit Tests (`cream-ksp/src/test/`)

Unit tests for processor logic (naming strategies, property matching, etc.):
```bash
./gradlew :cream-ksp:test
```

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

1. Define annotation in `cream-runtime/src/commonMain/kotlin/me/tbsten/cream/`
2. Add sealed class implementation to `GenerateSourceAnnotation.kt`
3. Create processor method in `CreamSymbolProcessor.kt` (e.g., `processNewAnnotation()`)
4. Add code generation logic in `cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/transform/`
5. Add test data in `test/src/commonMain/kotlin/me/tbsten/cream/test/newAnnotation/`
6. Add test cases in `test/src/commonTest/kotlin/me/tbsten/cream/test/newAnnotation/`

### Adding Configuration Options

1. Add property to `CreamOptions` in `cream-ksp/shared/src/commonMain/kotlin/me/tbsten/cream/ksp/options/`
2. Update parsing logic in `CreamOptions.kt` to read from KSP arguments
3. Use option in code generation logic (typically in `transform/` package)
4. Document in README.md under "Options" section

### Modifying Code Generation

- **Function naming:** Edit `CopyFunctionNameExt.kt` and `CopyFunctionName.kt`
- **Property matching:** Edit `FindMatchedProperty.kt`
- **Class generation:** Edit `Class.kt`, `Object.kt`, or `SealedClass.kt`
- **Type parameters:** Edit `CopyFunctionTypeParameters.kt`
- **KDoc generation:** Edit `KDoc.kt`

## Key Files to Know

| File | Purpose | Location |
|------|---------|----------|
| `CreamSymbolProcessor.kt` | KSP entry point & orchestration | cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/CreamSymbolProcessor.kt:1 |
| `Transform.kt` | Code generation dispatcher | cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/transform/Transform.kt:1 |
| `CopyFunctionNameExt.kt` | Function naming logic | cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/transform/CopyFunctionNameExt.kt:1 |
| `FindMatchedProperty.kt` | Property matching | cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/transform/FindMatchedProperty.kt:1 |
| `CreamOptions.kt` | Configuration parsing | cream-ksp/shared/src/commonMain/kotlin/me/tbsten/cream/ksp/options/CreamOptions.kt:1 |

## Common Pitfalls

1. **Multiplatform KSP**: Don't expect KSP to work in `commonMain` without the workaround. Generated code only appears in platform-specific source sets by default.

2. **Property Matching**: The processor matches properties by name (or via `@Map` annotation). Ensure constructor parameter names align with source property names for automatic copying.

3. **Sealed Class Hierarchy**: `@CopyToChildren` only generates functions to direct children. For deeply nested sealed hierarchies, consider multiple annotations or using `@CopyTo` explicitly.

4. **Generic Type Parameters**: When working with generics, ensure type parameters are properly preserved in generated functions. Check `CopyFunctionTypeParameters.kt` if issues arise.

5. **Visibility**: Generated copy functions respect the visibility of the target class constructor. Private constructors won't have accessible copy functions.

6. **Object Targets**: By default, copy functions to `object` types are generated (they just return the singleton). Use `cream.notCopyToObject=true` to suppress if undesired.
