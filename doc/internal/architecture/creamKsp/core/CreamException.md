[cream.kt](../../README.md) / [cream-ksp](../README.md) / [core layer](README.md)

# core/error

例外階層（`CreamException` / `InvalidCreamUsageException` / `InvalidCreamOptionException` / `UnknownCreamException` と `reportToGithub`）

`util` 以外に依存しない葉。だから `options` からも送出できる（options → core/error の 1 箇所だけパッケージ間の相互参照になるが、
core/error が葉なので循環しない）。

## Placement

| Path | When to use |
|---|---|
| `cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/core/error/*.kt` |  |

## Constraints

- declares the package its directory spells
- stays within 300 lines (FindMatchedProperty.kt: 500)
- imports from cream / KSP only: util.*, core.error.*

## Examples

- `CreamException`
