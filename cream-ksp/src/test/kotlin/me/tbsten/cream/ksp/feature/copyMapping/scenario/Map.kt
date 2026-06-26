package me.tbsten.cream.ksp.feature.copyMapping.scenario

import com.squareup.kotlinpoet.INT
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun mapScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "singleMapping" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("sourceName"), Prop("shared")),
                dataClass("Target", Prop("targetName"), Prop("shared")),
                properties = listOf("sourceName" to "targetName"),
            ),
        "multipleMappings" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("firstSource"), Prop("secondSource", INT)),
                dataClass("Target", Prop("firstTarget"), Prop("secondTarget", INT)),
                properties = listOf("firstSource" to "firstTarget", "secondSource" to "secondTarget"),
            ),
        "mapToNonexistentProperty" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("sourceName"), Prop("shared")),
                dataClass("Target", Prop("targetName"), Prop("shared")),
                properties = listOf("missing" to "targetName"),
            ),
        "mapOverridesNameMatch" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("alpha"), Prop("beta")),
                dataClass("Target", Prop("alpha")),
                properties = listOf("beta" to "alpha"),
            ),
    )
