[cream.kt](../../README.md) / [cream-ksp](../README.md) / [Composition root](README.md)

# ProcessContext

`ProcessContext = {resolver, options, codeGenerator, logger}`: every feature receives it as a context parameter

leaf infra: KSP API と `options`（`CreamOptions`）だけに依存し、feature / core / util / `CreamSymbolProcessor` を import しない。
**`logger` は必須**（`KSPLogger?` 禁止）。

## Placement

| Path | When to use |
|---|---|
| `cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/ProcessContext.kt` |  |

## Constraints

- stays within 300 lines (FindMatchedProperty.kt: 500)
- imports from cream / KSP only: options.*, the KSP API

## Examples

- `ProcessContext`
