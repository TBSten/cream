[cream.kt](../../README.md) / [cream-ksp](../README.md) / [feature layer](README.md)

# Feature diagnostics

その注釈に固有の補助: 引数 parse・注釈固有の検証 / 診断ヘルパ

## Placement

| Path | When to use |
|---|---|
| `cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/feature/*/*Diagnostics.kt` |  |

## Constraints

- declares the package its directory spells
- stays within 300 lines (FindMatchedProperty.kt: 500)
- is named after its feature directory
- does not import another feature
- imports from cream / KSP only: feature.*, core.*, options.*, util.*, ProcessContext, cream-runtime, the KSP API

## Examples

- `ChildOptionalsDiagnostics`
