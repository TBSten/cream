package me.tbsten.cream.ksp.feature.callFrom.scenario

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

internal fun propertyShapeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "zeroParams" to callFrom(fn("ping"), clazz("Trigger")),
        "mixedPrimitives" to
            callFrom(
                fn("record", param("name"), param("count", INT), param("enabled", BOOLEAN), param("ratio", DOUBLE)),
                dataClass("RecordArgs", Prop("name"), Prop("count", INT), Prop("enabled", BOOLEAN), Prop("ratio", DOUBLE)),
            ),
        "nullableProp" to
            callFrom(
                fn("describe", param("note", STRING.copy(nullable = true))),
                dataClass("DescribeArgs", Prop("note", STRING.copy(nullable = true))),
            ),
        "collectionProp" to
            callFrom(
                fn("collect", param("names", LIST.parameterizedBy(STRING))),
                dataClass("CollectArgs", Prop("names", LIST.parameterizedBy(STRING))),
            ),
        "customTypeProp" to
            callFrom(
                fn("render", param("config", classNameOf("Config"))),
                dataClass("RenderArgs", Prop("config", classNameOf("Config"))),
                extraDeclarations = listOf(dataClass("Config", Prop("id"))),
            ),
    )
