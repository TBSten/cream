[cream.kt](../../README.md) / [cream-ksp](../README.md) / [core layer](README.md)

# core/sealedCopy

`@SealedCopy` 生成（`when(this)` 自己 copy、leaf 分類。肥大ファイルは責務単位で複数に分割）

## Placement

| Path | When to use |
|---|---|
| `cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/core/sealedCopy/*.kt` |  |

## Constraints

- declares the package its directory spells
- stays within 300 lines (FindMatchedProperty.kt: 500)
- imports from cream / KSP only: core.*, options.*, util.*, cream-runtime, the KSP API

## Examples

- `SealedCopy`
