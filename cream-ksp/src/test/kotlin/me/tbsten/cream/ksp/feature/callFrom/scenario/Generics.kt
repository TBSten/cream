package me.tbsten.cream.ksp.feature.callFrom.scenario

import com.squareup.kotlinpoet.COMPARABLE
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeVariableName
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun genericsScenarios(): Generator<SnapshotScenario> {
    val t = TypeVariableName("T")
    val boundedT = TypeVariableName("T", COMPARABLE.parameterizedBy(TypeVariableName("T")))
    return Generator.snapshotScenarios(
        "genericFunction" to
            callFrom(
                FunSpec
                    .builder("tag")
                    .addTypeVariable(t)
                    .addParameter("item", t)
                    .addParameter("label", STRING)
                    .build(),
                dataClass("TagArgs", Prop("label")),
            ),
        "genericSourceClass" to
            callFrom(
                fn("count", param("total", INT)),
                dataClass("TallyArgs", Prop("item", t), Prop("total", INT), typeVariables = listOf(t)),
            ),
        "sharedTypeParam" to
            callFrom(
                FunSpec
                    .builder("process")
                    .addTypeVariable(t)
                    .addParameter("item", t)
                    .build(),
                dataClass("ProcessArgs", Prop("item", t), typeVariables = listOf(t)),
            ),
        "boundedTypeParam" to
            callFrom(
                FunSpec
                    .builder("sort")
                    .addTypeVariable(boundedT)
                    .addParameter("item", boundedT)
                    .build(),
                dataClass("SortArgs", Prop("item", boundedT), typeVariables = listOf(boundedT)),
            ),
    )
}
