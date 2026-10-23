[cream.kt](../README.md) / [architecture-test](README.md)

# ArchitectureDefinition

The `architecture { }` definition: one file per group (`<group>/<Group>.kt`) and per role (`<group>/<role>/<Role>.kt`)

shared paths and constraints live in `support/`. Do not make the split functions `inline` (katachi reads declaration sites off the stack trace).

## Placement

| Path | When to use |
|---|---|
| `architecture-test/src/test/kotlin/me/tbsten/cream/test/architecture/ProjectArchitecture.kt` |  |
| `architecture-test/src/test/kotlin/me/tbsten/cream/test/architecture/*/**/*.kt` |  |

## Constraints

- declares the package its directory spells

## Examples

- `ProjectArchitecture`
