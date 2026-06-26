package me.tbsten.cream.ksp.feature.copyTo.scenario

import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun escapingScenario(propertyName: String): SnapshotScenario = copyTo(dataClass("Source", Prop(propertyName)), dataClass("Target", Prop(propertyName)))

internal fun escapingScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "keywordProperty" to escapingScenario("in"),
        "spaceInName" to escapingScenario("my value"),
        "nonAsciiProperty" to escapingScenario("税込金額"),
    )
