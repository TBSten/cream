[cream.kt](../README.md)

# Build logic

The `buildLogic` included build, and the build-tool files `gradle()` does not cover

| Role | Summary |
|---|---|
| [ConventionPlugin](./ConventionPlugin.md) | A convention plugin class, applied with `id("buildLogic.xxx")` |
| [ConventionDsl](./ConventionDsl.md) | Internal helpers the convention plugins share: version catalog and extension accessors |

## Placement in this group

```
buildLogic/convention/src/main/kotlin/
  *Plugin.kt  ConventionPlugin
  dsl/*.kt    ConventionDsl
```
