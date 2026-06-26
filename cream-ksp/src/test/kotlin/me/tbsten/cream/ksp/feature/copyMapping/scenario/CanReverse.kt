package me.tbsten.cream.ksp.feature.copyMapping.scenario

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.INT
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun canReverseScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "bidirectional" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("shared"), Prop("sourceOnly", INT)),
                dataClass("Target", Prop("shared"), Prop("targetOnly", BOOLEAN)),
                canReverse = true,
            ),
        "bidirectionalWithMapping" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("sourceName"), Prop("shared")),
                dataClass("Target", Prop("targetName"), Prop("shared")),
                canReverse = true,
                properties = listOf("sourceName" to "targetName"),
            ),
    )
