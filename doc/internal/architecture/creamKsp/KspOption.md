[cream.kt](../README.md) / [cream-ksp](README.md)

# KSP option

KSP オプションのモデルとパース（`CreamOptions` / `EscapeDot` / `CopyFunNamingStrategy`）。全層から参照される横断モデル

パース失敗は `core/error` の例外で送出する。KSP API には依存しない。

## Placement

| Path | When to use |
|---|---|
| `cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/options/*.kt` |  |

## Constraints

- declares the package its directory spells
- stays within 300 lines (FindMatchedProperty.kt: 500)
- imports from cream / KSP only: options.*, util.*, core.error.*, cream-runtime

## Examples

- `CreamOptions`
