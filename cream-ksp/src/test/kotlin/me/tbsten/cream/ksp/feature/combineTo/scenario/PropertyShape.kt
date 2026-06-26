package me.tbsten.cream.ksp.feature.combineTo.scenario

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STRING
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.clazz
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun axisScenario(axis: Prop): SnapshotScenario =
    combinedInto(
        dataClass("Target", axis, Prop("extra", INT)),
        dataClass("SourceA", axis),
        dataClass("SourceB", Prop("extra", INT)),
    )

internal fun propertyShapeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "zeroProps" to combinedInto(clazz("Target"), clazz("SourceA"), clazz("SourceB")),
        "stringProp" to axisScenario(Prop("name")),
        "mixedPrimitives" to
            combinedInto(
                dataClass("Target", Prop("name"), Prop("age", INT), Prop("active", BOOLEAN), Prop("score", DOUBLE)),
                dataClass("SourceA", Prop("name"), Prop("age", INT)),
                dataClass("SourceB", Prop("active", BOOLEAN), Prop("score", DOUBLE)),
            ),
        "nullableProp" to axisScenario(Prop("name", STRING.copy(nullable = true))),
        "collectionProp" to axisScenario(Prop("tags", LIST.parameterizedBy(STRING))),
        "customTypeProp" to
            SnapshotScenario(
                dataClass("SourceA", Prop("data", classNameOf("Data"))).withCombineTo(classNameOf("Target")),
                dataClass("SourceB", Prop("extra", INT)).withCombineTo(classNameOf("Target")),
                dataClass("Target", Prop("data", classNameOf("Data")), Prop("extra", INT)),
                dataClass("Data", Prop("value")),
            ),
    )
