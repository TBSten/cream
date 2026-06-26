package me.tbsten.cream.ksp.feature.combineMapping.scenario

import com.squareup.kotlinpoet.INT
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private val partnerSource = dataClass("SourceB", Prop("extra", INT))

internal fun mapScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "singleMapping" to
            combineMapping(
                mappingHolder(),
                listOf(dataClass("SourceA", Prop("sourceName"), Prop("shared")), partnerSource),
                dataClass("Target", Prop("targetName"), Prop("shared"), Prop("extra", INT)),
                properties = listOf("sourceName" to "targetName"),
            ),
        "multipleMappings" to
            combineMapping(
                mappingHolder(),
                listOf(dataClass("SourceA", Prop("firstSource"), Prop("secondSource", INT)), partnerSource),
                dataClass("Target", Prop("firstTarget"), Prop("secondTarget", INT), Prop("extra", INT)),
                properties = listOf("firstSource" to "firstTarget", "secondSource" to "secondTarget"),
            ),
        "mapToNonexistentProperty" to
            combineMapping(
                mappingHolder(),
                listOf(dataClass("SourceA", Prop("sourceName"), Prop("shared")), partnerSource),
                dataClass("Target", Prop("targetName"), Prop("shared"), Prop("extra", INT)),
                properties = listOf("missing" to "targetName"),
            ),
        "mapOverridesNameMatch" to
            combineMapping(
                mappingHolder(),
                listOf(dataClass("SourceA", Prop("alpha"), Prop("beta")), partnerSource),
                dataClass("Target", Prop("alpha"), Prop("extra", INT)),
                properties = listOf("beta" to "alpha"),
            ),
    )
