[cream.kt](../README.md) / [Build logic](README.md)

# ConventionPlugin

A convention plugin class, applied with `id("buildLogic.xxx")`

## Placement

| Path | When to use |
|---|---|
| `buildLogic/convention/src/main/kotlin/*Plugin.kt` |  |

## Constraints

- implements Plugin<Project>

## Examples

- `LintPlugin` ... `buildLogic.lint`: ktlint for every module
