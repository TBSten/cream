package me.tbsten.cream.ksp.feature.copyTo.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import me.tbsten.cream.CopyTo
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun mapAnno(vararg targetPropertyNames: String): AnnotationSpec =
    AnnotationSpec.builder(CopyTo.Map::class).apply { targetPropertyNames.forEach { addMember("%S", it) } }.build()

internal fun mapScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "propertyMapping" to
            copyTo(
                dataClass("Source", Prop("sourceName", paramAnnotation = mapAnno("targetName")), Prop("shared")),
                dataClass("Target", Prop("targetName"), Prop("shared")),
            ),
        "mapToNonexistentProperty" to
            copyTo(
                dataClass("Source", Prop("sourceName", paramAnnotation = mapAnno("missingTarget")), Prop("shared")),
                dataClass("Target", Prop("targetName"), Prop("shared")),
            ),
        "mapOverridesNameMatch" to
            copyTo(
                dataClass("Source", Prop("alpha"), Prop("beta", paramAnnotation = mapAnno("alpha"))),
                dataClass("Target", Prop("alpha")),
            ),
    )
