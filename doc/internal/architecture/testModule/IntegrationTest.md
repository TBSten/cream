[cream.kt](../README.md) / [test module](README.md)

# Integration test

A kotest spec calling the generated functions: `<feature>/<Name>Test.kt`, named after the data it uses

## Placement

| Path | When to use |
|---|---|
| `test/src/commonTest/kotlin/me/tbsten/cream/test/*/*Test.kt` |  |

## Constraints

- declares the package its directory spells
- is a kotest FreeSpec

## Examples

- `BasicTest` ... `copyTo/BasicTest.kt`
