package me.tbsten.cream.ksp.feature.combineMapping.scenario

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
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun shapeScenario(vararg props: Prop): SnapshotScenario =
    combineMapping(
        mappingHolder(),
        listOf(dataClass("SourceA", *props), dataClass("SourceB", Prop("partner", INT))),
        dataClass("Target", *props, Prop("partner", INT)),
    )

internal fun propertyShapeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "stringProp" to shapeScenario(Prop("name")),
        "mixedPrimitives" to shapeScenario(Prop("name"), Prop("age", INT), Prop("active", BOOLEAN), Prop("score", DOUBLE)),
        "nullableProp" to shapeScenario(Prop("name", STRING.copy(nullable = true))),
        "collectionProp" to shapeScenario(Prop("tags", LIST.parameterizedBy(STRING))),
        "customTypeProp" to
            SnapshotScenario(
                listOf(
                    mappingHolder().withCombineMapping(listOf(classNameOf("SourceA"), classNameOf("SourceB")), classNameOf("Target")),
                    dataClass("SourceA", Prop("data", classNameOf("Data"))),
                    dataClass("SourceB", Prop("partner", INT)),
                    dataClass("Target", Prop("data", classNameOf("Data")), Prop("partner", INT)),
                    dataClass("Data", Prop("value")),
                ),
            ),
    )
