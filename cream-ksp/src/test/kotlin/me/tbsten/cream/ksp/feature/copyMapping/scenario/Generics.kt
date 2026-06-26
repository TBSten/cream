package me.tbsten.cream.ksp.feature.copyMapping.scenario

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
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("name")),
                dataClass("Target", Prop("name"), Prop("item", t), typeVariables = listOf(t)),
            ),
        "sourceOnlyTypeParam" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("item", t), typeVariables = listOf(t)),
                dataClass("Target", Prop("name")),
            ),
        "sharedTypeParam" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("item", t), typeVariables = listOf(t)),
                dataClass("Target", Prop("item", t), typeVariables = listOf(t)),
            ),
        "boundedTypeParam" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("item", boundedT), typeVariables = listOf(boundedT)),
                dataClass("Target", Prop("item", boundedT), typeVariables = listOf(boundedT)),
            ),
    )
}
