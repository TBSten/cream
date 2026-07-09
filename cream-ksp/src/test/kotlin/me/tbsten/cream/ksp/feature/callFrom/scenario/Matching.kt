package me.tbsten.cream.ksp.feature.callFrom.scenario

import com.squareup.kotlinpoet.INT
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun matchingScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "partialMatch" to
            callFrom(
                fn("submit", param("id"), param("comment")),
                dataClass("SubmitArgs", Prop("id")),
            ),
        "typeIncompatible" to
            callFrom(
                fn("resize", param("value", INT)),
                dataClass("ResizeArgs", Prop("value")),
            ),
    )
