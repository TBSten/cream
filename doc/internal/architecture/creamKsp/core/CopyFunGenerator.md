[cream.kt](../../README.md) / [cream-ksp](../README.md) / [core layer](README.md)

# core/copyFun

copy 関数生成（class / object / sealed への dispatch、Class / Object / SealedClass 生成）

## Placement

| Path | When to use |
|---|---|
| `cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/core/copyFun/*.kt` |  |

## Constraints

- declares the package its directory spells
- stays within 300 lines (FindMatchedProperty.kt: 500)
- imports from cream / KSP only: core.*, options.*, util.*, cream-runtime, the KSP API

## Examples

- `Class`
- `SealedClass`
