package me.tbsten.cream.ksp.feature.combineMapping.scenario

import com.squareup.kotlinpoet.INT
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.sealedInterface
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun sourceKindValidationScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "insufficientSources" to
            combineMapping(
                mappingHolder(),
                listOf(dataClass("SourceA", Prop("name"))),
                dataClass("Target", Prop("name"), Prop("extra", INT)),
            ),
        "nonClassSource" to
            combineMapping(
                mappingHolder(),
                listOf(dataClass("SourceA", Prop("name")), sealedInterface("SourceB", "extra")),
                dataClass("Target", Prop("name"), Prop("extra")),
            ),
    )
