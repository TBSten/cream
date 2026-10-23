[cream.kt](../README.md) / [cream-runtime](README.md)

# Runtime API

A public annotation, or the enum / name-token constants its arguments take, that users write in their own code

cream-runtime is annotation-only: it ships no logic, so a file holds annotation classes,
enum classes, or `const val` tokens such as the ones in `CopyFunctionNameToken.kt`.
Token constants are the single source of truth for `funName` / accessor-name tokens; cream-ksp reads them from here.

## Placement

| Path | When to use |
|---|---|
| `cream-runtime/src/commonMain/kotlin/me/tbsten/cream/*.kt` |  |

## Constraints

- declares the package its directory spells
- holds no runtime logic (no functions, no classes other than annotations and enums)

## Examples

- `CopyTo` ... `@CopyTo(Target::class)` written on the source class
- `CopyFunctionNameToken` ... Placeholders usable in `funName` templates
