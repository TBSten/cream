package me.tbsten.cream.ksp.feature.copyTo.scenario

import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.STRING
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun matchingScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "typeIncompatible" to copyTo(dataClass("Source", Prop("value", STRING)), dataClass("Target", Prop("value", INT))),
    )
