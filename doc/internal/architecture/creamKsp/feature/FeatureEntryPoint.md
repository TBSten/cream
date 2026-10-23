[cream.kt](../../README.md) / [cream-ksp](../README.md) / [feature layer](README.md)

# Feature entry point

`Process<Name>.kt`: top-level `context(ctx: ProcessContext) internal fun processXxx(): List<KSAnnotated>`

## Placement

| Path | When to use |
|---|---|
| `cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/feature/*/Process*.kt` |  |

## Constraints

- declares the package its directory spells
- stays within 300 lines (FindMatchedProperty.kt: 500)
- is named Process<FeatureDirectory>.kt
- declares a top-level fun process*()
- process*() is internal
- process*() returns List<KSAnnotated>
- process*() declares context(ctx: ProcessContext)
- does not import another feature
- imports from cream / KSP only: feature.*, core.*, options.*, util.*, ProcessContext, cream-runtime, the KSP API

## Examples

- `ProcessCopyTo` ... The entry point of `@CopyTo`
