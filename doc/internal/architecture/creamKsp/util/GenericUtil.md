[cream.kt](../../README.md) / [cream-ksp](../README.md) / [util layer](README.md)

# util

Kotlin stdlib だけに依存するヘルパ。KSP 以外の文脈でも再利用できる

## Placement

| Path | When to use |
|---|---|
| `cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/util/*.kt` |  |

## Constraints

- declares the package its directory spells
- stays within 300 lines (FindMatchedProperty.kt: 500)
- imports from cream / KSP only: util.*

## Examples

- `EscapeIdentifier`
