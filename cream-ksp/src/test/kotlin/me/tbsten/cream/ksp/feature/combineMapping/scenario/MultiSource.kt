package me.tbsten.cream.ksp.feature.combineMapping.scenario

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.INT
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun multiSourceScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "twoSources" to
            combineMapping(
                mappingHolder(),
                listOf(dataClass("SourceA", Prop("propertyA")), dataClass("SourceB", Prop("propertyB", INT))),
                dataClass("Target", Prop("propertyA"), Prop("propertyB", INT), Prop("propertyC", BOOLEAN)),
            ),
        "threeSources" to
            combineMapping(
                mappingHolder(),
                listOf(
                    dataClass("SourceA", Prop("propertyA")),
                    dataClass("SourceB", Prop("propertyB", INT)),
                    dataClass("SourceC", Prop("propertyC", BOOLEAN)),
                ),
                dataClass("Target", Prop("propertyA"), Prop("propertyB", INT), Prop("propertyC", BOOLEAN), Prop("propertyD", DOUBLE)),
            ),
        "overlappingProperty" to
            combineMapping(
                mappingHolder(),
                listOf(
                    dataClass("SourceA", Prop("shared"), Prop("uniqueA", INT)),
                    dataClass("SourceB", Prop("shared"), Prop("uniqueB", BOOLEAN)),
                ),
                dataClass("Target", Prop("shared"), Prop("uniqueA", INT), Prop("uniqueB", BOOLEAN)),
            ),
        "mapAcrossSources" to
            combineMapping(
                mappingHolder(),
                listOf(dataClass("SourceA", Prop("fromA")), dataClass("SourceB", Prop("originalB"), Prop("extra", INT))),
                dataClass("Target", Prop("fromA"), Prop("renamed"), Prop("extra", INT)),
                properties = listOf("originalB" to "renamed"),
            ),
    )
