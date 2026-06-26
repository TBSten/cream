package me.tbsten.cream.ksp.feature.combineTo.scenario

import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.STRING
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun matchingScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "typeIncompatible" to
            combinedInto(
                dataClass("Target", Prop("value", INT), Prop("extra", INT)),
                dataClass("SourceA", Prop("value", STRING)),
                dataClass("SourceB", Prop("extra", INT)),
            ),
    )
