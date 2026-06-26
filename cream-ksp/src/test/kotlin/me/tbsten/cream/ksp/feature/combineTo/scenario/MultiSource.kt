package me.tbsten.cream.ksp.feature.combineTo.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.TypeVariableName
import me.tbsten.cream.CombineTo
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private val EXCLUDE = AnnotationSpec.builder(CombineTo.Exclude::class).build()

private fun mapAnno(vararg targetPropertyNames: String): AnnotationSpec =
    AnnotationSpec.builder(CombineTo.Map::class).apply { targetPropertyNames.forEach { addMember("%S", it) } }.build()

internal fun multiSourceScenarios(): Generator<SnapshotScenario> {
    val t = TypeVariableName("T")
    val u = TypeVariableName("U")
    return Generator.snapshotScenarios(
        "singleSource" to
            combinedInto(
                dataClass("Target", Prop("name"), Prop("extra", INT)),
                dataClass("SourceA", Prop("name"), Prop("extra", INT)),
            ),
        "twoSources" to
            combinedInto(
                dataClass("Target", Prop("propertyA"), Prop("propertyB", INT), Prop("propertyC", BOOLEAN)),
                dataClass("SourceA", Prop("propertyA")),
                dataClass("SourceB", Prop("propertyB", INT)),
            ),
        "threeSources" to
            combinedInto(
                dataClass("Target", Prop("propertyA"), Prop("propertyB", INT), Prop("propertyC", BOOLEAN), Prop("propertyD", DOUBLE)),
                dataClass("SourceA", Prop("propertyA")),
                dataClass("SourceB", Prop("propertyB", INT)),
                dataClass("SourceC", Prop("propertyC", BOOLEAN)),
            ),
        "overlappingProperty" to
            combinedInto(
                dataClass("Target", Prop("shared"), Prop("uniqueA", INT), Prop("uniqueB", BOOLEAN)),
                dataClass("SourceA", Prop("shared"), Prop("uniqueA", INT)),
                dataClass("SourceB", Prop("shared"), Prop("uniqueB", BOOLEAN)),
            ),
        "excludeSuppressesAcrossSources" to
            combinedInto(
                dataClass("Target", Prop("shared"), Prop("uniqueA", INT), Prop("uniqueB", BOOLEAN)),
                dataClass("SourceA", Prop("shared", paramAnnotation = EXCLUDE), Prop("uniqueA", INT)),
                dataClass("SourceB", Prop("shared"), Prop("uniqueB", BOOLEAN)),
            ),
        "mapAcrossSources" to
            combinedInto(
                dataClass("Target", Prop("primary"), Prop("renamed")),
                dataClass("SourceA", Prop("primary")),
                dataClass("SourceB", Prop("originalB", paramAnnotation = mapAnno("renamed"))),
            ),
        "multiSourceGenerics" to
            combinedInto(
                dataClass("Target", Prop("a", t), Prop("b", u), typeVariables = listOf(t, u)),
                dataClass("SourceA", Prop("a", t), typeVariables = listOf(t)),
                dataClass("SourceB", Prop("b", u), typeVariables = listOf(u)),
            ),
    )
}
