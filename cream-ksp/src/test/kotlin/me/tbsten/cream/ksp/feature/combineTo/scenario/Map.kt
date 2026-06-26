package me.tbsten.cream.ksp.feature.combineTo.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.INT
import me.tbsten.cream.CombineTo
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun mapAnno(vararg targetPropertyNames: String): AnnotationSpec =
    AnnotationSpec.builder(CombineTo.Map::class).apply { targetPropertyNames.forEach { addMember("%S", it) } }.build()

internal fun mapScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "singleMapping" to
            combinedInto(
                dataClass("Target", Prop("targetName"), Prop("extra", INT)),
                dataClass("SourceA", Prop("sourceName", paramAnnotation = mapAnno("targetName"))),
                dataClass("SourceB", Prop("extra", INT)),
            ),
        "multipleMappings" to
            combinedInto(
                dataClass("Target", Prop("first"), Prop("second"), Prop("extra", INT)),
                dataClass("SourceA", Prop("value", paramAnnotation = mapAnno("first", "second"))),
                dataClass("SourceB", Prop("extra", INT)),
            ),
    )
