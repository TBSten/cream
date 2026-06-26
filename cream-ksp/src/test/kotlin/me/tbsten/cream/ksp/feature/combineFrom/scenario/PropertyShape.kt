package me.tbsten.cream.ksp.feature.combineFrom.scenario

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

private fun singlePropScenario(axisProp: Prop): SnapshotScenario =
    combineFrom(
        dataClass("Target", axisProp, Prop("extra", INT)),
        dataClass("SourceA", axisProp),
        dataClass("SourceB", Prop("extra", INT)),
    )

internal fun propertyShapeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "zeroProps" to combineFrom(clazz("Target"), clazz("SourceA"), clazz("SourceB")),
        "stringProp" to singlePropScenario(Prop("name")),
        "mixedPrimitives" to
            combineFrom(
                dataClass("Target", Prop("name"), Prop("age", INT), Prop("active", BOOLEAN), Prop("score", DOUBLE)),
                dataClass("SourceA", Prop("name"), Prop("age", INT)),
                dataClass("SourceB", Prop("active", BOOLEAN), Prop("score", DOUBLE)),
            ),
        "nullableProp" to singlePropScenario(Prop("name", STRING.copy(nullable = true))),
        "collectionProp" to singlePropScenario(Prop("tags", LIST.parameterizedBy(STRING))),
        "customTypeProp" to
            SnapshotScenario(
                dataClass("Target", Prop("data", classNameOf("Data")), Prop("extra", INT))
                    .withCombineFrom(classNameOf("SourceA"), classNameOf("SourceB")),
                dataClass("SourceA", Prop("data", classNameOf("Data"))),
                dataClass("SourceB", Prop("extra", INT)),
                dataClass("Data", Prop("value")),
            ),
    )
