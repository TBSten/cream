package me.tbsten.cream.ksp.feature.copyMapping.scenario

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.INT
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun excludesScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "excludeSharedProp" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("shared"), Prop("sourceOnly", INT)),
                dataClass("Target", Prop("shared"), Prop("targetOnly", BOOLEAN)),
                excludes = listOf("shared"),
            ),
        "excludeMappedProperty" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("sourceName"), Prop("shared")),
                dataClass("Target", Prop("targetName"), Prop("shared")),
                properties = listOf("sourceName" to "targetName"),
                excludes = listOf("targetName"),
            ),
        "excludeUnmatchedName" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("shared")),
                dataClass("Target", Prop("shared"), Prop("targetOnly", BOOLEAN)),
                excludes = listOf("missing"),
            ),
        "excludeSharedPropWithCanReverse" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("shared"), Prop("sourceOnly", INT)),
                dataClass("Target", Prop("shared"), Prop("targetOnly", BOOLEAN)),
                canReverse = true,
                excludes = listOf("shared"),
            ),
        "excludeMappedPropertyWithCanReverse" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("sourceName"), Prop("shared")),
                dataClass("Target", Prop("targetName"), Prop("shared")),
                canReverse = true,
                properties = listOf("sourceName" to "targetName"),
                excludes = listOf("targetName"),
            ),
        // The unmatched warning must fire ONCE (not once per direction) and name the entry as
        // the user wrote it, even though the reverse pass evaluates a Map-translated spelling.
        "excludeUnmatchedNameWithCanReverse" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("shared")),
                dataClass("Target", Prop("shared"), Prop("targetOnly", BOOLEAN)),
                canReverse = true,
                excludes = listOf("missing"),
            ),
    )
