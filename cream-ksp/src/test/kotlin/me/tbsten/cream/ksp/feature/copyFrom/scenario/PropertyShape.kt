package me.tbsten.cream.ksp.feature.copyFrom.scenario

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

private fun samePropsScenario(vararg props: Prop): SnapshotScenario = copyFrom(dataClass("Target", *props), dataClass("Source", *props))

internal fun propertyShapeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "zeroProps" to copyFrom(clazz("Target"), clazz("Source")),
        "stringProp" to samePropsScenario(Prop("name")),
        "mixedPrimitives" to samePropsScenario(Prop("name"), Prop("age", INT), Prop("active", BOOLEAN), Prop("score", DOUBLE)),
        "nullableProp" to samePropsScenario(Prop("name", STRING.copy(nullable = true))),
        "collectionProp" to samePropsScenario(Prop("tags", LIST.parameterizedBy(STRING))),
        "customTypeProp" to
            SnapshotScenario(
                dataClass("Target", Prop("data", classNameOf("Data"))).withCopyFrom(classNameOf("Source")),
                dataClass("Source", Prop("data", classNameOf("Data"))),
                dataClass("Data", Prop("value")),
            ),
    )
