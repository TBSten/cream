package me.tbsten.cream.ksp.feature.copyFrom.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import me.tbsten.cream.CopyFrom
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun mapAnno(vararg sourcePropertyNames: String): AnnotationSpec =
    AnnotationSpec.builder(CopyFrom.Map::class).apply { sourcePropertyNames.forEach { addMember("%S", it) } }.build()

internal fun mapScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "propertyMapping" to
            copyFrom(
                dataClass("Target", Prop("targetName", paramAnnotation = mapAnno("sourceName")), Prop("shared")),
                dataClass("Source", Prop("sourceName"), Prop("shared")),
            ),
        "mapToNonexistentProperty" to
            copyFrom(
                dataClass("Target", Prop("targetName", paramAnnotation = mapAnno("missingSource")), Prop("shared")),
                dataClass("Source", Prop("sourceName"), Prop("shared")),
            ),
        "mapOverridesNameMatch" to
            copyFrom(
                dataClass("Target", Prop("alpha", paramAnnotation = mapAnno("beta"))),
                dataClass("Source", Prop("alpha"), Prop("beta")),
            ),
    )
