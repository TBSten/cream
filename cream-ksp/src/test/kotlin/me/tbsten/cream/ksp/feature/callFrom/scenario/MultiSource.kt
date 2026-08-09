package me.tbsten.cream.ksp.feature.callFrom.scenario

import com.squareup.kotlinpoet.INT
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun multiSourceScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "twoSources" to
            callFrom(
                fn("consume", param("value")),
                dataClass("ArgsA", Prop("value")),
                dataClass("ArgsB", Prop("value")),
            ),
        "sourcesMatchingDifferentParams" to
            callFrom(
                fn("update", param("id"), param("count", INT)),
                dataClass("IdArgs", Prop("id")),
                dataClass("CountArgs", Prop("count", INT)),
            ),
    )
