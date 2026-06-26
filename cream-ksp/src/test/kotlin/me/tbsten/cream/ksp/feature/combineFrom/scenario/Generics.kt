package me.tbsten.cream.ksp.feature.combineFrom.scenario

import com.squareup.kotlinpoet.COMPARABLE
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
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
        "targetOnlyTypeParam" to
            combineFrom(
                dataClass("Target", Prop("name"), Prop("item", t), typeVariables = listOf(t)),
                dataClass("Source", Prop("name")),
            ),
        "sourceOnlyTypeParam" to
            combineFrom(
                dataClass("Target", Prop("name")),
                dataClass("Source", Prop("item", t), typeVariables = listOf(t)),
            ),
        "sharedTypeParam" to
            combineFrom(
                dataClass("Target", Prop("item", t), typeVariables = listOf(t)),
                dataClass("Source", Prop("item", t), typeVariables = listOf(t)),
            ),
        "boundedTypeParam" to
            combineFrom(
                dataClass("Target", Prop("item", boundedT), typeVariables = listOf(boundedT)),
                dataClass("Source", Prop("item", boundedT), typeVariables = listOf(boundedT)),
            ),
    )
}
