package me.tbsten.cream.ksp.feature.combineTo.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.INT
import me.tbsten.cream.CombineTo
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private val EXCLUDE = AnnotationSpec.builder(CombineTo.Exclude::class).build()

internal fun excludeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "excludedProperty" to
            combinedInto(
                dataClass("Target", Prop("name"), Prop("count", INT), Prop("extra", INT)),
                dataClass("SourceA", Prop("name"), Prop("count", INT, paramAnnotation = EXCLUDE)),
                dataClass("SourceB", Prop("extra", INT)),
            ),
        "excludeNoEffect" to
            combinedInto(
                dataClass("Target", Prop("name"), Prop("extra", INT)),
                dataClass("SourceA", Prop("name"), Prop("sourceOnly", INT, paramAnnotation = EXCLUDE)),
                dataClass("SourceB", Prop("extra", INT)),
            ),
    )
