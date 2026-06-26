package me.tbsten.cream.ksp.feature.combineMapping.scenario

import com.squareup.kotlinpoet.COMPARABLE
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeVariableName
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private val partnerSource = dataClass("SourceB", Prop("tag"))

internal fun genericsScenarios(): Generator<SnapshotScenario> {
    val t = TypeVariableName("T")
    val boundedT = TypeVariableName("T", COMPARABLE.parameterizedBy(TypeVariableName("T")))
    return Generator.snapshotScenarios(
        "targetOnlyTypeParam" to
            combineMapping(
                mappingHolder(),
                listOf(dataClass("SourceA", Prop("name")), partnerSource),
                dataClass("Target", Prop("name"), Prop("tag"), Prop("item", t), typeVariables = listOf(t)),
            ),
        "sourceOnlyTypeParam" to
            combineMapping(
                mappingHolder(),
                listOf(dataClass("SourceA", Prop("item", t), typeVariables = listOf(t)), partnerSource),
                dataClass("Target", Prop("tag")),
            ),
        "sharedTypeParam" to
            combineMapping(
                mappingHolder(),
                listOf(dataClass("SourceA", Prop("item", t), typeVariables = listOf(t)), partnerSource),
                dataClass("Target", Prop("item", t), Prop("tag"), typeVariables = listOf(t)),
            ),
        "boundedTypeParam" to
            combineMapping(
                mappingHolder(),
                listOf(dataClass("SourceA", Prop("item", boundedT), typeVariables = listOf(boundedT)), partnerSource),
                dataClass("Target", Prop("item", boundedT), Prop("tag"), typeVariables = listOf(boundedT)),
            ),
    )
}
