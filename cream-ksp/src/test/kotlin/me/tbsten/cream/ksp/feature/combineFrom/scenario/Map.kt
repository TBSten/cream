package me.tbsten.cream.ksp.feature.combineFrom.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.INT
import me.tbsten.cream.CombineFrom
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun mapAnno(vararg sourcePropertyNames: String): AnnotationSpec =
    AnnotationSpec.builder(CombineFrom.Map::class).apply { sourcePropertyNames.forEach { addMember("%S", it) } }.build()

internal fun mapScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "singleMapping" to
            combineFrom(
                dataClass("Target", Prop("targetName", paramAnnotation = mapAnno("sourceName")), Prop("shared"), Prop("extra", INT)),
                dataClass("SourceA", Prop("sourceName"), Prop("shared")),
                dataClass("SourceB", Prop("extra", INT)),
            ),
        "multipleMappings" to
            combineFrom(
                dataClass(
                    "Target",
                    Prop("first", paramAnnotation = mapAnno("a")),
                    Prop("second", paramAnnotation = mapAnno("b")),
                    Prop("extra", INT),
                ),
                dataClass("SourceA", Prop("a"), Prop("b")),
                dataClass("SourceB", Prop("extra", INT)),
            ),
    )
