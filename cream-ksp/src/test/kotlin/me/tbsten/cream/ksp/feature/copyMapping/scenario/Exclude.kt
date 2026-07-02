package me.tbsten.cream.ksp.feature.copyMapping.scenario

import com.squareup.kotlinpoet.INT
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

/** `excludes` on `@CopyMapping` — the annotation-level equivalent of `@Exclude` for external classes (issue #164). */
internal fun excludeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        // `shared` loses its `= this.shared` default → required.
        "excludeSharedProperty" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("shared"), Prop("sourceOnly", INT)),
                dataClass("Target", Prop("shared"), Prop("targetOnly", INT)),
                excludes = listOf("shared"),
            ),
        // excludes references the target-side name of a renamed property.
        "excludeRenamedTarget" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("sourceName"), Prop("shared")),
                dataClass("Target", Prop("targetName"), Prop("shared")),
                properties = listOf("sourceName" to "targetName"),
                excludes = listOf("targetName"),
            ),
        // A name matching no auto-defaulted parameter is a no-op and emits a KSP warning.
        "excludeUnmatchedNameWarns" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("shared")),
                dataClass("Target", Prop("shared")),
                excludes = listOf("nonexistent"),
            ),
        // With canReverse, the same-named shared property is excluded in both directions.
        "excludeWithCanReverse" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("shared"), Prop("sourceOnly", INT)),
                dataClass("Target", Prop("shared"), Prop("targetOnly", INT)),
                canReverse = true,
                excludes = listOf("shared"),
            ),
        // canReverse + a renamed property: `excludes` is target-side, but the reverse function's parameter is
        // source-side, so it must be translated (targetName -> sourceName). Both directions drop the default.
        "excludeRenamedWithCanReverse" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("sourceName"), Prop("shared")),
                dataClass("Target", Prop("targetName"), Prop("shared")),
                properties = listOf("sourceName" to "targetName"),
                canReverse = true,
                excludes = listOf("targetName"),
            ),
        // A source-side name under canReverse: it misses every forward (target-side) parameter, but the reverse
        // function's parameters ARE source-side, so it drops the reverse default — no no-op warning is emitted.
        "excludeSourceSideNameWithCanReverse" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("sourceName"), Prop("shared")),
                dataClass("Target", Prop("targetName"), Prop("shared")),
                properties = listOf("sourceName" to "targetName"),
                canReverse = true,
                excludes = listOf("sourceName"),
            ),
    )
