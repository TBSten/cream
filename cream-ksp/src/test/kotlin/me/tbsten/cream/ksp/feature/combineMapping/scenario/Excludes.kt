package me.tbsten.cream.ksp.feature.combineMapping.scenario

import com.squareup.kotlinpoet.INT
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private val excludePartnerSource = dataClass("SourceB", Prop("extra", INT))

internal fun excludesScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "excludeSharedProp" to
            combineMapping(
                mappingHolder(),
                listOf(dataClass("SourceA", Prop("shared"), Prop("sourceOnly", INT)), excludePartnerSource),
                dataClass("Target", Prop("shared"), Prop("sourceOnly", INT), Prop("extra", INT)),
                excludes = listOf("shared"),
            ),
        "excludeMappedProperty" to
            combineMapping(
                mappingHolder(),
                listOf(dataClass("SourceA", Prop("sourceName"), Prop("shared")), excludePartnerSource),
                dataClass("Target", Prop("targetName"), Prop("shared"), Prop("extra", INT)),
                properties = listOf("sourceName" to "targetName"),
                excludes = listOf("targetName"),
            ),
        "excludeUnmatchedName" to
            combineMapping(
                mappingHolder(),
                listOf(dataClass("SourceA", Prop("shared")), excludePartnerSource),
                dataClass("Target", Prop("shared"), Prop("extra", INT)),
                excludes = listOf("missing"),
            ),
    )
