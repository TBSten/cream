[cream.kt](../../README.md) / [cream-ksp](../README.md) / [core layer](README.md)

# core/parentOptional

`@ParentOptional` / `@ChildOptionals` 生成（sealed 親への nullable 拡張プロパティ accessor。両 feature が共有）

## Placement

| Path | When to use |
|---|---|
| `cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/core/parentOptional/*.kt` |  |

## Constraints

- declares the package its directory spells
- stays within 300 lines (FindMatchedProperty.kt: 500)
- imports from cream / KSP only: core.*, options.*, util.*, cream-runtime, the KSP API

## Examples

- `ParentOptionalAccessor`
