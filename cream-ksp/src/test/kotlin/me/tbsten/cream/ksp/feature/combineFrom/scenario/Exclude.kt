package me.tbsten.cream.ksp.feature.combineFrom.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.INT
import me.tbsten.cream.CombineFrom
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private val EXCLUDE = AnnotationSpec.builder(CombineFrom.Exclude::class).build()

internal fun excludeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "excludedProperty" to
            combineFrom(
                dataClass("Target", Prop("name"), Prop("count", INT, paramAnnotation = EXCLUDE), Prop("extra", INT)),
                dataClass("SourceA", Prop("name"), Prop("count", INT)),
                dataClass("SourceB", Prop("extra", INT)),
            ),
        "excludeNoEffect" to
            combineFrom(
                dataClass("Target", Prop("name"), Prop("targetOnly", INT, paramAnnotation = EXCLUDE), Prop("extra", INT)),
                dataClass("SourceA", Prop("name")),
                dataClass("SourceB", Prop("extra", INT)),
            ),
    )
