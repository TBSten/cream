package me.tbsten.cream.ksp.feature.combineFrom.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.TypeVariableName
import me.tbsten.cream.CombineFrom
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private val EXCLUDE = AnnotationSpec.builder(CombineFrom.Exclude::class).build()

private fun mapAnno(vararg sourcePropertyNames: String): AnnotationSpec =
    AnnotationSpec.builder(CombineFrom.Map::class).apply { sourcePropertyNames.forEach { addMember("%S", it) } }.build()

internal fun multiSourceScenarios(): Generator<SnapshotScenario> {
    val t = TypeVariableName("T")
    val u = TypeVariableName("U")
    return Generator.snapshotScenarios(
        "noSourcesRejected" to
            SnapshotScenario(dataClass("Target", Prop("name"), Prop("extra", INT)).withCombineFrom()),
        "singleSource" to
            combineFrom(
                dataClass("Target", Prop("name"), Prop("extra", INT)),
                dataClass("SourceA", Prop("name"), Prop("extra", INT)),
            ),
        "twoSources" to
            combineFrom(
                dataClass("Target", Prop("propertyA"), Prop("propertyB", INT), Prop("propertyC", BOOLEAN)),
                dataClass("SourceA", Prop("propertyA")),
                dataClass("SourceB", Prop("propertyB", INT)),
            ),
        "threeSources" to
            combineFrom(
                dataClass("Target", Prop("propertyA"), Prop("propertyB", INT), Prop("propertyC", BOOLEAN), Prop("propertyD", DOUBLE)),
                dataClass("SourceA", Prop("propertyA")),
                dataClass("SourceB", Prop("propertyB", INT)),
                dataClass("SourceC", Prop("propertyC", BOOLEAN)),
            ),
        "overlappingProperty" to
            combineFrom(
                dataClass("Target", Prop("shared"), Prop("uniqueA", INT), Prop("uniqueB", BOOLEAN)),
                dataClass("SourceA", Prop("shared"), Prop("uniqueA", INT)),
                dataClass("SourceB", Prop("shared"), Prop("uniqueB", BOOLEAN)),
            ),
        "excludeOverlappingProperty" to
            combineFrom(
                dataClass("Target", Prop("shared", paramAnnotation = EXCLUDE), Prop("uniqueA", INT), Prop("uniqueB", BOOLEAN)),
                dataClass("SourceA", Prop("shared"), Prop("uniqueA", INT)),
                dataClass("SourceB", Prop("shared"), Prop("uniqueB", BOOLEAN)),
            ),
        "mapAcrossSources" to
            combineFrom(
                dataClass("Target", Prop("fromA"), Prop("renamed", paramAnnotation = mapAnno("originalB")), Prop("extra", INT)),
                dataClass("SourceA", Prop("fromA")),
                dataClass("SourceB", Prop("originalB"), Prop("extra", INT)),
            ),
        "multiSourceGenerics" to
            combineFrom(
                dataClass("Target", Prop("a", t), Prop("b", u), typeVariables = listOf(t, u)),
                dataClass("SourceA", Prop("a", t), typeVariables = listOf(t)),
                dataClass("SourceB", Prop("b", u), typeVariables = listOf(u)),
            ),
    )
}
